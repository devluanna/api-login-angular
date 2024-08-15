package org.api.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
public class Users implements UserDetails{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id_user;

    private String first_name;
    private String last_name;
    private String email;
    private String identity;
    private String password;
    private String confirmPassword;
    private Status status;
    private ProfileRole role;
    private boolean firstAccessRequired;
    private boolean passwordIsCompliance;
    private SubStatus subStatus;
    private LocalDateTime createdDate;
    private LocalDateTime lastPasswordUpdateDate; // track last password update
    private LocalDateTime passwordExpirationDays; // password expiration period


    public Users(String first_name, String last_name, String email, String identity, String password, ProfileRole role) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.identity = identity;
        this.password = password;
        this.role = role;
        this.status = Status.valueOf("AVAILABLE");
        this.firstAccessRequired = true;
        this.passwordIsCompliance = false;
        this.subStatus = SubStatus.valueOf("IN_NON_COMPLIANCE");
        this.createdDate = LocalDateTime.now();
        this.lastPasswordUpdateDate = LocalDateTime.now();
        this.passwordExpirationDays = this.lastPasswordUpdateDate.plusDays(30);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == ProfileRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return identity;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }


}