package com.portfolio.pti.Security.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${mail.from:${spring.mail.username:tomasperesin@gmail.com}}")
    private String mailFrom;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public void sendPasswordResetEmail(String toEmail, String nombre, String token) {
        String resetUrl = frontendUrl + "/cambiar-password?token=" + token;
        String subject = "Restablecimiento de Contraseña - Portfolio Tomás Peresín";
        String htmlContent = buildHtmlContent(nombre, resetUrl);

        logger.info("Generando enlace de recuperación para {}: {}", toEmail, resetUrl);

        // 1. Priorizar API HTTP de Brevo (Puerto 443 HTTPS - Nunca bloqueado en Render Free Tier)
        String apiKey = resolveBrevoApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            if (sendViaBrevoHttpApi(apiKey, toEmail, nombre, subject, htmlContent)) {
                return;
            }
            logger.warn("Fallo envío por API HTTP de Brevo, intentando fallback por SMTP...");
        }

        // 2. Fallback por SMTP tradicional (JavaMailSender)
        if (mailSender == null || mailFrom == null || mailFrom.trim().isEmpty()) {
            logger.warn("JavaMailSender o mail.from no están configurados. Enlace de restablecimiento: {}", resetUrl);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Correo de restablecimiento enviado exitosamente por SMTP a {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Error al enviar correo por SMTP a {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado en servicio de correo SMTP: {}", e.getMessage());
        }
    }

    private String resolveBrevoApiKey() {
        if (brevoApiKey != null && !brevoApiKey.trim().isEmpty()) {
            return brevoApiKey.trim();
        }
        // Permite colocar la API Key (xkeysib-...) directamente en MAIL_PASSWORD en Render
        if (mailPassword != null && mailPassword.trim().startsWith("xkeysib-")) {
            return mailPassword.trim();
        }
        return null;
    }

    private boolean sendViaBrevoHttpApi(String apiKey, String toEmail, String nombre, String subject, String htmlContent) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("api-key", apiKey);

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "Portfolio Tomás Peresín");
            sender.put("email", mailFrom != null && !mailFrom.trim().isEmpty() ? mailFrom.trim() : "tomasperesin@gmail.com");

            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", toEmail);
            recipient.put("name", (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : toEmail);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", sender);
            body.put("to", Collections.singletonList(recipient));
            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Correo de restablecimiento enviado exitosamente vía API HTTP de Brevo a {}", toEmail);
                return true;
            } else {
                logger.error("Respuesta no exitosa de API HTTP de Brevo ({}): {}", response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al enviar correo vía API HTTP de Brevo a {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    private String buildHtmlContent(String nombre, String resetUrl) {
        return "<!DOCTYPE html>"
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
    }
}
