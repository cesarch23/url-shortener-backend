package shorty.com.urlshortener.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    @Column(name = "user_email",nullable=false,unique = true,length = 254)
    public String email;
    @Column(name="user_name", nullable=false,length=50)
    public String name;
    @Column(name="user_lastname", nullable = false, length = 60)
    public String lastname;
    @Column(name="user_created_at",nullable = false)
    public LocalDateTime createdAt;
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    public List<Link> links;
}
