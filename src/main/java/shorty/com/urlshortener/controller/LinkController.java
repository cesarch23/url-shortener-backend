package shorty.com.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shorty.com.urlshortener.DTO.LinkDTO;
import shorty.com.urlshortener.DTO.ShortUrlRequest;
import shorty.com.urlshortener.serviceImpls.ShortenerServiceImpl;

@RestController
@RequestMapping("api/v1/links")
public class LinkController {

    private final ShortenerServiceImpl shortenerService;


    public LinkController(ShortenerServiceImpl shortenerService) {
        this.shortenerService = shortenerService;
    }

    @PostMapping
    public ResponseEntity<LinkDTO> shortLongUrl(@RequestBody ShortUrlRequest shortUrlRequest){
        return new ResponseEntity<>(shortenerService.shortUrlGenerator(shortUrlRequest),HttpStatus.OK);
    }
}
