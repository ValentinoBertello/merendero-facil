package com.merendero.facil.service.impl;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    @Test
    void sendVerificationCode_storesCodeAndSendsEmail() throws Exception {
        String email = "test@example.com";
        Session session = Session.getInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailVerificationService.sendVerificationCode(email);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        MimeMessage sent = captor.getValue();

        assertEquals("🔒 Código de verificación", sent.getSubject());
    }

    @Test
    void validateCode_returnsFalseWhenNoCodeStored() {
        String email = "no-code@example.com";
        // no se envió código antes
        assertFalse(emailVerificationService.validateCode(email, "000000"));
    }

    @Test
    void sendVerificationCode_propagatesExceptionWhenCreateMimeMessageFails() {
        String email = "fail@example.com";

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("boom"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailVerificationService.sendVerificationCode(email));
        assertEquals("boom", ex.getMessage());
    }
}