package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.ellka.touragency.dto.*;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.mapper.UserMapper;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.model.security.TourAgencyUserDetails;
import ua.ellka.touragency.repo.UserRepo;
import ua.ellka.touragency.util.JwtUtil;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ClientService clientService;
    private final GuideService guideService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public UserDTO registerUser(RegisterDTO registerDTO) {
        String passwordHash = passwordEncoder.encode(registerDTO.getPassword());

        userRepo.findByEmail(registerDTO.getEmail()).ifPresent(user -> {
            throw new ExistingServiceException("User with email already exists");
        });

        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordHash);
        user.setRole(registerDTO.getRole());

        User saved;
        if (registerDTO.getRole().equals("ROLE_CLIENT")) {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setName(registerDTO.getName());
            clientDTO.setPhone(registerDTO.getPhone());
            clientDTO.setPassportNumber(registerDTO.getPassportNumber());
            clientDTO.setEmail(registerDTO.getEmail());
            saved = userRepo.save(user);
            clientService.createClient(clientDTO);
        } else {
            GuideDTO guideDTO = new GuideDTO();
            guideDTO.setName(registerDTO.getName());
            guideDTO.setLanguage(registerDTO.getLanguage());
            guideDTO.setEmail(registerDTO.getEmail());
            saved = userRepo.save(user);
            guideService.createGuide(guideDTO);
        }

        return userMapper.userToUserDTO(saved);
    }

    @Override
    public AuthDTO authenticateUser(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        UserDetails authAttempt = org.springframework.security.core.userdetails.User
                .builder()
                .username(email)
                .password(password)
                .authorities(Set.of(new SimpleGrantedAuthority(user.getRole())))
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authAttempt, password);

        Authentication authenticate = authenticationManager.authenticate(authentication);

        TourAgencyUserDetails principal = (TourAgencyUserDetails) authenticate.getPrincipal();

        if (principal != null) {
            String principalPassword = principal.getPassword();

            boolean matches = passwordEncoder.matches(password, principalPassword);
            if (matches) {
                String token = jwtUtil.generateToken(principal);

                AuthDTO authDTO = new AuthDTO();
                authDTO.setUser(userMapper.userToUserDTO(user));
                authDTO.setToken(token);

                return authDTO;
            }
        }

        return null;
    }

    //TODO realize method
    @Override
    public UserDTO logoutUser() {
        return getCurrentUser();
    }

    @Override
    public UserDTO getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        Object principal = authentication.getPrincipal();
        if (principal instanceof TourAgencyUserDetails userDetails) {
            User existingUser = userRepo.findById(userDetails.getId())
                    .orElseThrow(() -> new NotFoundServiceException("User not found"));

            return userMapper.userToUserDTO(existingUser);
        } else {
            throw new ServiceException("Anonymous user");
        }
    }
}
