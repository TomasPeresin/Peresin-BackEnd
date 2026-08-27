package com.portfolio.pti.Security.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${mail.from:${spring.mail.username:}}")
    private String mailFrom;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String nombre, String token) {
        String resetUrl = frontendUrl + "/cambiar-password?token=" + token;

        logger.info("Generando enlace de recuperación para {}: {}", toEmail, resetUrl);

        if (mailSender == null || mailFrom == null || mailFrom.trim().isEmpty()) {
            logger.warn("JavaMailSender o spring.mail.username no están configurados. Enlace de restablecimiento para pruebas: {}", resetUrl);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject("Restablecimiento de Contraseña - Portfolio Tomás Peresín");

            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head><meta charset='UTF-8'></head>"
                    + "<body style='margin: 0; padding: 20px; font-family: \"Segoe UI\", Roboto, sans-serif; background-color: #f8fafc; color: #1e293b;'>"
                    + "<div style='max-width: 540px; margin: 0 auto; background: #ffffff; border-radius: 12px; padding: 32px; border: 1px solid #e2e8f0; box-shadow: 0 4px 16px rgba(0,0,0,0.05);'>"
                    + "  <div style='text-align: center; margin-bottom: 24px;'>"
                    + "    <div style='display: inline-block; width: 44px; height: 44px; line-height: 44px; background: linear-gradient(135deg, #2563eb, #7c3aed); color: #ffffff; font-weight: bold; border-radius: 10px; font-size: 18px;'>TP</div>"
                    + "    <h2 style='margin-top: 12px; margin-bottom: 4px; color: #0f172a;'>Portfolio Tomás Peresín</h2>"
                    + "    <p style='margin: 0; color: #64748b; font-size: 14px;'>Recuperación de Contraseña</p>"
                    + "  </div>"
                    + "  <p style='font-size: 16px;'>Hola <strong>" + (nombre != null ? nombre : "") + "</strong>,</p>"
                    + "  <p style='font-size: 15px; line-height: 1.6; color: #334155;'>Has solicitado restablecer tu contraseña de acceso. Haz clic en el siguiente botón para continuar:</p>"
                    + "  <div style='text-align: center; margin: 30px 0;'>"
                    + "    <a href='" + resetUrl + "' style='background: linear-gradient(135deg, #2563eb, #7c3aed); color: #ffffff; text-decoration: none; padding: 12px 28px; border-radius: 8px; font-weight: 600; font-size: 15px; display: inline-block; box-shadow: 0 4px 12px rgba(37,99,235,0.25);'>Restablecer Contraseña</a>"
                    + "  </div>"
                    + "  <p style='font-size: 13px; color: #64748b;'>Si el botón no funciona, copia y pega este enlace en tu navegador:<br><a href='" + resetUrl + "' style='color: #2563eb; word-break: break-all;'>" + resetUrl + "</a></p>"
                    + "  <div style='margin-top: 28px; padding-top: 20px; border-top: 1px solid #f1f5f9; font-size: 12px; color: #94a3b8; line-height: 1.5;'>"
                    + "    <p style='margin: 0;'>Este enlace tiene una validez de <strong>30 minutos</strong>. Si no solicitaste este cambio, puedes ignorar este mensaje de forma segura.</p>"
                    + "  </div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Correo de restablecimiento enviado exitosamente a {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Error al enviar correo a {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado en servicio de correo: {}", e.getMessage());
        }
    }
}
