package com.example.demo.Service;

import com.example.demo.Dto.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails) throws MessagingException;
}
