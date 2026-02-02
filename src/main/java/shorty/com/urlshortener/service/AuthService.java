package shorty.com.urlshortener.service;

import shorty.com.urlshortener.DTO.UserDto;

public interface AuthService {
    public UserDto create(UserDto userDto);

}
