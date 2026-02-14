package shorty.com.urlshortener.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shorty.com.urlshortener.DTO.LinkDTO;
import shorty.com.urlshortener.DTO.ShortUrlRequest;
import shorty.com.urlshortener.serviceImpls.ShortenerServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
public class LinkController {

    private final ShortenerServiceImpl shortenerService;


    public LinkController(ShortenerServiceImpl shortenerService) {
        this.shortenerService = shortenerService;
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkDTO> shortLongUrl(@RequestBody ShortUrlRequest shortUrlRequest){
        return new ResponseEntity<>(shortenerService.shortUrlGenerator(shortUrlRequest),HttpStatus.OK);
    }
    @GetMapping("/{id}/links")
    public ResponseEntity<Page<LinkDTO>> getLinkByUserId(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size
            ){
        return new ResponseEntity<>(shortenerService.getLinksByUserId(id,page,size), HttpStatus.OK);
    }

}
