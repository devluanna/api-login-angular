package org.api.domain.model.ResponseDTO;

public record AuthenticationDTO (String identity, String password) {

    public String identity() {
        return identity;
    }


    public String password() {
        return password;
    }
}
