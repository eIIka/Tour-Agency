package ua.ellka.touragency.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.ellka.touragency.dto.GuideDTO;
import ua.ellka.touragency.mapper.GuideMapper;
import ua.ellka.touragency.model.Guide;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.repo.GuideRepo;
import ua.ellka.touragency.repo.UserRepo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GuideServiceImplTest {
    private GuideService guideService;
    private GuideRepo guideRepo;
    private Guide guide;
    private GuideDTO guideDTO;
    private UserRepo userRepo;
    private User user;

    @BeforeEach
    void setUp() {
        GuideMapper guideMapper = GuideMapper.INSTANCE;
        guideRepo = mock(GuideRepo.class);
        userRepo = mock(UserRepo.class);
        guideService = new GuideServiceImpl(guideMapper, guideRepo, userRepo);

        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("test_password");
        user.setRole("ROLE_GUIDE");

        guide = new Guide();
        guide.setId(1L);
        guide.setName("Guide");
        guide.setLanguage("English");
        guide.setUser(user);

        guideDTO = new GuideDTO();
        guideDTO.setId(1L);
        guideDTO.setName("Guide");
        guideDTO.setLanguage("English");
        guideDTO.setEmail("test@gmail.com");
    }

    @Test
    void createGuideTest_success() {
        when(guideRepo.findByName(any())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user));
        when(guideRepo.save(any())).thenReturn(guide);

        GuideDTO created = guideService.createGuide(guideDTO);

        assertNotNull(created);
        assertEquals(created.getId(), guideDTO.getId());
        assertEquals(created.getName(), guideDTO.getName());
        assertEquals(created.getLanguage(), guideDTO.getLanguage());
    }

    @Test
    void createGuideTest_guideNameExistingException() {
        when(guideRepo.findByName(any())).thenReturn(Optional.of(guide));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> guideService.createGuide(guideDTO));
        assertEquals("Guide already exists", exception.getMessage());
    }

    @Test
    void createGuideTest_guideEmailNotFoundException() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> guideService.createGuide(guideDTO));
        assertEquals("User with email not found", exception.getMessage());
    }

    @Test
    void getAllGuidesTest_success() {
        when(guideRepo.count()).thenReturn(1L);
        when(guideRepo.findAll()).thenReturn(List.of(guide));

        List<GuideDTO> allGuides = guideService.getAllGuides();

        assertNotNull(allGuides);
        assertEquals(1, allGuides.size());
        assertEquals(allGuides.get(0).getId(), guideDTO.getId());
        assertEquals(allGuides.get(0).getName(), guideDTO.getName());
        assertEquals(allGuides.get(0).getLanguage(), guideDTO.getLanguage());
    }

    @Test
    void getAllGuidesTest_guideNotFoundException() {
        when(guideRepo.count()).thenReturn(0L);

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> guideService.getAllGuides());
        assertEquals("Guides not found", exception.getMessage());
    }

    @Test
    void updateGuideTest_success() {
        when(guideRepo.findById(anyLong())).thenReturn(Optional.of(guide));
        when(guideRepo.findByName(any())).thenReturn(Optional.empty());
        when(guideRepo.save(any())).thenReturn(guide);

        GuideDTO updated = guideService.updateGuide(1L, guideDTO);

        assertNotNull(updated);
        assertEquals(updated.getId(), guideDTO.getId());
        assertEquals(updated.getName(), guideDTO.getName());
        assertEquals(updated.getLanguage(), guideDTO.getLanguage());
    }

    @Test
    void updateGuideTest_guideNotFoundException() {
        when(guideRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> guideService.updateGuide(1L, guideDTO));
        assertEquals("Guide not found", exception.getMessage());
    }

    @Test
    void updateGuideTest_guideNameAlreadyExistingException() {
        Guide guide2 = new Guide();
        guide2.setId(2L);
        guide2.setName("Guide2");

        when(guideRepo.findById(anyLong())).thenReturn(Optional.of(guide));
        when(guideRepo.findByName(any())).thenReturn(Optional.of(guide2));

        ExistingServiceException exception = assertThrows(ExistingServiceException.class,
                () -> guideService.updateGuide(1L, guideDTO));
        assertEquals("Name already exists", exception.getMessage());
    }

    @Test
    void deleteGuideTest_success() {
        when(guideRepo.findById(anyLong())).thenReturn(Optional.of(guide));

        GuideDTO deleted = guideService.deleteGuide(1L);

        assertNotNull(deleted);
        assertEquals(deleted.getId(), guideDTO.getId());
        assertEquals(deleted.getName(), guideDTO.getName());
        assertEquals(deleted.getLanguage(), guideDTO.getLanguage());
    }

    @Test
    void deleteGuideTest_guideNotFoundException() {
        when(guideRepo.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundServiceException exception = assertThrows(NotFoundServiceException.class,
                () -> guideService.deleteGuide(1L));
        assertEquals("Guide not found", exception.getMessage());
    }

}