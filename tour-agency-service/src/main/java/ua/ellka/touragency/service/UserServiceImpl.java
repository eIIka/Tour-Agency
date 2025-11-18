package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.mapper.UserMapper;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.repo.UserRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepo userRepo;

    @Override
    public List<UserDTO> getAllUser() {
        List<User> allUsers = userRepo.findAll();
        if (allUsers.isEmpty()) {
            throw new NotFoundServiceException("User not found");
        }

        return allUsers.stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("User not found"));

        return userMapper.userToUserDTO(user);
    }
}
