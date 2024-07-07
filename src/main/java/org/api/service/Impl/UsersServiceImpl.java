package org.api.service.Impl;

import jakarta.transaction.Transactional;
import org.api.domain.model.ResponseDTO.UpdateUserDTO;
import org.api.domain.model.ResponseDTO.UsersDTO;
import org.api.domain.model.Users;
import org.api.domain.repository.UsersRepository;
import org.api.service.UsersService;
import org.api.utils.MailConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    MailConfig emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Users findById(Integer id_user) {
        return usersRepository.findById(id_user).orElseThrow(NoSuchElementException::new);
    }


    @Override
    @Transactional
    public UsersDTO getUserById(Integer id_user) {
        return usersRepository.findUserById(id_user);
    }

    //Método por criar o usuário no sistema.
    //Method for creating the user in the system.
    @Override
    @Transactional
    public UsersDTO createNewUser(UsersDTO newUser, Users users) {
        validateExistsEmail(newUser);

        Users userCreated = new Users(
                newUser.getFirst_name(), newUser.getLast_name(), newUser.getEmail(), newUser.getIdentity(),
                newUser.getPassword(), newUser.getRole()
        );


        String passwordUser = "12345678";
        String encryptedPassword = passwordEncoder.encode(passwordUser);
        userCreated.setPassword(encryptedPassword);

        userCreated.setIdentity(generateId());

        Users savedUser = usersRepository.save(userCreated);
        BeanUtils.copyProperties(savedUser, newUser);

        return newUser;
    }


    // Método responsável por atualizar as informações do usuário.
    // Method responsible for updating user information.
    @Override
    @Transactional
    public UpdateUserDTO toUpdateUser(Users users, Integer id_user, UpdateUserDTO updateUserDTO) {
        Users selectedUser = findById(id_user);

        savingNewUpdatesInTheField(users, selectedUser, updateUserDTO);

        Users savedUser = usersRepository.save(users);

        UpdateUserDTO updatedUser = new UpdateUserDTO();
        updatedUser.setFirst_name(savedUser.getFirst_name());
        updatedUser.setEmail(savedUser.getEmail());

        return updatedUser;
    }

    // Método responsável pela validação (Se o campo X não for atualizado, selecionará o atual) caso contrário, o campo será atualizado com sucesso.
    // Method responsible for validating (If field X is not updated, it will select the current one) if not, the field will be updated successfully.
    public void savingNewUpdatesInTheField(Users users, Users selectedUser, UpdateUserDTO updateUserDTO) {

        selectedUserFieldsDTO(selectedUser, updateUserDTO);

        if(selectedUser.getFirst_name() != null) {
            users.setFirst_name(selectedUser.getFirst_name());
        }

        if(selectedUser.getLast_name() != null) {
            users.setLast_name(selectedUser.getLast_name());
        }

        if(selectedUser.getEmail() != null) {
            users.setEmail(selectedUser.getEmail());
        }

        if(selectedUser.getStatus() != null) {
            users.setStatus(selectedUser.getStatus());
        }

        if(selectedUser.getRole() != null) {
            users.setRole(selectedUser.getRole());
        }

    }

    // Método responsável por selecionar os campos que serao atualizados do DTO.
    // Method responsible for selecting the fields that will be updated from the DTO.
    public void selectedUserFieldsDTO(Users selectedUser, UpdateUserDTO updateUserDTO) {
        selectedUser.setFirst_name(updateUserDTO.getFirst_name());
        selectedUser.setLast_name(updateUserDTO.getLast_name());
        selectedUser.setEmail(updateUserDTO.getEmail());
        selectedUser.setStatus(updateUserDTO.getStatus());
        selectedUser.setRole(updateUserDTO.getRole());
    }


    // Método responsável por enviar o e-mail para a caixa de entrada do NOVO USUÁRIO no sistema, com o ID e senha gerados.
    // Method responsible for sending the email to the NEW USER's inbox in the system, with the generated ID and password.
    private void sendWelcomeEmail(UsersDTO users, Users savedUser, String passwordUser) {
        String subject = "WELCOME! Your credentials are ready!";
        String emailBody = "Hello! " + users.getFirst_name() + " " + users.getLast_name() + ",\n\nWelcome to the System!\n\n" +
                "\n\nRemember to reset your password and make your profile as updated and complete as possible.\n\n" +
                "Here is your registration information:\n\n" +
                "Login identity: " + savedUser.getIdentity() + "\n" +
                "Password: " + passwordUser;

        emailService.sendEmail(users.getEmail(), subject, emailBody);
    }


    // Método que valida se o email já existe no banco de dados.
    // Method that validates whether the email already exists in the database.
    private void validateExistsEmail(UsersDTO users) {
        Users existingEmail = usersRepository.findByEmail(users.getEmail());

        if (existingEmail != null) {
            throw new IllegalArgumentException("Email already exists");
        }
    }


    // Método responsável por gerar automaticamente a IDENTIDADE de 6 dígitos do Usuário no sistema.
    // Method responsible for automatically generating the User's 6-digit IDENTITY in the system.
    @Transactional
    private String generateId() {

        String characters = "0123456789";
        StringBuilder identity = new StringBuilder();
        int length = 6;

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            identity.append(characters.charAt(index));
        }

        return identity.toString();
    }


    // Método responsável por gerar automaticamente a SENHA de 10 dígitos do Usuário no sistema. (((que será enviada por email)))
    // Method responsible for automatically generating the User's 10-digit PASSWORD in the system. ((which will be sent by email))
    @Transactional
    private String generateRandomPassword() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$#";
        StringBuilder password = new StringBuilder();
        int length = 10;

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            password.append(characters.charAt(index));
        }

        String passwordUser = password.toString();

        System.out.println("SENHA DO USUARIO ->> " + passwordUser);
        return password.toString();
    }


}
