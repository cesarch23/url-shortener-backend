package shorty.com.urlshortener.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Anotación personalizada para inyectar el usuario actual
 * Simplifica el acceso al usuario autenticado en los controllers
 *
 * En lugar de:
 * @AuthenticationPrincipal UserPrincipal user
 *
 * Podemos usar:
 * @CurrentUser UserPrincipal user
 *
 * @Target: Dónde se puede usar (parámetros y tipos)
 * @Retention: Se mantiene en runtime
 * @Documented: Aparece en JavaDoc
 * @AuthenticationPrincipal: Delegamos a esta anotación de Spring
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
