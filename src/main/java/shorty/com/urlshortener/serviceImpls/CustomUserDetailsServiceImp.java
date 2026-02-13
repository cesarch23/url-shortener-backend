package shorty.com.urlshortener.serviceImpls;


import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shorty.com.urlshortener.entity.User;
import shorty.com.urlshortener.entity.UserPrincipal;
import shorty.com.urlshortener.repository.UserRepository;

import java.util.UUID;

/**
 * Servicio personalizado para cargar detalles del usuario
 * Usado por Spring Security para autenticación
 */
@Service
public class CustomUserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por su email (username)
     * Usado por Spring Security en autenticación tradicional
     * @param email Email del usuario
     * @return UserDetails Detalles del usuario para Spring Security
     * @throws UsernameNotFoundException Si el usuario no existe
     * @Transactional: Ejecuta dentro de una transacción de BD
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow( ()-> new UsernameNotFoundException("usuario no encontrado con email: "+email) );
        return UserPrincipal.create(user);
    }
    /**
     * Carga un usuario por su ID
     * Usado en el filtro JWT para cargar el usuario autenticado
     * @param id ID del usuario
     * @return UserDetails Detalles del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Transactional
    public UserDetails loadUserById(UUID id) {
        // Buscamos el usuario en la base de datos por ID
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con id: " + id)
                );

        // Convertimos el User a UserPrincipal
        return UserPrincipal.create(user);
    }

    @Transactional
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("usuario no encontrado con email: "+email));
    }
    @Transactional
    public User findById(UUID id) {
        // Buscamos el usuario en la base de datos por ID
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con id: " + id)
                );

    }

}
