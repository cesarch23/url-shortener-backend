package shorty.com.urlshortener.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shorty.com.urlshortener.entity.Link;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkRepository extends JpaRepository<Link,String> {

     Page<Link> findAllByUserId(UUID userId, Pageable pageable);

     Optional<Link> findByCode(String code);
}
