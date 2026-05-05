package com.bookstore.user.service;

import com.bookstore.user.dto.AuthResponse;
import com.bookstore.user.dto.ChangePasswordRequest;
import com.bookstore.user.dto.LoginRequest;
import com.bookstore.user.dto.UpdateProfileRequest;
import com.bookstore.user.dto.UserRegistrationRequest;
import com.bookstore.user.entity.User;
import com.bookstore.user.entity.UserRole;
import com.bookstore.user.event.UserRegisteredEvent;
import com.bookstore.user.exception.BadRequestException;
import com.bookstore.user.exception.ResourceNotFoundException;
import com.bookstore.user.repository.UserRepository;
import com.bookstore.user.security.JwtUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtil,
        KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public User register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role() == null ? UserRole.USER : request.role());

        User saved = userRepository.save(user);
        kafkaTemplate.send(
            "user-events",
            new UserRegisteredEvent(saved.getId(), saved.getEmail(), saved.getRole(), saved.getCreatedAt())
        );
        return saved;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElse(UserRole.USER.name());
        String token = jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal(), role);
        return new AuthResponse(token);
    }

    public User getProfile() {
        return getCurrentUser();
    }

    @Transactional
    public User updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }
        user.setEmail(request.email());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
