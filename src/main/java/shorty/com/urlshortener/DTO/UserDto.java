package shorty.com.urlshortener.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import shorty.com.urlshortener.entity.Link;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserDto {
    public UUID userId;
    public String email;
    public String name;
    public String lastname;
    public List<Link> links;

    UserDto(){}

    public UserDto(UUID userId, String email, String name, String lastname, List<Link> links) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.links = links;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}

