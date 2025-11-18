package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.mapper.UserMapper;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.repo.UserRepo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    private UserService userService;
    private UserRepo userRepo;
    private UserMapper userMapper = UserMapper.INSTANCE;
    private User user;
    private User user2;
    private UserDTO userDTO;
    private UserDTO userDTO2;

    @BeforeEach
    void setUp() {
        userRepo = Mockito.mock(UserRepo.class);
        userService = new UserServiceImpl(userMapper, userRepo);

        user = new User();
        user.setId(1L);
        user.setEmail("test1@gmail.com");
        user.setRole("ROLE_CLIENT");

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@gmail.com");
        user2.setRole("ROLE_GUIDE");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test1@gmail.com");
        userDTO.setRole("ROLE_CLIENT");

        userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setEmail("test2@gmail.com");
        userDTO2.setRole("ROLE_GUIDE");
    }

    @Test
    void getAllUserTest_success() {
        when(userRepo.findAll()).thenReturn(Arrays.asList(user, user2));

        List<UserDTO> allUser = userService.getAllUser();

        assertNotNull(allUser);

        UserDTO firstUser = allUser.get(0);
        assertEquals(userDTO.getId(), firstUser.getId());
        assertEquals(userDTO.getEmail(), firstUser.getEmail());
        assertEquals(userDTO.getRole(), firstUser.getRole());

        UserDTO lastUser = allUser.get(1);
        assertEquals(userDTO2.getId(), lastUser.getId());
        assertEquals(userDTO2.getEmail(), lastUser.getEmail());
        assertEquals(userDTO2.getRole(), lastUser.getRole());
    }

    @Test
    void getAllUserTest_userNotFoundException() {
        when(userRepo.findAll()).thenReturn(List.of());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> userService.getAllUser());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserByIdTest_success() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));

        UserDTO userById = userService.getUserById(1L);

        assertNotNull(userById);
        assertEquals(userDTO.getId(), userById.getId());
        assertEquals(userDTO.getEmail(), userById.getEmail());
        assertEquals(userDTO.getRole(), userById.getRole());
    }

    @Test
    void getUserByIdTest_userNotFoundException() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> userService.getUserById(1L));
        assertEquals("User not found", exception.getMessage());
    }
}