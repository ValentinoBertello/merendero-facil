package com.merendero.facil.movement.service.output.impl;

import com.merendero.facil.movement.service.output.EmailService;
import com.merendero.facil.supply.entity.SupplyEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado del envio de emails al usuario.
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    /**
     * Envía un email de alerta de stock bajo al usuario actualmente autenticado.
     **/
    @Override
    public void sendLowStockEmail(SupplyEntity supply) {
        try {
            MimeMessageHelper helper = this.buildBaseMessage();

            String testContent = this.getLowStockHtmlText(supply);

            helper.setTo(this.getUsernameLogged());
            helper.setSubject("¡Alerta! Stock bajo de " + supply.getName());
            helper.setText(testContent, true);

            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Error enviando email de stock bajo: {}", e.getMessage(), e);
        }
    }

    /**
     * Construye un MimeMessageHelper con la configuración base
     * (mensaje vacío y remitente por defecto).
     */
    private MimeMessageHelper buildBaseMessage() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("merenderofacilteam@gmail.com");
        return helper;
    }

    /**
     * Recupera el email del usuario actualmente autenticado
     */
    private String getUsernameLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * Genera el contenido HTML del email de alerta de stock bajo para el insumo indicado.
     */
    private String getLowStockHtmlText(SupplyEntity supply) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@600&display=swap');</style>"
                + "</head>"
                + "<body style='margin: 0; padding: 20px; background-color: #f5f5f5; font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 20px auto; background: white; padding: 40px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>"
                + "<h1 style='font-size: 2.2rem; margin: -8px 0 30px 0; text-align: center; color: #ffb343; font-family: Poppins, sans-serif; font-weight: 600; border-bottom: 3px solid #ffd000; display: inline-block;'>¡Stock Bajo de " + supply.getName() + "!</h1>"
                + "<p style='font-size: 16px; color: #555; line-height: 1.6;'>¡Atención! Necesitas reponer un insumo</p>"
                + "<p style='font-size: 16px; color: #555; line-height: 1.6;'>Hemos detectado que el siguiente insumo ha bajado por debajo del nivel mínimo establecido: <span style='font-weight: bold;'>" + supply.getName() + "</span> </p>"
                + "<p style='font-size: 14px; color: #777; line-height: 1.5;'>Recuerda que puedes gestionar tu inventario directamente en la plataforma de Merendero Fácil.</p>"
                + "<a style='display: inline-block; background: #ffb343; color: white; padding: 12px 27px; border-radius: 5px; font-size: 15px; font-weight: bold; letter-spacing: 2px;'  href='http://localhost:4200/stock/movimientos/entradas'>GESTIONAR INVENTARIO</a>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}