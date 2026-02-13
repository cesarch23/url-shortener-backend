package shorty.com.urlshortener.exeption;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import shorty.com.urlshortener.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import shorty.com.urlshortener.utils.CookieUtils;

import java.io.IOException;

import static shorty.com.urlshortener.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * Manejador que se ejecuta cuando falla la autenticación OAuth2
 * Redirige al frontend con un mensaje de error
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    // Repositorio de cookies OAuth2
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    public OAuth2AuthenticationFailureHandler(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    /**
     * Método que se ejecuta cuando la autenticación OAuth2 falla
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param exception Excepción que causó el fallo
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        //TODO VERIFCAR ESTO DE SUPER
        // super.onAuthenticationFailure(request, response, exception);
        // Obtenemos la URL de redirección de las cookies
        // Si no existe, usamos "/" como URL por defecto
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        // Construimos la URL agregando el mensaje de error como parámetro
        // Ejemplo: http://localhost:4200/oauth2/redirect?error=Authentication failed
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        // Limpiamos las cookies de OAuth2
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // Redirigimos al usuario a la URL con el error
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


    /**
     * Método que se ejecuta cuando la autenticación OAuth2 falla
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param exception Excepción que causó el fallo
     */

}
