package org.api.service.Impl;

import org.api.domain.model.ResponseDTO.PasswordDTO;
import org.api.domain.model.ResponseDTO.RecoveryPasswordDTO;
import org.api.domain.model.SubStatus;
import org.api.domain.model.Users;
import org.api.domain.repository.UsersRepository;
import org.api.service.PasswordService;
import org.api.service.UsersService;
import org.api.utils.MailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


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

            System.out.println("SENT!!");
            System.out.println("ÏD USER " + existingUser.getId_user());

            existingUser.setFirstAccessRequired(true);
            existingUser.setPasswordIsCompliance(false);
            existingUser.setSubStatus(SubStatus.IN_NON_COMPLIANCE);

            LocalDateTime now = LocalDateTime.now();
            existingUser.setLastPasswordUpdateDate(now);

            LocalDateTime passwordExpirationDate = now.plusMinutes(3);
            existingUser.setPasswordExpirationDays(passwordExpirationDate);


            if (now.isAfter(passwordExpirationDate)) {
                System.out.println("Your password will expire in " + passwordExpirationDate);
            }

            usersRepository.save(existingUser);

            //sendEmailWithNewPassword(existingUser, passwordUser); // (use this method to recover your password, the new password is generated and sent via email)

            return existingUser;
        } else {
            throw new IllegalArgumentException("Email does not match any user's email.");
        }
    }

    public Users toUpdatePassword(Users updatedNewPassword, PasswordDTO passwordDTO, Integer id_user) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication is required to update the password.");
        }

        Users authenticatedUser = (Users) authentication.getPrincipal();

        if (!authenticatedUser.getId_user().equals(id_user)) {
            throw new IllegalArgumentException("Access denied!");
        }

        String newPassword = passwordDTO.getPassword();
        String newPasswordConfirmation = passwordDTO.getConfirmPassword();

        if (newPassword == null || newPassword.length() < 10) {
            throw new IllegalArgumentException("Password must be at least 10 characters.");
        }

        if (!newPassword.equals(newPasswordConfirmation)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        String encryptedNewPassword = new BCryptPasswordEncoder().encode(newPassword);
        updatedNewPassword.setPassword(encryptedNewPassword);

        updatedNewPassword.setFirstAccessRequired(false);
        updatedNewPassword.setPasswordIsCompliance(true);
        updatedNewPassword.setSubStatus(SubStatus.UNLOCKED);
        LocalDateTime now = LocalDateTime.now();
        updatedNewPassword.setLastPasswordUpdateDate(now);

        LocalDateTime passwordExpirationDate = now.plusDays(30);

        //LocalDateTime passwordExpirationDate = now.plusMinutes(4); // for tests
        updatedNewPassword.setPasswordExpirationDays(passwordExpirationDate);

        return usersRepository.save(updatedNewPassword);
    }


    //Metodo responsavel por checar e bloquear caso a senha expire os 3 meses
    @Scheduled(cron = "0 0 0 * * ?") // in case of days (30), it will be updated at midnight
    //@Scheduled(cron = "0 * * * * ?") // in case of tests in minutes use this one!
    public void checkAndUpdatePasswordStatus() {
        List<Users> users = usersRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Users user : users) {
            // Verifica apenas usuários com SubStatus IN_NON_COMPLIANCE ou ON_ALERT
            if (user.getSubStatus() == SubStatus.IN_NON_COMPLIANCE || user.getSubStatus() == SubStatus.ON_ALERT) {
                LocalDateTime passwordExpirationDate = user.getPasswordExpirationDays();

                if (passwordExpirationDate != null) {
                    long daysUntilExpiration  = Duration.between(now, passwordExpirationDate).toMinutes();

                    if (daysUntilExpiration  <= 6 && daysUntilExpiration  > 3) {
                        user.setFirstAccessRequired(true);
                        user.setPasswordIsCompliance(false);
                        user.setSubStatus(SubStatus.IN_NON_COMPLIANCE);
                    } else if (daysUntilExpiration  == 3) {
                        user.setSubStatus(SubStatus.ON_ALERT);
                    } else if (daysUntilExpiration  <= 0) {
                        user.setSubStatus(SubStatus.BLOCKED);
                    }
                }
                usersRepository.save(user);
            }
        }
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

        System.out.println("NEW PASSWORD USER" + passwordUser);
        return password.toString();
    }

}
