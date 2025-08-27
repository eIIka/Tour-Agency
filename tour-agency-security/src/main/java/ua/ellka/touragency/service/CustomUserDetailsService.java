package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.ellka.touragency.model.security.CustomUserDetails;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.repo.UserRepo;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email \"" + username + "\" not found"));

        return new CustomUserDetails(user);
    }
}
