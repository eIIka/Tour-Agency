package ua.ellka.touragency.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.ellka.touragency.dto.AuthDTO;
import ua.ellka.touragency.dto.LoginDTO;
import ua.ellka.touragency.dto.RegisterDTO;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerParentTest {
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);

        AuthController authController = new AuthController(authService);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void registerTest_returnsOk() throws Exception {
        when(authService.registerUser(any())).thenReturn(new UserDTO());

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@gmail.com");
        registerDTO.setPassword("password");
        registerDTO.setRole("ROLE_CLIENT");
        registerDTO.setName("test name");
        registerDTO.setPassportNumber("AA123456");
        registerDTO.setPhone("+380999999999");

        String reqBody = objectMapper.writeValueAsString(registerDTO);
        mockMvc.perform(
                        post("/v1/auth/register")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void registerTest_returnsConflict() throws Exception {
        when(authService.registerUser(any())).thenThrow(ExistingServiceException.class);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@gmail.com");
        registerDTO.setPassword("password");
        registerDTO.setRole("ROLE_CLIENT");
        registerDTO.setName("test name");
        registerDTO.setPassportNumber("AA123456");
        registerDTO.setPhone("+380999999999");

        String reqBody = objectMapper.writeValueAsString(registerDTO);
        mockMvc.perform(
                        post("/v1/auth/register")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void loginTest_returnsOk() throws Exception {
        when(authService.authenticateUser(any())).thenReturn(new AuthDTO());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@gmail.com");
        loginDTO.setPassword("password");

        String reqBody = objectMapper.writeValueAsString(loginDTO);
        mockMvc.perform(
                        post("/v1/auth/login")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void loginTest_returnsNotFound() throws Exception {
        when(authService.authenticateUser(any())).thenThrow(NotFoundServiceException.class);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@gmail.com");
        loginDTO.setPassword("password");

        String reqBody = objectMapper.writeValueAsString(loginDTO);
        mockMvc.perform(
                        post("/v1/auth/login")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCurrentUserTest_returnsOk() throws Exception {
        when(authService.getCurrentUser()).thenReturn(new UserDTO());

        mockMvc.perform(get("/v1/auth/current"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCurrentUserTest_returnsNotFound() throws Exception {
        when(authService.getCurrentUser()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/auth/current"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}