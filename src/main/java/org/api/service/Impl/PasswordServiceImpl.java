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
import org.springframework.security.core.context.SecurityContextHolder;
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

            System.out.println("ENVIADO");
            System.out.println("ÏD DO USUARIO QUE ESTA RECUPERANDO SENHA: " + existingUser.getId_user());
            existingUser.setFirstAccessRequired(true);
            existingUser.setPasswordIsCompliance(false);
            existingUser.setSubStatus(SubStatus.IN_NON_COMPLIANCE);

            LocalDateTime now = LocalDateTime.now();
            existingUser.setLastPasswordUpdateDate(now);

            LocalDateTime passwordExpirationDate = now.plusMinutes(3);
            existingUser.setPasswordExpirationDays(passwordExpirationDate);

            // Verificação de expiração da senha
            if (now.isAfter(passwordExpirationDate)) {
                System.out.println("Sua senha vai expirar em " + passwordExpirationDate);
            }

            usersRepository.save(existingUser);

            //sendEmailWithNewPassword(existingUser, passwordUser);

            return existingUser;
        } else {
            throw new IllegalArgumentException("Email does not match any user's email.");
        }
    }

    public Users toUpdatePassword(Users updatedNewPassword, PasswordDTO passwordDTO, Integer id_user) {

        Users authenticatedUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!authenticatedUser.getId_user().equals(id_user)) {
            throw new IllegalArgumentException("Access denied!");
        }

        String newPassword = updatedNewPassword.getPassword();

        if (newPassword == null || newPassword.length() < 10) {
            throw new IllegalArgumentException("Password must be at least 10 characters.");
        }

        String encryptedPassword = passwordEncoder.encode(newPassword);
        updatedNewPassword.setPassword(encryptedPassword);

        updatedNewPassword.setFirstAccessRequired(false);
        updatedNewPassword.setPasswordIsCompliance(true);
        updatedNewPassword.setSubStatus(SubStatus.UNLOCKED);
        LocalDateTime now = LocalDateTime.now();
        updatedNewPassword.setLastPasswordUpdateDate(now);

        // Calcular a data de expiração da senha para 30 dias após a atualização
        LocalDateTime passwordExpirationDate = now.plusDays(30);
        updatedNewPassword.setPasswordExpirationDays(passwordExpirationDate);

        // Verificação de expiração da senha
        if (now.isAfter(passwordExpirationDate)) {
            System.out.println("Sua senha vai expirar em " + passwordExpirationDate);
        }

        System.out.println("SUBSTATUS DO USUARIO EH " + updatedNewPassword.getSubStatus());

        return usersRepository.save(updatedNewPassword);
    }


    //Metodo responsavel por checar e bloquear caso a senha expire os 3 meses
    //@Scheduled(cron = "0 0 0 * * ?") // Executa diariamente à meia-noite
    @Scheduled(cron = "0 * * * * ?") // Executa a cada minuto
    public void checkAndUpdatePasswordStatus() {
        List<Users> users = usersRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Users user : users) {
            // Verifica apenas usuários com SubStatus IN_NON_COMPLIANCE ou ON_ALERT
            if (user.getSubStatus() == SubStatus.IN_NON_COMPLIANCE || user.getSubStatus() == SubStatus.ON_ALERT) {
                LocalDateTime passwordExpirationDate = user.getPasswordExpirationDays();

                if (passwordExpirationDate != null) {
                    long minutesUntilExpiration = Duration.between(now, passwordExpirationDate).toMinutes();

                    if (minutesUntilExpiration <= 3 && minutesUntilExpiration > 1) {
                        user.setFirstAccessRequired(true);
                        user.setPasswordIsCompliance(false);
                        user.setSubStatus(SubStatus.IN_NON_COMPLIANCE);
                    } else if (minutesUntilExpiration <= 1) {
                        user.setSubStatus(SubStatus.ON_ALERT);
                    } else if (minutesUntilExpiration <= 0) {

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

        System.out.println("SENHA NOVA DO USUARIO ->> " + passwordUser);
        return password.toString();
    }

}
