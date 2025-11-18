package ua.ellka.touragency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ua.ellka.touragency.dto.GuideDTO;
import ua.ellka.touragency.exception.ExistingServiceException;
import ua.ellka.touragency.exception.NotFoundServiceException;
import ua.ellka.touragency.exception.ServiceException;
import ua.ellka.touragency.mapper.GuideMapper;
import ua.ellka.touragency.model.Guide;
import ua.ellka.touragency.model.User;
import ua.ellka.touragency.repo.GuideRepo;
import ua.ellka.touragency.repo.UserRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideMapper guideMapper;
    private final GuideRepo guideRepo;
    private final UserRepo userRepo;

    //12
    @Override
    public GuideDTO createGuide(GuideDTO guideDTO) {
        Guide guide = guideMapper.guideDTOToGuide(guideDTO);

        guideRepo.findByName(guide.getName())
                .ifPresent(exists -> {
                    throw new ExistingServiceException("Guide already exists");
                });

        User user = userRepo.findByEmail(guideDTO.getEmail())
                .orElseThrow(() -> new NotFoundServiceException("User with email not found"));
        guide.setUser(user);

        try {
            Guide save = guideRepo.save(guide);
            return guideMapper.guideToGuideDTO(save);
        }catch (DataAccessException e) {
            throw new ServiceException("Error while creating guide: " + e.getMessage());
        }
    }

    //13
    @Override
    public List<GuideDTO> getAllGuides() {
        if (guideRepo.count() == 0) {
            throw new NotFoundServiceException("Guides not found");
        }

        return guideRepo.findAll().stream()
                .map(guideMapper::guideToGuideDTO)
                .toList();
    }

    //14
    @Override
    //@PreAuthorize("@accessChecker.isGuideOwner(#id)")
    public GuideDTO updateGuide(Long id, GuideDTO guideDTO) {
        Guide updatedGuide = guideRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Guide not found"));

        guideRepo.findByName(guideDTO.getName())
                .filter(g -> !g.getId().equals(id))
                .ifPresent(exists -> {
                    throw new ExistingServiceException("Name already exists");
                });

        updatedGuide.setId(id);
        updatedGuide.setName(guideDTO.getName());
        updatedGuide.setLanguage(guideDTO.getLanguage());

        try {
            Guide save = guideRepo.save(updatedGuide);
            return guideMapper.guideToGuideDTO(save);
        }catch (DataAccessException e) {
            throw new ServiceException("Error while updating guide: " + e.getMessage());
        }
    }

    //15
    @Override
    //@PreAuthorize("@accessChecker.isGuideOwner(#id)")
    public GuideDTO deleteGuide(Long id) {
        Guide existingGuide = guideRepo.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Guide not found"));

        User user = existingGuide.getUser();

        try {
            guideRepo.delete(existingGuide);
            userRepo.delete(user);
            return guideMapper.guideToGuideDTO(existingGuide);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete guide due to database error");
        }
    }
}
