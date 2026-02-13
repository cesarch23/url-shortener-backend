package shorty.com.urlshortener.exeption;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import shorty.com.urlshortener.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import shorty.com.urlshortener.security.JwtService;
import shorty.com.urlshortener.utils.CookieUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static shorty.com.urlshortener.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;

    //CONFIGURACION DE APP PROPERTIES
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final List<String> authorizedRedirectUris;
    public OAuth2AuthenticationSuccessHandler(
            JwtService jwtService,
            HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
            @Value("${app.oauth2.authorized-redirect-uris}")
            List<String> authorizedRedirectUris) {
        this.jwtService = jwtService;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.authorizedRedirectUris = authorizedRedirectUris;
        this.setDefaultTargetUrl("http://localhost:4200/oauth2/redirect");
    }


    /**
     * Método que se ejecuta cuando la autenticación OAuth2 es exitosa
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param authentication Objeto de autenticación con datos del usuario
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        // Determinamos la URL a donde redirigir
        String targetUrl = determineTargetUrl(request, response, authentication);

        // Si la respuesta ya fue enviada, no podemos redirigir
        if (response.isCommitted()) {
            logger.debug("La respuesta ya ha sido confirmada. No se puede redirigir a " + targetUrl);
            return;
        }

        // Limpiamos las cookies y atributos de autenticación
        clearAuthenticationAttributes(request, response);

        // Redirigimos al usuario a la URL objetivo
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determina la URL de redirección y agrega el token JWT
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param authentication Autenticación del usuario
     * @return String URL completa con el token JWT como parámetro
     */
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        // Intentamos obtener la URL de redirección de las cookies
        // Esta URL fue guardada cuando el usuario inició el flujo OAuth2
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        // Verificamos que la URL de redirección esté autorizada
        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            try {//TODO VERIFICAR Y MEJORAR
                throw new BadRequestException("Lo sentimos! Tenemos una URI de redirección no autorizada y no podemos continuar con la autenticación");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        //TODO AGREGAR AL PARAMETRO O URL EL PROVIDER NAME COMO GOOGLE GITHUB
        // Si no hay URL de redirección, usamos la URL por defecto
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());


        // Creamos el token JWT para el usuario autenticado
        String token = jwtService.createToken(authentication);
        System.out.println(" token creado: " + token);

        // Construimos la URL final agregando el token como query parameter
        // Ejemplo: http://localhost:4200/oauth2/redirect?token=eyJhbGc...

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    /**
     * Limpia las cookies y atributos de autenticación
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request,
                                                 HttpServletResponse response) {
        // Limpia atributos del padre (SimpleUrlAuthenticationSuccessHandler)
        super.clearAuthenticationAttributes(request);
        // Elimina las cookies de OAuth2
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * Verifica si una URI de redirección está autorizada
     * Esto previene ataques de redirección abierta
     * @param uri URI a verificar
     * @return boolean true si está autorizada, false si no
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        // Convertimos la URI a objeto URI
        URI clientRedirectUri = URI.create(uri);

        // Verificamos contra la lista de URIs autorizadas
        return this.authorizedRedirectUris
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    /// Convertimos cada URI autorizada a objeto URI
                    URI authorizedURI = URI.create(authorizedRedirectUri);

                    // Verificamos que el host y puerto coincidan
                    // Ejemplo: localhost:4200 debe coincidir exactamente
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }


}
