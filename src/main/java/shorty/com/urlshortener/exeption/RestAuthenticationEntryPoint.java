package shorty.com.urlshortener.exeption;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Punto de entrada para manejar errores de autenticación
 * Se ejecuta cuando un usuario no autenticado intenta acceder a un recurso protegido
 *
 * @Slf4j: Lombok proporciona el logger automáticamente
 */
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Método que se ejecuta cuando hay un error de autenticación
     * @param request       Petición HTTP
     * @param response      Respuesta HTTP
     * @param authException Excepción de autenticación que ocurrió
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // Logueamos el error
        log.error("Respondiendo con error de no autorizado. Mensaje - {}", authException.getMessage());

        // Enviamos respuesta HTTP 401 (No autorizado)
        // Incluimos el mensaje de la excepción
        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,  // Código 401
                authException.getLocalizedMessage()                // Mensaje de error
        );

    }
}
