package shorty.com.urlshortener.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import shorty.com.urlshortener.serviceImpls.CustomUserDetailsServiceImp;

import java.io.IOException;
import java.util.UUID;


/**
 * Filtro que intercepta cada petición HTTP para validar el token JWT
 * OncePerRequestFilter: Garantiza que el filtro se ejecute una sola vez por petición
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsServiceImp customUserDetailsServiceImp;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsServiceImp customUserDetailsServiceImp) {
        this.jwtService = jwtService;
        this.customUserDetailsServiceImp = customUserDetailsServiceImp;

    }


    /**
     * Método que se ejecuta en cada petición HTTP
     * @param request Petición HTTP entrante
     * @param response Respuesta HTTP
     * @param filterChain Cadena de filtros de Spring Security
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extraemos el token JWT del header Authorization
            String jwt = getJwtFromRequest(request);

            // Verificamos que el token existe y es válido
            if (StringUtils.hasText(jwt) && jwtService.isValidToken(jwt)) {
                // Extraemos el ID del usuario del token
                UUID userId = jwtService.getUserIdFromToken(jwt);

                // Cargamos los detalles completos del usuario desde la BD
                UserDetails userDetails = customUserDetailsServiceImp.loadUserById(userId);

                // Creamos el objeto de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,        // Principal (usuario)
                                null,               // Credentials (no necesarias con JWT)
                                userDetails.getAuthorities()  // Roles/permisos
                        );

                // Agregamos detalles adicionales de la petición HTTP
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Establecemos la autenticación en el contexto de seguridad
                // Esto marca al usuario como autenticado para esta petición
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("JW FILTER "+ authentication);
            }
        } catch (Exception ex) {
            // Si hay algún error, lo logueamos
            // No lanzamos excepción para no bloquear la petición
            log.error("No se pudo establecer la autenticación del usuario en el contexto de seguridad", ex);
        }

        // Continuamos con la cadena de filtros
        // Esto es crucial: sin esto, la petición no continuaría
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     * @param request Petición HTTP
     * @return String Token JWT sin el prefijo "Bearer " o null si no existe
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // Obtenemos el valor del header Authorization
        String bearerToken = request.getHeader("Authorization");

        // Verificamos que existe y comienza con "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Retornamos el token sin el prefijo "Bearer "
            // substring(7) elimina los primeros 7 caracteres ("Bearer ")
            return bearerToken.substring(7);
        }
        // Si no hay token o no tiene el formato correcto, retornamos null
        return null;
    }
}
