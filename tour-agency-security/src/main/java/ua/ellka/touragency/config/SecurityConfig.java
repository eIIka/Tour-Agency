package ua.ellka.touragency.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.ellka.touragency.auth.AuthEntryPointJwt;
import ua.ellka.touragency.auth.GoogleAuthErrorHandler;
import ua.ellka.touragency.auth.GoogleAuthSuccessHandler;
import ua.ellka.touragency.client.GoogleAccessResponseClient;
import ua.ellka.touragency.filter.JwtFilter;
import ua.ellka.touragency.service.GoogleOAuth2UserService;
import ua.ellka.touragency.service.GoogleOidcUserService;
import ua.ellka.touragency.util.JwtUtil;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final GoogleOAuth2UserService googleOAuth2UserService;
    private final GoogleOidcUserService googleOidcUserService;
    private final GoogleAccessResponseClient googleAccessResponseClient;
    private final GoogleAuthErrorHandler googleAuthErrorHandler;
    private final GoogleAuthSuccessHandler googleAuthSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v1/auth/**",
                                "/v1/auth/oauth2/**",
                                "/v1/info",
                                "/oauth2/**"
                        ).permitAll()


                        .requestMatchers(HttpMethod.GET, "/v1/tour/profit/**").hasAnyRole("GUIDE", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/v1/country/**", "/v1/guide", "/v1/tour/**")
                        .hasAnyRole("CLIENT", "GUIDE", "ADMIN")

                        .requestMatchers("/v1/users/**", "/v1/country/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/v1/client").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/v1/client/**", "/v1/booking/statisticsByMonth")
                        .hasAnyRole("ADMIN", "GUIDE")

                        .requestMatchers("/v1/client/**").hasRole("CLIENT")

                        .requestMatchers("/v1/guide/**", "/v1/tour/**").hasRole("GUIDE")

                        .requestMatchers(HttpMethod.GET, "/v1/booking/tour/**")
                        .hasAnyRole("CLIENT", "GUIDE", "ADMIN")

                        .requestMatchers("/v1/booking/**").hasRole("CLIENT")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(conf ->
                        conf.authenticationEntryPoint(new AuthEntryPointJwt()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(conf ->
                        conf
                                .authorizationEndpoint(end ->
                                        end.baseUri("/v1/auth/oauth2"))
                                .redirectionEndpoint(redirect ->
                                        redirect.baseUri("/v1/auth/google/callback"))

                                .successHandler(googleAuthSuccessHandler)
                                .failureHandler(googleAuthErrorHandler)

                                .tokenEndpoint(token ->
                                        token.accessTokenResponseClient(googleAccessResponseClient))

                                .userInfoEndpoint(user ->
                                        user
                                                .userService(googleOAuth2UserService)
                                                .oidcUserService(googleOidcUserService))
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
