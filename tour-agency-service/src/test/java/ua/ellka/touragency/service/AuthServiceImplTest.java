package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.ellka.touragency.dto.AuthDTO;
import ua.ellka.touragency.dto.LoginDTO;
import ua.ellka.touragency.dto.RegisterDTO;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.mapper.UserMapper;
import ua.ellka.touragency.model.security.CustomUserDetails;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.repo.UserRepo;
import ua.ellka.touragency.util.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {
    private AuthService authService;
    private UserRepo userRepo;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private CustomUserDetails customUserDetails;
    private Authentication authentication;
    private SecurityContext securityContext;
    private User user;
    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        userRepo = Mockito.mock(UserRepo.class);
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        ClientService clientService = Mockito.mock(ClientService.class);
        GuideService guideService = Mockito.mock(GuideService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        authService = new AuthServiceImpl(userRepo, bCryptPasswordEncoder, userMapper,
                clientService, guideService, authenticationManager, jwtUtil);

        registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password");
        registerDTO.setRole("ROLE_CLIENT");
        registerDTO.setName("Test");
        registerDTO.setPhone("123456");
        registerDTO.setPassportNumber("AA123456");

        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashed");
        user.setRole("ROLE_CLIENT");

        customUserDetails = new CustomUserDetails(user);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);
    }

    @Test
    void registerUserTest_createUserWithRoleClientSuccess() {
        when(userRepo.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(registerDTO.getPassword())).thenReturn("hashed");
        when(userRepo.save(Mockito.any(User.class))).thenReturn(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());

        UserDTO result = authService.registerUser(registerDTO);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void registerUserTest_createUserWithRoleGuideSuccess() {
        when(userRepo.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(registerDTO.getPassword())).thenReturn("hashed");

        user.setRole("ROLE_GUIDE");
        registerDTO.setRole("ROLE_GUIDE");
        when(userRepo.save(Mockito.any(User.class))).thenReturn(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());

        UserDTO result = authService.registerUser(registerDTO);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void registerUserTest_userAlreadyExistsException() {
        when(userRepo.findByEmail(registerDTO.getEmail()))
                .thenReturn(Optional.of(user));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> authService.registerUser(registerDTO));
        assertEquals("User with email already exists", exception.getMessage());
    }

    @Test
    void authenticateUserTest_success() {
        when(userRepo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(customUserDetails, loginDTO.getPassword()));
        when(bCryptPasswordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(customUserDetails)).thenReturn("jwt-token");

        AuthDTO result = authService.authenticateUser(loginDTO);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
    }

    @Test
    void authenticateUserTest_userNotFoundException() {
        when(userRepo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> authService.authenticateUser(loginDTO));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void authenticateUserTest_passwordMismatchException() {
        when(userRepo.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(customUserDetails, loginDTO.getPassword()));
        when(bCryptPasswordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        AuthDTO result = authService.authenticateUser(loginDTO);
        assertNull(result);
    }

    @Test
    void getCurrentUserTest_success() {
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        SecurityContextHolder.setContext(securityContext);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setRole("ROLE_CLIENT");

        UserDTO result = authService.getCurrentUser();

        assertNotNull(result);
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getRole(), result.getRole());
        assertEquals(userDTO.getId(), result.getId());
    }

    @Test
    void getCurrentUserTest_principalIsAnonymousException() {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> authService.getCurrentUser());
        assertEquals("Anonymous user", exception.getMessage());
    }

    @Test
    void getCurrentUserTest_userNotFoundException() {
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        SecurityContextHolder.setContext(securityContext);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> authService.getCurrentUser());
        assertEquals("User not found", exception.getMessage());
    }
}