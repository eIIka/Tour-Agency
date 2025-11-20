package ua.ellka.touragency.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.model.security.TourAgencyUserDetails;
import ua.ellka.touragency.repo.UserRepo;
import ua.ellka.touragency.util.JwtUtil;

import java.io.IOException;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class GoogleAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${auth.success-redirect-url}")
    private String successRedirectUrl;

    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");

        User user = getOrCreateUser(email);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User could not be created");
            return;
        }

        String token = jwtUtil.generateToken(new TourAgencyUserDetails(user));
        response.sendRedirect(successRedirectUrl + "/" + token);
    }

    private User getOrCreateUser(String email) {
        return userRepo.findByEmail(email).orElseGet(() -> createUser(email));
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setRole("ROLE_USER");
        user.setPassword("password");

        return userRepo.save(user);
    }
}
