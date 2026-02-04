package shorty.com.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shorty.com.urlshortener.entity.Link;

@Repository
public interface LinkRepository extends JpaRepository<Link,String> {
}
