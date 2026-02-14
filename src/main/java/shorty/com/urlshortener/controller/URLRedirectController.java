package shorty.com.urlshortener.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shorty.com.urlshortener.DTO.LongUrlResponse;
import shorty.com.urlshortener.serviceImpls.ShortenerServiceImpl;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/links")
public class URLRedirectController {

    private final ShortenerServiceImpl shortenerService;

    public URLRedirectController(ShortenerServiceImpl shortenerService) {
        this.shortenerService = shortenerService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getLongUrl(
            @PathVariable String shortCode ){
        try
        {
            LongUrlResponse response = this.shortenerService.getLongUrl(shortCode);
            if(Objects.isNull( response ) || Objects.isNull( response.LongUrl() ) ){
                return ResponseEntity
                        .notFound()
                        .build();

            }
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .header("Location",response.LongUrl())
                    .header("Cache-Control","private, max-age=90")
                    .header("X-Robots-Tag","noindex")
                    .build();
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
