package shorty.com.urlshortener.DTO;

import java.time.LocalDateTime;

public record ShortUrlResponse(
        String shortUrl,
        String originalUrl,
        LocalDateTime createdDate
) {
}
