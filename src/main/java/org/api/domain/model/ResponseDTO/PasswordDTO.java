package org.api.domain.model.ResponseDTO;

public record PasswordDTO(String password) {
    public String getPassword() {
        return password;
    }

}
