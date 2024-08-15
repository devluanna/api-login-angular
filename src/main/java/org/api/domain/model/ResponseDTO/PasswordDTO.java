package org.api.domain.model.ResponseDTO;

public record PasswordDTO(String password, String confirmPassword) {
    public String getPassword() {
        return password;
    }
    public String getConfirmPassword() {return confirmPassword;
    }

}
