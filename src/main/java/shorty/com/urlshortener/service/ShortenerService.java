package shorty.com.urlshortener.service;

import shorty.com.urlshortener.DTO.LinkDTO;
import shorty.com.urlshortener.DTO.LongUrlResponse;
import shorty.com.urlshortener.DTO.ShortUrlRequest;

import java.security.NoSuchAlgorithmException;

public interface ShortenerService {
    public LinkDTO shortUrlGenerator(ShortUrlRequest shortUrlRequest) throws NoSuchAlgorithmException;
    public LongUrlResponse getLongUrlByCode( LongUrlResponse longUrlResponse );
}
