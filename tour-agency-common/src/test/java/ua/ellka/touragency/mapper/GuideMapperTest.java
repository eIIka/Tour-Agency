package ua.ellka.touragency.mapper;

import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.GuideDTO;
import ua.ellka.touragency.model.Guide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GuideMapperTest {

    @Test
    void guideToGuideDTO() {
        GuideDTO guideDTO = GuideMapper.INSTANCE.guideToGuideDTO(TestData.GUIDE);

        assertNotNull(guideDTO);
        assertEquals(TestData.GUIDE.getId(), guideDTO.getId());
        assertEquals(TestData.GUIDE.getName(), guideDTO.getName());
        assertEquals(TestData.GUIDE.getLanguage(), guideDTO.getLanguage());
        assertEquals(TestData.GUIDE.getUser().getEmail(), guideDTO.getEmail());
    }

    @Test
    void guideDTOToGuide() {
        Guide guide = GuideMapper.INSTANCE.guideDTOToGuide(TestData.GUIDE_DTO);

        assertNotNull(guide);
        assertEquals(TestData.GUIDE_DTO.getId(), guide.getId());
        assertEquals(TestData.GUIDE_DTO.getName(), guide.getName());
        assertEquals(TestData.GUIDE_DTO.getLanguage(), guide.getLanguage());
        assertEquals(TestData.GUIDE_DTO.getEmail(), guide.getUser().getEmail());
    }
}
