package shorty.com.urlshortener.utils;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

/**
 * Utilidad para trabajar con cookies HTTP
 * Proporciona métodos para crear, leer, eliminar y serializar cookies
 */
public class CookieUtils {

    /**
     * Obtiene una cookie específica de la petición HTTP
     * @param request Petición HTTP
     * @param name Nombre de la cookie a buscar
     * @return Optional con la cookie si existe, vacío si no
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        // Obtenemos todas las cookies de la petición
        Cookie[] cookies = request.getCookies();

        // Verificamos que existan cookies
        if (cookies != null && cookies.length > 0) {
            // Iteramos sobre todas las cookies
            for (Cookie cookie : cookies) {
                // Si encontramos la que buscamos, la devolvemos
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        // Si no encontramos la cookie, devolvemos Optional vacío
        return Optional.empty();
    }

    /**
     * Agrega una nueva cookie a la respuesta HTTP
     * @param response Respuesta HTTP
     * @param name Nombre de la cookie
     * @param value Valor de la cookie
     * @param maxAge Tiempo de vida en segundos
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // Creamos una nueva cookie
        Cookie cookie = new Cookie(name, value);
        // Establecemos el path (disponible en toda la aplicación)
        cookie.setPath("/");
        // HttpOnly: Previene acceso desde JavaScript (seguridad XSS)
        cookie.setHttpOnly(true);
        // Tiempo de vida de la cookie
        cookie.setMaxAge(maxAge);
        // Agregamos la cookie a la respuesta
        response.addCookie(cookie);
    }

    /**
     * Elimina una cookie existente
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param name Nombre de la cookie a eliminar
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        // Obtenemos todas las cookies
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            // Buscamos la cookie a eliminar
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    // Para eliminar una cookie, establecemos su valor vacío
                    cookie.setValue("");
                    // Path debe coincidir con el original
                    cookie.setPath("/");
                    // MaxAge = 0 indica que debe eliminarse inmediatamente
                    cookie.setMaxAge(0);
                    // Enviamos la cookie "eliminada" en la respuesta
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * Serializa un objeto a String Base64
     * Útil para guardar objetos complejos en cookies
     * @param object Objeto a serializar
     * @return String Objeto serializado en Base64
     */
    public static String serialize(Object object) {
        // SerializationUtils convierte el objeto a bytes
        // Base64 codifica los bytes a una cadena segura para cookies
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * Deserializa un objeto desde una cookie
     * @param cookie Cookie que contiene el objeto serializado
     * @param cls Clase del objeto a deserializar
     * @return T Objeto deserializado
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        // Decodificamos el valor Base64 de la cookie
        // SerializationUtils reconstruye el objeto desde los bytes
        // Hacemos cast al tipo especificado
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
