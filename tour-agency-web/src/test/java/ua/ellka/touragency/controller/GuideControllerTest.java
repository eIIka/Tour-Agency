package ua.ellka.touragency.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.ellka.touragency.dto.GuideDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.service.GuideService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class GuideControllerTest extends ControllerParentTest {
    @Mock
    private GuideService guideService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        GuideController guideController = new GuideController(guideService);

        mockMvc = MockMvcBuilders.standaloneSetup(guideController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void getAllGuidesTest_returnsOk() throws Exception {
        when(guideService.getAllGuides()).thenReturn(List.of(new GuideDTO()));

        mockMvc.perform(get("/v1/guide"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllGuidesTest_returnsNotFound() throws Exception {
        when(guideService.getAllGuides()).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(get("/v1/guide"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createGuideTest_returnsOk() throws Exception {
        when(guideService.createGuide(any())).thenReturn(new GuideDTO());

        GuideDTO guideDTO = new GuideDTO();
        guideDTO.setName("test name");
        guideDTO.setLanguage("test language");

        String reqBody = objectMapper.writeValueAsString(guideDTO);
        mockMvc.perform(
                        post("/v1/guide")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createGuideTest_returnsConflict() throws Exception {
        when(guideService.createGuide(any())).thenThrow(ExistingServiceException.class);

        GuideDTO guideDTO = new GuideDTO();
        guideDTO.setName("test name");
        guideDTO.setLanguage("test language");

        String reqBody = objectMapper.writeValueAsString(guideDTO);
        mockMvc.perform(
                        post("/v1/guide")
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateGuideTest_returnsOk() throws Exception {
        when(guideService.updateGuide(anyLong(), any())).thenReturn(new GuideDTO());

        GuideDTO guideDTO = new GuideDTO();
        guideDTO.setName("test name");
        guideDTO.setLanguage("test language");

        String reqBody = objectMapper.writeValueAsString(guideDTO);
        mockMvc.perform(
                        put("/v1/guide/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateGuideTest_returnsNotFound() throws Exception {
        when(guideService.updateGuide(anyLong(), any())).thenThrow(NotFoundServiceException.class);

        GuideDTO guideDTO = new GuideDTO();
        guideDTO.setName("test name");
        guideDTO.setLanguage("test language");

        String reqBody = objectMapper.writeValueAsString(guideDTO);
        mockMvc.perform(
                        put("/v1/guide/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateGuideTest_returnsConflict() throws Exception {
        when(guideService.updateGuide(anyLong(), any())).thenThrow(ExistingServiceException.class);

        GuideDTO guideDTO = new GuideDTO();
        guideDTO.setName("test name");
        guideDTO.setLanguage("test language");

        String reqBody = objectMapper.writeValueAsString(guideDTO);
        mockMvc.perform(
                        put("/v1/guide/{id}", 1L)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteGuideTest_returnsOk() throws Exception {
        when(guideService.deleteGuide(anyLong())).thenReturn(new GuideDTO());

        mockMvc.perform(delete("/v1/guide/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteGuideTest_returnsNotFound() throws Exception {
        when(guideService.deleteGuide(anyLong())).thenThrow(NotFoundServiceException.class);

        mockMvc.perform(delete("/v1/guide/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}