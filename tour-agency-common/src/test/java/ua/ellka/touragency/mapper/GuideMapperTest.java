package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.ellka.touragency.dto.GuideDTO;
import ua.ellka.touragency.model.Guide;

import static org.junit.jupiter.api.Assertions.*;

class GuideMapperTest {
    private GuideMapper guideMapper;
    @BeforeEach
    void setUp() {
        guideMapper = Mappers.getMapper(GuideMapper.class);
    }

    @Test
    void guideToGuideDTO() {
        GuideDTO guideDTO = guideMapper.guideToGuideDTO(TestData.GUIDE);

        assertNotNull(guideDTO);
        assertEquals(TestData.GUIDE.getId(), guideDTO.getId());
        assertEquals(TestData.GUIDE.getName(), guideDTO.getName());
        assertEquals(TestData.GUIDE.getLanguage(), guideDTO.getLanguage());
        assertEquals(TestData.GUIDE.getUser().getEmail(), guideDTO.getEmail());
    }

    @Test
    void guideDTOToGuide() {
        Guide guide = guideMapper.guideDTOToGuide(TestData.GUIDE_DTO);

        assertNotNull(guide);
        assertEquals(TestData.GUIDE_DTO.getId(), guide.getId());
        assertEquals(TestData.GUIDE_DTO.getName(), guide.getName());
        assertEquals(TestData.GUIDE_DTO.getLanguage(), guide.getLanguage());
        assertEquals(TestData.GUIDE_DTO.getEmail(), guide.getUser().getEmail());
    }
}