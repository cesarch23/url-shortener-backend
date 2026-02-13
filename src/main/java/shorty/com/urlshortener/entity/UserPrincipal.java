package shorty.com.urlshortener.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

/**
 * Clase que representa al usuario autenticado en Spring Security
 * Implementa UserDetails (para autenticación tradicional)
 * e OAuth2User (para autenticación OAuth2)
 */
@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private UUID id;
    private String email;
    private String name;
    //roles del usuario
    private Collection<? extends GrantedAuthority> authorities;
    //atributos adicionales de oauth
    private Map<String, Object> attributes;

    public UserPrincipal(UUID id, String email, String name, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    /**
     * Crea un UserPrincipal a partir de un usuario de la base de datos
     * @param user El usuario de la BD
     * @return UserPrincipal con los datos del usuario
     */
    public static UserPrincipal create(User user){
        //TODO AQUI QUITE ROLE_
        List<GrantedAuthority> authorities  =
                Collections.singletonList( new SimpleGrantedAuthority( "ROLE_".concat(user.getRole().toUpperCase()) ));
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getName(),
                authorities,null
        );
    }
    /**
     * Crea un UserPrincipal con atributos OAuth2
     * @param user El usuario de la BD
     * @param attributes Atributos de OAuth2 (datos de Google)
     * @return UserPrincipal completo
     */
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        return new UserPrincipal(
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getName(),
                userPrincipal.getAuthorities(),
                attributes  // Incluye atributos de OAuth2
        );
    }

    /**
     * @return
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * @return El email como nombre de usuario
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * @return true, la cuenta nunca expira
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * @return
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    /**
     * @param name
     * @param <A>
     * @return
     */
    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }
}
