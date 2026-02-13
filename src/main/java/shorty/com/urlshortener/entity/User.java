package shorty.com.urlshortener.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import shorty.com.urlshortener.enums.AuthProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_email",nullable=false,unique = true,length = 254)
    private String email;
    @Column(name = "user_email_verified",nullable = false)
    private Boolean emailVerified;
    @Column(name="user_name", nullable=false,length=50)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name="user_auth_provider",nullable = false)
    private AuthProvider authProvider;
    @Column(name="user_provider_id",nullable = true)
    private String providerId;

    @Column(name = "user_role",nullable = false,length = 12)
    private String role;

    @Column(name="user_lastname", nullable = true, length = 60)
    private String lastname;
    @Column(name="user_created_at",nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Link> links;

    @PrePersist()
    protected  void onCreate(){
        createdAt = LocalDateTime.now();
    }

    public User(){}

}
