package org.api.service;

import org.api.domain.model.ResponseDTO.UpdateUserDTO;
import org.api.domain.model.ResponseDTO.UsersDTO;
import org.api.domain.model.Users;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {
    UsersDTO createNewUser(UsersDTO newUser, Users users);

    Users findById(Integer id_user);

    UpdateUserDTO toUpdateUser(Users users, Integer id_user, UpdateUserDTO updateUserDTO);

    UsersDTO getUserById(Integer id_user);
}
