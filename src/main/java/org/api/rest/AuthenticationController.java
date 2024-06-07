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

        return ResponseEntity.ok(new ResponseTokenDTO(token));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<String> updatePassword(@PathVariable Integer id, @RequestBody @Valid PasswordDTO passwordDTO) {
        Users user = usersService.findById(id);

        user.setPassword(passwordDTO.getPassword());
        passwordService.toUpdatePassword(user, passwordDTO, id);

        return ResponseEntity.ok("Password updated successfully!");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody RecoveryPasswordDTO recoveryPasswordDTO, String email, Users user) {

        passwordService.recoverPassword(user, recoveryPasswordDTO, email);

        return ResponseEntity.ok("Please check your email. The new password has been sent!");
    }

}