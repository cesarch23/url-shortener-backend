package shorty.com.urlshortener.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "links")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "link_id",unique = true)
    public UUID id;
    @Column(name = "link_original_url",length = 2000,nullable = false)
    public String originalUrl;
    @Column(name = "link_short_code",length = 6,nullable = false)
    public String code;
    @Column(name = "link_created_at",nullable = false)
    public LocalDateTime createdAt; //YYYY-MM-DD HH:MI:SS
    @Column(name = "link_expire_at",nullable = false)
    public LocalDateTime expiredAt;
    @Column(name="user_id",nullable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name="user_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_link_user"),
            insertable = false,
            updatable = false
    )
    public User user;



}
