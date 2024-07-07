package org.api.rest;

import lombok.SneakyThrows;
import org.api.domain.model.ResponseDTO.UpdateUserDTO;
import org.api.domain.model.ResponseDTO.UsersDTO;
import org.api.domain.model.Users;
import org.api.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    @Autowired
    UsersService usersService;

    @SneakyThrows
    @PostMapping("/register")
    public ResponseEntity createUser(@RequestBody UsersDTO newUser, Users users) {

        UsersDTO createdUser = usersService.createNewUser(newUser, users);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id_user}")
    public ResponseEntity updateUser(@PathVariable Integer id_user, @RequestBody UpdateUserDTO updateUserDTO) {

        if (id_user == null) {
            System.out.println("Usuario nao encontrado!");
            return ResponseEntity.badRequest().build();
        }

        Users account = usersService.findById(id_user);

        UpdateUserDTO updatedAccount = usersService.toUpdateUser(account, id_user, updateUserDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping("/u/{id_user}")
    public ResponseEntity <UsersDTO> getUserById(@PathVariable Integer id_user) {
        UsersDTO personUser = usersService.getUserById(id_user);

        if(personUser == null) {
            ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(personUser);
    }

}
