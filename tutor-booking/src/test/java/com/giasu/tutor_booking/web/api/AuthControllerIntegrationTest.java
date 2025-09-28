package com.giasu.tutor_booking.web.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giasu.tutor_booking.domain.user.AccountStatus;
import com.giasu.tutor_booking.domain.user.UserAccount;
import com.giasu.tutor_booking.domain.user.UserRole;
import com.giasu.tutor_booking.dto.auth.LoginRequest;
import com.giasu.tutor_booking.dto.auth.RegisterRequest;
import com.giasu.tutor_booking.repository.UserAccountRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUp() {
        userAccountRepository.deleteAll();
    }

    @Test
    void registerCreatesParentAccount() throws Exception {
        String email = randomEmail();
        RegisterRequest request = new RegisterRequest(email, "Password123", "Parent User", "0123456789", UserRole.PARENT);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.role").value(UserRole.PARENT.name()))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("accessToken");
        assertThat(userAccountRepository.findByEmailIgnoreCase(email)).isPresent();
    }

    @Test
    void registerDuplicateEmailReturnsConflict() throws Exception {
        String email = randomEmail();
        userAccountRepository.save(UserAccount.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123"))
                .fullName("Existing User")
                .role(UserRole.PARENT)
                .status(AccountStatus.ACTIVE)
                .build());

        RegisterRequest request = new RegisterRequest(email, "Password123", "Parent User", null, UserRole.PARENT);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginReturnsToken() throws Exception {
        String email = randomEmail();
        userAccountRepository.save(UserAccount.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123"))
                .fullName("Parent User")
                .role(UserRole.PARENT)
                .status(AccountStatus.ACTIVE)
                .build());

        LoginRequest request = new LoginRequest(email, "Password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(email));
    }

    @Test
    void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest(randomEmail(), "WrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private String randomEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}

