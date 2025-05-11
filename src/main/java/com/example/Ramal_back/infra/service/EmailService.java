package com.example.Ramal_back.infra.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Recuperação de Senha";
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        String htmlContent = """
                <html>
                <body style="margin: 0; padding: 0; background-color: #F1F3F6; font-family: Arial, sans-serif; color: #555555;">
                  <table align="center" width="100%%" cellpadding="0" cellspacing="0" style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; overflow: hidden;">
                
                    <!-- Header -->
                    <tr>
                      <td style="background-color: #3b3f57; padding: 20px; text-align: center;">
                        <img src="cid:logoImage" alt="Logo" style="width: 150px;">
                      </td>
                    </tr>
                
                    <!-- Content -->
                    <tr>
                      <td style="padding: 30px;">
                        <h2 style="color: #7001FD;">Recuperação de Senha</h2>
                        <p>Olá,</p>
                        <p>Você solicitou a redefinição de senha para sua conta. Para continuar com o processo, clique no botão abaixo:</p>
                        <div style="text-align: center; margin: 30px 0;">
                          <a href="%s" style="background-color: #7001FD; color: #ffffff; padding: 14px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">Redefinir Senha</a>
                        </div>
                        <p>Se você não solicitou essa alteração, pode ignorar este e-mail com segurança.</p>
                      </td>
                    </tr>
                
                    <!-- Footer -->
                    <tr>
                      <td style="background-color: #F1F3F6; text-align: center; padding: 20px; font-size: 12px; color: #999999;">
                        © 2025 Ram.all. Todos os direitos reservados.
                      </td>
                    </tr>
                
                  </table>
                </body>
                </html>
                """.formatted(resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            File imageFile = new File("src/main/java/com/example/Ramal_back/infra/service/logo.png");

            if (!imageFile.exists()) {
                throw new RuntimeException("Imagem não encontrada em: " + imageFile.getAbsolutePath());
            }

            FileSystemResource image = new FileSystemResource(imageFile);
            helper.addInline("logoImage", image);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();

        }
    }
}