package shorty.com.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shorty.com.urlshortener.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Buscar un usuario por el email
     * @param mail es el email de usuario
     * @return Optional con el usuario si existe, null si no
     */
    Optional<User> findByEmail(String mail);

    /**
     * Verifica si existe el usuario con el email
     * @param mail El email a verificar
     * @return true si existe, false si no
     */
    Boolean existsByEmail(String mail);
}
