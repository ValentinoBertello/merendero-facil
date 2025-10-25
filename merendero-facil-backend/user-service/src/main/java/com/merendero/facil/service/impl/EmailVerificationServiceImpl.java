package com.merendero.facil.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.merendero.facil.service.EmailVerificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Toda la lógica de negocio referida a el envío de mails y validación de codigos
 * **/
@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final JavaMailSender mailSender;

    //Map en memoria que guardará pares (email -> código)
    private final Cache<String, String> codeStorage;

    public EmailVerificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.codeStorage = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Genera y envía un código de verificación a un email específico.
     * El código se almacena en memoria por 10 minutos.
     */
    @Override
    @Transactional(readOnly = true)
    public void sendVerificationCode(String email) {
        try {
            // Generamos y agregamos el codigo en memoria
            String code = generateRandomCode();
            codeStorage.put(email, code);

            // Creamos el mensaje a enviar
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("merenderofacilteam@gmail.com");

            String testContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<style>@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@600&display=swap');</style>"
                    + "</head>"
                    + "<body style='margin: 0; padding: 20px; background-color: #f5f5f5; font-family: Arial, sans-serif;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; background: white; padding: 40px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>"
                    + "<h1 style='font-size: 2.2rem; margin: -8px 0 30px 0; text-align: center; color: #ffb343; font-family: Poppins, sans-serif; font-weight: 600; border-bottom: 3px solid #ffd000; display: inline-block;'>Verificación requerida</h1>"
                    + "<p style='font-size: 16px; color: #555; line-height: 1.6;'>Se ha solicitado un código de verificación para tu cuenta. Si no realizaste esta solicitud, ignora este mensaje.</p>"
                    + "<div style='margin: 30px 0; text-align: center;'>"
                    + "<div style='display: inline-block; background: #ffb343; color: white; padding: 15px 30px; border-radius: 5px; font-size: 24px; font-weight: bold; letter-spacing: 2px;'>" + code + "</div>"
                    + "</div>"
                    + "<p style='font-size: 14px; color: #777; line-height: 1.5;'>Este código es válido por 10 minutos. No lo compartas con nadie.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            // Seteamos el mail al que le mandaremos el código
            helper.setTo(email);
            helper.setSubject("🔒 Código de verificación");
            helper.setText(testContent, true);
            // Mandamos el mail
            mailSender.send(message);
        }  catch (MessagingException e) {
            System.err.println("Error enviando email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el email", e);
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Valida que un código proporcionado coincida con el almacenado para un email.
     * @return true si el código es correcto, false en caso contrario.
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean validateCode(String email, String code) {
        String storedCode = this.codeStorage.getIfPresent(email);
        return Objects.equals(storedCode, code);
    }

    /**
     * Genera un código aleatorio de 6 dígitos.
     */
    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}