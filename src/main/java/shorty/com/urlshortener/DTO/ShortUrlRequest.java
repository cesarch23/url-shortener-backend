package shorty.com.urlshortener.DTO;
import java.time.LocalDateTime;
import java.util.UUID;

public class ShortUrlRequest {
    public String longUrl;
    public LocalDateTime expiredDate;

    public UUID userId;

    public ShortUrlRequest(String longUrl, UUID userId,LocalDateTime expiredDate) {
        this.longUrl = longUrl;
        this.expiredDate = expiredDate;
        this.userId = userId;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
