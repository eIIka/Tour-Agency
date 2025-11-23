package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.mapper.UserMapper;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.model.security.TourAgencyUserDetails;
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

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public UserDTO deleteUser(Long userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;

        if (principal instanceof TourAgencyUserDetails userDetails) {
            currentUserId = userDetails.getId();
        }

        if (currentUserId != null && currentUserId.equals(userId)) {

            throw new org.springframework.security.access.AccessDeniedException("Admins cannot delete their own account.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundServiceException("User not found"));


        userRepo.deleteById(userId);

        return userMapper.userToUserDTO(user);
    }
}
