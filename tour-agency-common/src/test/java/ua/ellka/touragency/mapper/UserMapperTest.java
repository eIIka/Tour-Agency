package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    @Test
    void userToUserDTO() {
        UserDTO userDTO = UserMapper.INSTANCE.userToUserDTO(TestData.USER);

        assertNotNull(userDTO);
        assertEquals(TestData.USER.getId(), userDTO.getId());
        assertEquals(TestData.USER.getEmail(), userDTO.getEmail());
        assertEquals(TestData.USER.getRole(), userDTO.getRole());
    }

    @Test
    void userDTOToUser() {
        User user = UserMapper.INSTANCE.userDTOToUser(TestData.USER_DTO);

        assertNotNull(user);
        assertEquals(TestData.USER_DTO.getId(), user.getId());
        assertEquals(TestData.USER_DTO.getEmail(), user.getEmail());
        assertEquals(TestData.USER_DTO.getRole(), user.getRole());
    }
}
