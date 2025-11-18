package ua.ellka.touragency.service;

import ua.ellka.touragency.dto.GuideDTO;

import java.util.List;

public interface GuideService {
    GuideDTO createGuide(GuideDTO guideDTO);
    List<GuideDTO> getAllGuides();
    GuideDTO updateGuide(Long id, GuideDTO guideDTO);
    GuideDTO deleteGuide(Long id);
}
