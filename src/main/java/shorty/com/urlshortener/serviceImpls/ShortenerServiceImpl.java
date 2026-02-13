package shorty.com.urlshortener.serviceImpls;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shorty.com.urlshortener.DTO.LinkDTO;
import shorty.com.urlshortener.DTO.LongUrlResponse;
import shorty.com.urlshortener.DTO.ShortUrlRequest;
import shorty.com.urlshortener.entity.Link;
import shorty.com.urlshortener.mapper.LinkMapper;
import shorty.com.urlshortener.repository.LinkRepository;
import shorty.com.urlshortener.service.ShortenerService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;


@Service
public class ShortenerServiceImpl implements ShortenerService {

    private final String BASE62;
    private final String SECRET_KEY;
    private LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    public ShortenerServiceImpl(
            @Value("${BASE62}") String BASE62,
            @Value("${SECRET_KEY_HMAC}") String SECRET_KEY,
            LinkRepository linkRepository,
            LinkMapper linkMapper
            ){
        this.BASE62 = BASE62;
        this.SECRET_KEY = SECRET_KEY;
        this.linkRepository = linkRepository;
        this.linkMapper = linkMapper;
    }

    @Override
    @Transactional
    public LinkDTO shortUrlGenerator(ShortUrlRequest shortUrlRequest)   {
        try {
            String shortCode = this.generateShortCode(shortUrlRequest,8);
            Link link = this.linkMapper.toEntity(shortUrlRequest);
            link.setCode(shortCode);
            link.setCreatedAt( LocalDateTime.now());
            Link newLink = this.linkRepository.save(link);
            //TODO obtner error cuando no se guarda en la base de datos, quiere decir que
            // hay colision o el usuario esta generando para la misma url otro codigo
            return LinkDTO.builder()
                    .shortCode(newLink.getCode())
                    .longUrl(newLink.getOriginalUrl())
                    .createdDate(newLink.getCreatedAt())
                    .expiredDate(newLink.getExpiredAt())
                    .build();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LongUrlResponse getLongUrlByCode(LongUrlResponse longUrlResponse) {
        
        return null;
    }


    //todo obtener original url en base al code

    //Todo obtner todas las urls de un cliente;

    public String generateShortCode(ShortUrlRequest shortUrlRequest, int shortCodeLength ) throws NoSuchAlgorithmException{
        try {
            byte[] hmac = this.hmacSha256( shortUrlRequest.getLongUrl(),this.SECRET_KEY );
            byte[] first8Bytes = Arrays.copyOfRange(hmac,0,8);
            BigInteger number = new BigInteger(1,first8Bytes);
            String base62 = toBase62(number);
            return base62.substring(0,Math.min(shortCodeLength,base62.length()));
        } catch (Exception e) {
            //este error seria al genera el hasmap
            throw new RuntimeException(e);
        }
    }
    public byte[] hmacSha256(String data, String key) throws Exception{
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
    public String toBase62(BigInteger number){
         StringBuilder result = new StringBuilder();
         while (number.compareTo(BigInteger.ZERO) > 0){
             BigInteger[] divmod = number.divideAndRemainder(BigInteger.valueOf(62));
             result.insert(0,this.BASE62.charAt(divmod[1].intValue()));
             number = divmod[0];
         }
         return result.toString();
    }
    public void main() throws NoSuchAlgorithmException {
        UUID userId  = UUID.randomUUID();
        String originalUrl = "https://www.google.com";

        ShortUrlRequest s = new ShortUrlRequest(originalUrl,userId,LocalDateTime.now());

        String code = this.generateShortCode(s,8);
        System.out.println("Código corto: " + code);
    }
}
