package shorty.com.urlshortener.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
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

    public Link (){}

    public Link(UUID id, String originalUrl, String code, LocalDateTime createdAt, LocalDateTime expiredAt, User user) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.code = code;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link link)) return false;
        return Objects.equals(id, link.id) && Objects.equals(originalUrl, link.originalUrl) && Objects.equals(code, link.code) && Objects.equals(createdAt, link.createdAt) && Objects.equals(expiredAt, link.expiredAt) && Objects.equals(user, link.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originalUrl, code, createdAt, expiredAt, user);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
