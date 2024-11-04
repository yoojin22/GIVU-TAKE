package com.accepted.givutake.user.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String email, String subject, String text) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true indicates multipart message

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(text, true); // true indicates HTML

        mailSender.send(message);
    }

    public void sendMultipleMessage(String email, String fileName, String subject, String text, byte[] pdfByte) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setTo(email);

        //첨부 파일 설정
        try {
            helper.addAttachment(
                    MimeUtility.encodeText(fileName, "UTF-8", "B"),
                    new ByteArrayResource(pdfByte) // pdfByte 배열 사용
            );
        } catch (UnsupportedEncodingException e) {
            log.error("첨부 파일 설정 중 오류 발생: {}", e.getMessage());
        }

        // 전송
        mailSender.send(message);
    }


}
