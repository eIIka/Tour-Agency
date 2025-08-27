package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.repo.UserRepo;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepo userRepoMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customUserDetailsService = new CustomUserDetailsService(userRepoMock);
    }

    @Test
    void loadUserByUsernameTest_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("password");

        when(userRepoMock.findByEmail(anyString())).thenReturn(of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test user email");

        assertNotNull(userDetails);
        assertEquals(userDetails.getUsername(), user.getEmail());
        assertEquals(userDetails.getPassword(), user.getPassword());
    }

    @Test
    void loadUserByUsernameTest_userNotFoundException() {
        when(userRepoMock.findByEmail(anyString())).thenReturn(of(new User()));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test user email");

        assertNotNull(userDetails);
    }
}