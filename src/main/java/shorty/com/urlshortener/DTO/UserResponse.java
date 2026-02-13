package shorty.com.urlshortener.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    // ID del usuario
    private UUID id;
    // Nombre completo
    private String name;
    // Email
    private String email;
    // URL de la imagen de perfil
    private String rol;
    // Proveedor de autenticación (GOOGLE, LOCAL, etc.)
    private String provider;
}
