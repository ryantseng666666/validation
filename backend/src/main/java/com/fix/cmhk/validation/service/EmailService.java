package com.fix.cmhk.validation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String to, String code, String type) {
        log.info("Preparing to send verification email for type: {} to: {}", type, to);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        
        String subject;
        String content;
        
        switch (type) {
            case "REGISTER":
                subject = "注册验证码";
                content = "您正在注册CMHK验证系统账号，验证码为：" + code + "\n该验证码5分钟内有效。";
                break;
            case "LOGIN":
                subject = "登录验证码";
                content = "您正在登录CMHK验证系统，验证码为：" + code + "\n该验证码5分钟内有效。";
                break;
            case "RESET_PASSWORD":
                subject = "重置密码验证码";
                content = "您正在重置CMHK验证系统的密码，验证码为：" + code + "\n该验证码5分钟内有效。";
                break;
            default:
                subject = "验证码";
                content = "您的验证码为：" + code + "\n该验证码5分钟内有效。";
        }
        
        message.setSubject(subject);
        message.setText(content);
        
        try {
            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw e;
        }
    }
} 