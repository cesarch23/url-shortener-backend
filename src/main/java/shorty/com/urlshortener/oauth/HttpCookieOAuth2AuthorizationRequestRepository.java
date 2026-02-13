package shorty.com.urlshortener.oauth;


import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import shorty.com.urlshortener.utils.CookieUtils;


/**
 * Repositorio personalizado que guarda las peticiones de autorización OAuth2 en cookies
 * Por defecto, Spring Security usa la sesión, pero esto no funciona bien con apps stateless
 *
 * ¿Por qué usar cookies?
 * - Permite una arquitectura stateless
 * - Funciona mejor con frontends separados (SPA)
 * - No requiere mantener sesiones en el servidor
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // Nombre de la cookie que guarda la petición de autorización
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    // Nombre de la cookie que guarda la URL de redirección
    //TODO ACTUALIZARA AL DEL VALOR DE ENV
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    // Tiempo de expiración de las cookies (3 minutos)
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    /**
     * Carga la petición de autorización desde las cookies
     * @param request Petición HTTP
     * @return OAuth2AuthorizationRequest La petición guardada o null
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        // Buscamos la cookie con la petición de autorización
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                // Si existe, deserializamos su contenido
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                // Si no existe, devolvemos null
                .orElse(null);
    }

    /**
     * Guarda la petición de autorización en cookies
     * @param authorizationRequest Petición de autorización a guardar
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     */
    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        // Si la petición es null, eliminamos las cookies
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }

        // Guardamos la petición de autorización serializada en una cookie
        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);

        // Obtenemos la URL de redirección de los parámetros de la petición
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);

        // Si existe, la guardamos en otra cookie
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS);
        }

    }

    /**
     * Elimina la petición de autorización
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @return OAuth2AuthorizationRequest La petición antes de eliminarla
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response) {
        // Simplemente cargamos la petición (no la eliminamos aquí)
        // La eliminación real se hace en removeAuthorizationRequestCookies()
        return this.loadAuthorizationRequest(request);
    }
    /**
     * Elimina las cookies relacionadas con la autorización OAuth2
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request,
                                                  HttpServletResponse response) {
        // Eliminamos la cookie de la petición de autorización
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        // Eliminamos la cookie de la URL de redirección
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
