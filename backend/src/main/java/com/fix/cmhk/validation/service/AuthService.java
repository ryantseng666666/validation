package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.dto.AuthResponse;
import com.fix.cmhk.validation.dto.LoginRequest;
import com.fix.cmhk.validation.dto.RegisterRequest;
import com.fix.cmhk.validation.entity.User;
import com.fix.cmhk.validation.repository.UserRepository;
import com.fix.cmhk.validation.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration request for user: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - Username is already taken: {}", request.getUsername());
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - Email is already in use: {}", request.getEmail());
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(true);

        userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, user.getUsername(), "User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Processing login request for user: {}", request.getUsername());
        
        // 先通过邮箱查找用户
        Optional<User> user = userRepository.findByEmail(request.getUsername());
        if (!user.isPresent()) {
            // 如果通过邮箱没找到,再尝试通过用户名查找
            user = userRepository.findByUsername(request.getUsername());
            if (!user.isPresent()) {
                throw new RuntimeException("用户不存在");
            }
        }
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.get().getUsername(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        log.info("User logged in successfully: {}", request.getUsername());
        return new AuthResponse(token, request.getUsername(), "Login successful");
    }

    @Transactional
    public String sendVerificationCode(String email, String type) {
        log.info("Generating verification code for email: {} and type: {}", email, type);
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        String code = generateVerificationCode();
        if ("REGISTER".equals(type)) {
            // 注册场景
            if (existingUser.isPresent()) {
                log.warn("Registration failed - Email already exists: {}", email);
                throw new RuntimeException("该邮箱已注册，请直接登录");
            }
            
            // 新用户直接发送验证码,不保存用户信息
            log.info("Sending verification code for new user registration: {}", email);
            emailService.sendVerificationEmail(email, code, type);
            
        } else if ("RESET_PASSWORD".equals(type) ||  "LOGIN".equals(type)) {
            // 重置密码/login场景
            if (!existingUser.isPresent()) {
                log.warn("Reset password failed - Email not found: {}", email);
                throw new RuntimeException("该邮箱未注册，请先注册");
            }

            User user = existingUser.get();
            user.setVerificationCode(code);
            user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            log.info("Sending verification code for password reset: {}", email);
            emailService.sendVerificationEmail(email, code, type);
            
        } else {
            throw new IllegalArgumentException("无效的验证码类型");
        }

        return "验证码发送成功";
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
} 