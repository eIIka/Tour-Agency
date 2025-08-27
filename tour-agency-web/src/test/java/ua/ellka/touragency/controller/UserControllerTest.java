package ua.ellka.touragency.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.ellka.touragency.dto.UserDTO;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerParentTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void getAllUsersTest_returnsOk() throws Exception {
        when(userService.getAllUser()).thenReturn(List.of(new UserDTO(), new UserDTO()));

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllUsersTest_returnsNotFound() throws Exception {
        when(userService.getAllUser()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserByIdTest_returnsOk() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(new UserDTO());

        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserByIdTest_returnsNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}