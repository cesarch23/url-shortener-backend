package shorty.com.urlshortener.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import shorty.com.urlshortener.entity.UserPrincipal;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Proveedor de tokens JWT usando la librería Auth0
 * Responsable de crear y validar tokens JWT
 * @Component: Marca esta clase como un bean de Spring
 * @RequiredArgsConstructor: Lombok genera constructor con dependencias
 * @Slf4j: Lombok proporciona un logger
 */
@Component
@Slf4j
public class JwtService {

    private int expirationMinutes;
    private Algorithm ALGORITHM;
    public JwtService(
            @Value("${app.jwt.expiration-minutes}") int expirationMinutes,
            @Value("${app.jwt.secret}") String SECRET_kEY
    ){
        this.expirationMinutes = expirationMinutes;
        this.ALGORITHM = Algorithm.HMAC256(SECRET_kEY);
    }

    public String createToken(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return JWT
                .create()
                .withSubject(userPrincipal.getId().toString())
                .withIssuer("api-shortener")
                .withClaim("email", userPrincipal.getEmail())//TODO ELIMINAR, EXPONIENDO DATOS
                .withClaim("role","USER")//TODO PERSONALIZAR ESTO PARA ADMIN Y USER
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationMinutes)))
                .sign(ALGORITHM);
    }
    /**
     * Extrae el ID del usuario de un token JWT
     * @param token Token JWT
     * @return UUID ID del usuario
     */
    public UUID getUserIdFromToken(String token){
        return  UUID.fromString(
                JWT
                        .require(ALGORITHM)
                        .build()
                        .verify(token)
                        .getSubject()
        );
    }
    public boolean isValidToken(String token){
        try{
            JWT.require(ALGORITHM)
                    .build()
                    .verify(token);
            return true;

        }catch (JWTVerificationException ex)
        {
            log.error(ex.getMessage());
            return false;
        }
          /* se puede merjorar
        } catch (SignatureVerificationException ex) {
            log.error("Firma JWT inválida");
        } catch (TokenExpiredException ex) {
            log.error("Token JWT expirado");
        } catch (JWTDecodeException ex) {
            log.error("Token JWT malformado");
        }
        */
    }
}
