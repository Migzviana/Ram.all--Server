package com.example.Ramal_back.infra.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void  sendResetPasswordEmail(String to, String token){
        String subject = "Recuperação de Senha";
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        String text = "Clique no link para redefinir sua senha: " + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

    }
}
