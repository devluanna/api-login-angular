package org.api.service.Impl;

import org.api.domain.model.ResponseDTO.PasswordDTO;
import org.api.domain.model.ResponseDTO.RecoveryPasswordDTO;
import org.api.domain.model.Users;
import org.api.domain.repository.UsersRepository;
import org.api.service.PasswordService;
import org.api.service.UsersService;
import org.api.utils.MailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    MailConfig emailService;

    @Autowired
    UsersService usersService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users recoverPassword(Users user, RecoveryPasswordDTO passDTO, String email) {

        Users existingUser = usersRepository.findByEmail(passDTO.getEmail());

        if (existingUser != null) {
            String passwordUser = generateNewPassword();

            String encryptedPassword = passwordEncoder.encode(passwordUser);
            existingUser.setPassword(encryptedPassword);
            usersRepository.save(existingUser);

            sendEmailWithNewPassword(existingUser, passwordUser);

            return existingUser;
        } else {
            throw new IllegalArgumentException("Email does not match any user's email.");
        }
    }

    public Users toUpdatePassword(Users updatedNewPassword, PasswordDTO passwordDTO, Integer id) {
        Users authenticatedUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!authenticatedUser.getId_user().equals(id)) {
            throw new IllegalArgumentException("Access denied!");
        }

        String newPassword = updatedNewPassword.getPassword();

        if (newPassword == null || newPassword.length() < 10) {
            throw new IllegalArgumentException("Password must be at least 10 characters.");
        }

        String encryptedPassword = passwordEncoder.encode(newPassword);
        updatedNewPassword.setPassword(encryptedPassword);

        return usersRepository.save(updatedNewPassword);
    }

    private void sendEmailWithNewPassword(Users savedUser, String passwordUser) {
        String subject = "Password recovery!";
        String emailBody = "Hello! " + savedUser.getFirst_name() + " " + savedUser.getLast_name() + ",\n\nWelcome back!\n\n" +
                "\n\nRemember to reset your password\n\n" +
                "Here is your new generated password:\n\n" +
                "Login identity: " + savedUser.getIdentity() + "\n" +
                "Password: " + passwordUser;

        emailService.sendEmail(savedUser.getEmail(), subject, emailBody);
    }

    private String generateNewPassword() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$#";
        StringBuilder password = new StringBuilder();
        int length = 10;

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            password.append(characters.charAt(index));
        }

        String passwordUser = password.toString();

        System.out.println("SENHA NOVA DO USUARIO ->> " + passwordUser);
        return password.toString();
    }

}
