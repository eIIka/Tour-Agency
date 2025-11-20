//package ua.ellka.touragency.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ua.ellka.touragency.dto.AuthDTO;
//import ua.ellka.touragency.dto.LoginDTO;
//import ua.ellka.touragency.dto.RegisterDTO;
//import ua.ellka.touragency.dto.UserDTO;
//import ua.ellka.touragency.service.AuthService;
//
//@RestController
//@RequestMapping("/v1/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final AuthService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<UserDTO> register(@RequestBody RegisterDTO registerDTO) {
//        UserDTO userDTO = authService.registerUser(registerDTO);
//        return ResponseEntity.ok(userDTO);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthDTO> login(@RequestBody LoginDTO loginDTO) {
//        AuthDTO authDTO = authService.authenticateUser(loginDTO);
//        return ResponseEntity.ok(authDTO);
//    }
//
//    //TODO realize method in service
//    @PostMapping("/logout")
//    public ResponseEntity<UserDTO> logout() {
//        UserDTO userDTO = authService.logoutUser();
//        return ResponseEntity.ok(userDTO);
//    }
//
//    @GetMapping("/current")
//    public ResponseEntity<UserDTO> getCurrentUser() {
//        UserDTO currentUser = authService.getCurrentUser();
//        return ResponseEntity.ok(currentUser);
//    }
//}
