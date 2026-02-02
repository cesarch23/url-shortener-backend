package shorty.com.urlshortener.DTO;

import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public class LinkDTO {
    public UUID linkId;
    public String longUrl;
    public String shortCode;
    public LocalDateTime createdDate; //YYYY-MM-DD HH:MI:SS
    public LocalDateTime expiredDate;
}


