package ua.ellka.touragency.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ua.ellka.touragency.config.WebTestConfig;
import ua.ellka.touragency.exception.GlobalExceptionHandler;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebTestConfig.class)
public class ControllerParentTest {
    protected MockMvc mockMvc;

    @Autowired
    protected GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    protected ObjectMapper objectMapper;
}
