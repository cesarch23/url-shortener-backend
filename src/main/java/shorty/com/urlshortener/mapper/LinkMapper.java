package shorty.com.urlshortener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import shorty.com.urlshortener.DTO.LinkDTO;
import shorty.com.urlshortener.DTO.ShortUrlRequest;
import shorty.com.urlshortener.entity.Link;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LinkMapper {

    /*
    public String longUrl;
    public LocalDateTime expiredDate;

    public UUID userId;
    * */
    @Mappings({
            @Mapping(target = "code",ignore = true),
            @Mapping(source = "longUrl", target = "originalUrl"),
            @Mapping(target = "createdAt",ignore = true),
            @Mapping(source = "expiredDate", target = "expiredAt"),
            @Mapping(source = "userId", target = "user.id")
    })
    Link toEntity (ShortUrlRequest shortUrlRequest);
    /*
    @Mappings({
            @Mapping(source = "code", target = "shortCode"),
            @Mapping(source = "originalUrl", target = "longUrl"),
            @Mapping(source = "createdAt", target = "createdDate"),
            @Mapping(source = "expiredAt", target = "expiredDate"),
            @Mapping(source = "id", target = "userId",ignore = true)
    })
    LinkDTO toDTO (Link link);
    List<LinkDTO> toListDto (List<Link> links);*/

    /*
    *
    * public String longUrl;
    public String shortCode;
    public LocalDateTime createdDate; //YYYY-MM-DD HH:MI:SS
    public LocalDateTime expiredDate;
    * */

    /*
    *
    *  private String code;
    private String originalUrl;
    private LocalDateTime createdAt; //YYYY-MM-DD HH:MI:SS
    private LocalDateTime expiredAt;
    @JoinColumn(
            name="user_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_link_user"),
            insertable = true,//testear en false para ambos
            updatable = false
    )
    private User user;
    * */


}
