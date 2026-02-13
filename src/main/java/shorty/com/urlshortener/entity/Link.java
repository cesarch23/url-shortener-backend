package shorty.com.urlshortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(
        name = "links",
        indexes = {
                @Index(name = "idx_expired_date", columnList = "link_expire_at"),
                @Index(name = "idx_user_id",columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_id_original_url",columnNames = {"user_id","link_original_url"})
        }
)
@Getter
@Setter
public class Link {
    @Id
    @Column(name = "link_short_code",length =11,nullable = false)//indexado auto
    private String code;
    @Column(name = "link_original_url",length = 2000,nullable = false)
    private String originalUrl;
    @Column(name = "link_created_at",nullable = false)
    private LocalDateTime createdAt; //YYYY-MM-DD HH:MI:SS
    @Column(name = "link_expire_at",nullable = false)
    private LocalDateTime expiredAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name="user_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_link_user"),
            insertable = true,//testear en false para ambos
            updatable = false
    )
    private User user;

    public Link (){}


}
