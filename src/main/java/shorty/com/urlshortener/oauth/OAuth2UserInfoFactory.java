package shorty.com.urlshortener.oauth;


import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import shorty.com.urlshortener.enums.AuthProvider;

import java.util.Map;

/**
 * Factory que crea la instancia correcta de OAuth2UserInfo
 * según el proveedor de autenticación
 */
public class OAuth2UserInfoFactory {
    /**
     * Crea el objeto OAuth2UserInfo apropiado según el proveedor
     * @param registrationId Nombre del proveedor (google, facebook, etc.)
     * @param attributes Atributos del usuario del proveedor
     * @return Instancia de OAuth2UserInfo específica del proveedor
     * @throws OAuth2AuthenticationException Si el proveedor no está soportado
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                   Map<String, Object> attributes) {
        // Verificamos si el proveedor es Google
        if(registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            // Devolvemos la implementación específica de Google
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            // Si el proveedor no está soportado, lanzamos excepción
            throw new OAuth2AuthenticationException(
                    "Login con " + registrationId + " no está soportado aún."
            );
        }
        // Aquí podrías agregar más proveedores:
        // else if(registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.toString())) {
        //     return new FacebookOAuth2UserInfo(attributes);
        // }
    }
}
