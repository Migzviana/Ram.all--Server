package com.example.Ramal_back.infra.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
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

        String htmlContent = """
        <html>
                      <body style="font-family: Arial, sans-serif; background-color: #f1f3f6; padding: 20px; margin: 0;">
                        <table width="100%%" cellpadding="0" cellspacing="0" style="max-width: 600px; margin: auto; border-collapse: collapse;">
                          <tr>
                            <td style="background-color: #3b3f57; padding: 20px; text-align: center; border-top-left-radius: 8px; border-top-right-radius: 8px;">
                              <img src="cid:logoImage" alt="Logo" style="width: 150px;">
                            </td>
                          </tr>
                          <tr>
                            <td style="background-color: #ffffff; padding: 30px; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                              <h2 style="color: #7001FD; margin-top: 0;">Recuperação de Senha</h2>
                              <p style="margin: 16px 0;">Olá,</p>
                              <p style="margin: 16px 0;">Você solicitou a redefinição de senha. Copie o código abaixo e insira-o na página de redefinição de senha:</p>
                              <div style="text-align: center; font-size: 28px; font-weight: bold; margin: 24px 0; color: #7001FD;">%s</div>
                              <p style="margin: 16px 0;">Este código expira em 1 hora.</p>
                              <p style="margin: 16px 0;">Este e-mail foi enviado para garantir a segurança da sua conta. Caso não tenha solicitado, desconsidere.</p>
                              <p style="margin-top: 40px; font-size: 12px; color: #999999; text-align: center;">© 2025 Ram.all. Todos os direitos reservados.</p>
                            </td>
                          </tr>
                        </table>
                      </body>
                    </html>
    """.formatted(token);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            File imageFile = new File("src/main/java/com/example/Ramal_back/infra/service/logo.png");

            if (!imageFile.exists()) {
                throw new RuntimeException("Imagem do logo não encontrada em: " + imageFile.getAbsolutePath());
            }

            FileSystemResource image = new FileSystemResource(imageFile);
            helper.addInline("logoImage", image);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Erro ao montar o e-mail: " + e.getMessage());
            throw new RuntimeException("Erro ao montar o e-mail. Tente novamente.");
        } catch (MailException e) {
            System.err.println("Erro ao enviar o e-mail: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar o e-mail. Verifique o servidor de e-mail.");
        } catch (RuntimeException e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            throw e;
        }
    }
}
