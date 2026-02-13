package shorty.com.urlshortener.DTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class LinkDTO {
    public String longUrl;
    public String shortCode;
    public LocalDateTime createdDate; //YYYY-MM-DD HH:MI:SS
    public LocalDateTime expiredDate;
    public LinkDTO(){}
}


