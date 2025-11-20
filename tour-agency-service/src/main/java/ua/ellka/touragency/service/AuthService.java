package ua.ellka.touragency.service;


import ua.ellka.touragency.dto.AuthDTO;
import ua.ellka.touragency.dto.LoginDTO;
import ua.ellka.touragency.dto.RegisterDTO;
import ua.ellka.touragency.dto.UserDTO;

public interface AuthService {
    UserDTO registerUser(RegisterDTO registerDTO);
    AuthDTO authenticateUser(LoginDTO loginDTO);
    UserDTO logoutUser();
    UserDTO getCurrentUser();
}
