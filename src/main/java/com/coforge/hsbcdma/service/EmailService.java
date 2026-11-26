package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.UserMananagementDTO.DecryptEmailRequest;
import com.coforge.hsbcdma.dto.UserMananagementDTO.DecryptEmailResponse;
import com.coforge.hsbcdma.util.AESUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * This is Email Service which sends temporary password to user through Email
 * @author Vandana Pal
 */

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTempPassword(String toEmail, String tempPassword) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Temporary Password");
        message.setText(
                "Your temporary password is: " + tempPassword +
                        "\nPlease login and change your password immediately."
        );

        mailSender.send(message);
    }


    public void sendProfileAttachedNotification(String toEmail, String candidateName, Long demandId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Profile Shared to Demand - " + demandId);
        message.setText(
                "Dear Team,\n\n" +
                        "A new profile has been Shared to Demand: " + demandId + "\n" +
                        "Candidate Name: " + candidateName + "\n\n" +
                        "Please review the profile at the earliest.\n\n" +
                        "Regards,\nHSBC DMA System"
        );
        mailSender.send(message);
    }



    public DecryptEmailResponse decryptEmail(DecryptEmailRequest request) {

        try {
            String decrypted = AESUtil.decrypt(
                    request.getEncryptedEmail(),
                    request.getPassword()
            );

            return new DecryptEmailResponse(decrypted, "Decryption successful");

        } catch (Exception e) {
            throw new RuntimeException("Invalid password or corrupted data");
        }
    }


}
