package shorty.com.urlshortener.serviceImpls;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shorty.com.urlshortener.entity.User;
import shorty.com.urlshortener.entity.UserPrincipal;
import shorty.com.urlshortener.enums.AuthProvider;
import shorty.com.urlshortener.oauth.OAuth2UserInfo;
import shorty.com.urlshortener.oauth.OAuth2UserInfoFactory;
import shorty.com.urlshortener.repository.UserRepository;

import javax.naming.AuthenticationException;
import java.util.Optional;

/**
 * Servicio personalizado que procesa la información del usuario
 * después de una autenticación OAuth2 exitosa
 */
@Service
public class CustomOAuth2UserServiceImp extends DefaultOAuth2UserService {

    private  final UserRepository userRepository;

    public CustomOAuth2UserServiceImp(UserRepository userRepository){
        this.userRepository = userRepository;

    }
    /**
     * Método que se ejecuta después de que el usuario se autentica con OAuth2
     * @param oAuth2UserRequest Contiene información sobre la petición OAuth2
     * @return OAuth2User El usuario autenticado
     * @throws OAuth2AuthenticationException Si hay un error en la autenticación
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        //obtener infor desde el proveedor usando oauth2
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try{
            return processOAuth2User(oAuth2UserRequest,oAuth2User);

        }catch (Exception exception){
            throw new OAuth2AuthenticationException(exception.getMessage());
        }
    }
    /**
     * Procesa la información del usuario OAuth2 y lo registra o actualiza en la BD
     * @param oAuth2UserRequest Petición OAuth2 con información del proveedor
     * @param oAuth2User Información del usuario devuelta por el proveedor
     * @return OAuth2User Usuario procesado y listo para autenticación
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest,
                                         OAuth2User oAuth2User) {
        // Obtenemos el ID de registro del proveedor (ej: "google")
        String registrationId = oAuth2UserRequest.getClientRegistration()
                .getRegistrationId();

        // Usamos el factory para obtener la información específica del proveedor
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        // Verificamos que el email no esté vacío
        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException(
                    "Email no encontrado del proveedor OAuth2"
            );
        }

        // Buscamos si el usuario ya existe en nuestra base de datos
        Optional<User> userOptional = userRepository.findByEmail( oAuth2UserInfo.getEmail() );
        User user;
        if(userOptional.isPresent()) {
            // El usuario ya existe
            user = userOptional.get();

            // Verificamos que esté usando el mismo proveedor
            if(!user.getAuthProvider().equals(
                    AuthProvider.valueOf(registrationId.toUpperCase())
            )) {
                // Si intentó usar un proveedor diferente, lanzamos error
                throw new OAuth2AuthenticationException(
                        "Parece que ya estás registrado con " + user.getAuthProvider() +
                                ". Por favor usa tu cuenta de " + user.getAuthProvider()
                );
            }
            // Actualizamos la información del usuario existente
            //TODO VERIFICAR SI CAMBIO DATOS Y ACTUALIZAR
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            // El usuario no existe, lo registramos como nuevo
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        // Creamos y devolvemos el UserPrincipal con los datos del usuario
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    /**
     * Registra un nuevo usuario en la base de datos
     * @param oAuth2UserRequest Petición OAuth2
     * @param oAuth2UserInfo Información del usuario del proveedor
     * @return User Usuario guardado en la BD
     */
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest,
                                 OAuth2UserInfo oAuth2UserInfo) {
        // Construimos el nuevo usuario usando el patrón Builder
        User user = User.builder()
                .authProvider(AuthProvider.valueOf(
                        oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()
                ))
                //id del usuario en provedor
                .providerId(oAuth2UserInfo.getId())
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .emailVerified(true)
                .role("USER")
                .build();

        // TODO Guardamos el usuario en la base de datos, lastname es optional
        return userRepository.save(user);
    }

    /**
     * Actualiza la información de un usuario existente
     * @param existingUser Usuario existente en la BD
     * @param oAuth2UserInfo Nueva información del proveedor OAuth2
     * @return User Usuario actualizado
     */
    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        // Actualizamos el nombre (puede haber cambiado en Google)
        existingUser.setName(oAuth2UserInfo.getName());
        // Guardamos los cambios en la BD
        return userRepository.save(existingUser);
    }

}
