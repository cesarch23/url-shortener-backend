package shorty.com.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shorty.com.urlshortener.exeption.OAuth2AuthenticationFailureHandler;
import shorty.com.urlshortener.exeption.OAuth2AuthenticationSuccessHandler;
import shorty.com.urlshortener.exeption.RestAuthenticationEntryPoint;
import shorty.com.urlshortener.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import shorty.com.urlshortener.security.JwtAuthenticationFilter;
import shorty.com.urlshortener.serviceImpls.CustomOAuth2UserServiceImp;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Servicio personalizado para OAuth2
    private final CustomOAuth2UserServiceImp customOAuth2UserService;
    // Manejador de éxito OAuth2
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    // Manejador de fallo OAuth2
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    // Repositorio de peticiones OAuth2
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    // Filtro de autenticación JWT
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomOAuth2UserServiceImp customOAuth2UserService,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
            HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Define la cadena de filtros de seguridad
     * Configura todas las reglas de seguridad de la aplicación
     * @param http Objeto para configurar seguridad HTTP
     * @return SecurityFilterChain Cadena de filtros configurada
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configuración CORS (permite peticiones desde el frontend)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Deshabilitamos CSRF porque usamos JWT (stateless)
                // CSRF es innecesario con autenticación basada en tokens
                .csrf(AbstractHttpConfigurer::disable)

                // Configuración de sesiones
                .sessionManagement(session ->
                        // STATELESS: No guardamos estado en el servidor
                        // Cada petición debe incluir el token JWT
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .formLogin(AbstractHttpConfigurer::disable) // Deshabilitamos el formulario de login por defecto
                .httpBasic(AbstractHttpConfigurer::disable)// Deshabilitamos HTTP Basic authentication
                .exceptionHandling(exceptions -> exceptions // Configuración de manejo de excepciones
                        // Punto de entrada para errores de autenticación
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                )

                // Configuración de autorización de peticiones
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación requerida)
                        .requestMatchers("/", "/error", "/favicon.ico", "/**/*.png",
                                "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html",
                                "/**/*.css", "/**/*.js")
                        .permitAll()

                        // Rutas de autenticación (públicas)
                        .requestMatchers("/api/v1/auth/**", "/oauth2/**","/api/v1/links/**").permitAll()
                        .requestMatchers("/api/v1/users/**").hasAnyRole("ADMIN","USER")

                        // Todas las demás rutas requieren autenticación
                        .anyRequest()
                        .authenticated()
                )

                // Configuración de OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        // Configuración del endpoint de autorización
                        .authorizationEndpoint(authorization -> authorization
                                // URL base para iniciar OAuth2
                                .baseUri("/oauth2/authorize")
                                // Repositorio personalizado (usa cookies)
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                        )
                        // Configuración del endpoint de redirección
                        .redirectionEndpoint(redirection -> redirection
                                // URL donde Google redirige después de autenticar
                                .baseUri("/login/oauth2/code/*")
                        )
                        // Configuración del servicio de información de usuario
                        .userInfoEndpoint(userInfo -> userInfo
                                // Servicio personalizado para procesar usuario OAuth2
                                .userService(customOAuth2UserService)
                        )
                        // Manejador cuando la autenticación es exitosa
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        // Manejador cuando la autenticación falla
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

        // Agregamos nuestro filtro JWT antes del filtro de autenticación estándar
        // Esto permite que el JWT sea validado en cada petición
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Construimos y retornamos la cadena de filtros
        return http.build();
    }

    /**
     * Configuración CORS (Cross-Origin Resource Sharing)
     * Permite que el frontend (en otro dominio) haga peticiones a la API
     * @return CorsConfigurationSource Configuración CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (frontend Angular)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers permitidos cambien auth to Auth..
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "content-type", "x-auth-token","*"));

        // Headers expuestos (visibles para el frontend)
        configuration.setExposedHeaders(List.of("x-auth-token"));

        // Permitir credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);

        // Tiempo de cache de la configuración CORS (1 hora)
        configuration.setMaxAge(3600L);

        // Aplicamos la configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
