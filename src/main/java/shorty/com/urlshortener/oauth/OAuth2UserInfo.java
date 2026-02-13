package shorty.com.urlshortener.oauth;

import java.util.Map;

/**
 * Clase abstracta que define la estructura para extraer
 * información del usuario de diferentes proveedores OAuth2
 * Cada proveedor (Google, Facebook, etc.) tendrá su propia implementación
 */
public abstract class OAuth2UserInfo {
    // Mapa con los atributos que devuelve el proveedor OAuth2
    protected Map<String, Object> attributes;
    /**
     * Constructor que recibe los atributos del proveedor
     * @param attributes Mapa con los datos del usuario
     */
    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return Todos los atributos
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * @return ID del usuario en el proveedor
     */
    public abstract String getId();

    /**
     * @return Nombre del usuario
     */
    public abstract String getName();

    /**
     * @return Email del usuario
     */
    public abstract String getEmail();

    /**
     * @return URL de la imagen de perfil
     */
    public abstract String getImageUrl();
}
