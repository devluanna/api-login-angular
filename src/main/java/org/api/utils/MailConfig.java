package org.api.utils;

import org.springframework.context.annotation.Configuration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
public class MailConfig {

    //"Service" responsável pela comunicação com o Outlook, para envio do email após o registo.
    //"Service" responsible for communicating with outlook, to send the email after registration.
    public void sendEmail(String to, String subject, String body) {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.outlook.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");


        final String username = "rhconnectsystem@hotmail.com";
        final String password = "rhconnect@98";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);


            Transport.send(message);

            System.out.println("E-mail enviado com sucesso para: " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Erro ao enviar e-mail para: " + to);
        }
    }
}