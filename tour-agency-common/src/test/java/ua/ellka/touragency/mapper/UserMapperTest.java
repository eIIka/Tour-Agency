package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {
    private UserMapper userMapper;
    @BeforeEach
    public void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void userToUserDTO() {
        UserDTO userDTO = userMapper.userToUserDTO(TestData.USER);

        assertNotNull(userDTO);
        assertEquals(TestData.USER.getId(), userDTO.getId());
        assertEquals(TestData.USER.getEmail(), userDTO.getEmail());
        assertEquals(TestData.USER.getRole(), userDTO.getRole());
    }

    @Test
    void userDTOToUser() {
        User user = userMapper.userDTOToUser(TestData.USER_DTO);

        assertNotNull(user);
        assertEquals(TestData.USER_DTO.getId(), user.getId());
        assertEquals(TestData.USER_DTO.getEmail(), user.getEmail());
        assertEquals(TestData.USER_DTO.getRole(), user.getRole());
    }
}
