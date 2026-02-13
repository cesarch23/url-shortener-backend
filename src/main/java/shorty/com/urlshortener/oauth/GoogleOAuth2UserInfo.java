package shorty.com.urlshortener.oauth;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    /**
     * Constructor que recibe los atributos del proveedor
     *
     * @param attributes Mapa con los datos del usuario
     */
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    /**
     * @return El ID del usuario en Google
     * Google usa "sub" (subject) como identificador único
     */
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    /**
     * @return El nombre completo del usuario
     * Google devuelve el nombre en el campo "name"
     */
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    /**
     * @return El email del usuario
     * Google devuelve el email en el campo "email"
     */
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    /**
     * @return URL de la foto de perfil
     * Google devuelve la foto en el campo "picture", aunque no esta implementado
     */
    @Override
    public String getImageUrl() {
        return null;
    }
}
