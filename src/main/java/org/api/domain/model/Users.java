package org.api.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import org.api.domain.model.ResponseDTO.UsersDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

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
    private Status status;
    private ProfileRole role;

    public Users(String first_name, String last_name, String email, String identity, String password, ProfileRole role) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.identity = identity;
        this.password = password;
        this.role = role;
        this.status = Status.valueOf("AVAILABLE");
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