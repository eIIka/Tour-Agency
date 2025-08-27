package ua.ellka.touragency.service;

import ua.ellka.touragency.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUser();
    UserDTO getUserById(Long id);
}
