package shorty.com.urlshortener.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    @Column(name = "user_email",nullable=false,unique = true,length = 254)
    public String email;
    @Column(name="user_name", nullable=false,length=50)
    public String name;
    @Column(name="user_lastname", nullable = false, length = 60)
    public String lastname;
    @Column(name="user_created_at",nullable = false)
    public LocalDateTime createdAt;
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    public List<Link> links;

    public User(){}
    public User(UUID id, String email, String name, String lastname, LocalDateTime createdAt, List<Link> links) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.createdAt = createdAt;
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getName(), user.getName()) && Objects.equals(getLastname(), user.getLastname()) && Objects.equals(getCreatedAt(), user.getCreatedAt()) && Objects.equals(getLinks(), user.getLinks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getName(), getLastname(), getCreatedAt(), getLinks());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }


}
