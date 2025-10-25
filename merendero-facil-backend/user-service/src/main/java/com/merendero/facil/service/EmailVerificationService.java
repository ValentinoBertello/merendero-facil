package com.merendero.facil.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface EmailVerificationService {

    void sendVerificationCode(String email) throws MessagingException;

    Boolean validateCode(String email, String code);
}
