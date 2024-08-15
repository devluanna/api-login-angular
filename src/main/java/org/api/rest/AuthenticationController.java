package org.api.rest;

import jakarta.validation.Valid;
import org.api.domain.model.ResponseDTO.AuthenticationDTO;
import org.api.domain.model.ResponseDTO.PasswordDTO;
import org.api.domain.model.ResponseDTO.RecoveryPasswordDTO;
import org.api.domain.model.ResponseDTO.ResponseTokenDTO;
import org.api.domain.model.Users;
import org.api.infra.security.TokenService;
import org.api.service.PasswordService;
import org.api.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager manager;

    @Autowired
    TokenService tokenService;

    @Autowired
    PasswordService passwordService;

    @Autowired
    UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO oauth, Users users) {
        var auth = new UsernamePasswordAuthenticationToken(oauth.identity(), oauth.password());
        var authentication = manager.authenticate(auth);

        var token = tokenService.generateToken((Users) authentication.getPrincipal());

        Users authenticatedUser = (Users) authentication.getPrincipal();
        Integer userId = authenticatedUser.getId_user();


        passwordService.checkAndUpdatePasswordStatus();


        return ResponseEntity.ok(new ResponseTokenDTO(token, userId));
    }



    @PutMapping("/password/{id_user}")
    public ResponseEntity<String> updatePassword(@PathVariable Integer id_user, @RequestBody @Valid PasswordDTO passwordDTO) {
        try {
            Users user = usersService.findById(id_user);
            user.setPassword(passwordDTO.getPassword());
            user.setConfirmPassword(passwordDTO.getConfirmPassword());

            passwordService.toUpdatePassword(user, passwordDTO, id_user);
            return ResponseEntity.ok("Password updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();  // Log the error details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody RecoveryPasswordDTO recoveryPasswordDTO, String email, Users user) {

        passwordService.recoverPassword(user, recoveryPasswordDTO, email);

        return ResponseEntity.ok("Please check your email. The new password has been sent!");
    }

}