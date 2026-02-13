package shorty.com.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shorty.com.urlshortener.DTO.UserResponse;
import shorty.com.urlshortener.entity.User;
import shorty.com.urlshortener.entity.UserPrincipal;
import shorty.com.urlshortener.security.CurrentUser;
import shorty.com.urlshortener.serviceImpls.CustomUserDetailsServiceImp;

/**
 * Controlador para endpoints de autenticación
 * @RestController: Combina @Controller y @ResponseBody
 * @RequestMapping: Prefijo de todas las rutas (/api/auth)
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    // Repositorio para acceder a usuarios
    private final CustomUserDetailsServiceImp userDetailsService;

    public AuthController(CustomUserDetailsServiceImp userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Obtiene la información del usuario actual autenticado
     * @param userPrincipal Usuario autenticado (inyectado automáticamente)
     * @return ResponseEntity con los datos del usuario
     *
     * @GetMapping: Define una ruta GET
     * @PreAuthorize: Requiere que el usuario tenga el rol USER
     * @CurrentUser: Anotación personalizada para inyectar el usuario actual
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        // Buscamos el usuario completo en la base de datos
        User user = userDetailsService.findById(userPrincipal.getId());

        // Construimos el DTO de respuesta
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getAuthProvider().toString())
                .rol(user.getRole().toUpperCase())
                .build();

        // Retornamos la respuesta con código HTTP 200
        return ResponseEntity.ok(userResponse);
    }


}
