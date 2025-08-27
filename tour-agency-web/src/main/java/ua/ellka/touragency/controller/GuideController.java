package ua.ellka.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.ellka.touragency.dto.GuideDTO;
import ua.ellka.touragency.service.GuideService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/guide")
public class GuideController {
    private final GuideService guideService;

    @GetMapping
    public ResponseEntity<List<GuideDTO>> getAllGuides() {
        List<GuideDTO> allGuides = guideService.getAllGuides();

        return ResponseEntity.ok(allGuides);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuideDTO> createGuide(@RequestBody GuideDTO guideDTO) {
        GuideDTO createGuide = guideService.createGuide(guideDTO);

        return ResponseEntity.ok(createGuide);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuideDTO> updateGuide(@PathVariable(name = "id") Long id, @RequestBody GuideDTO guideDTO) {
        GuideDTO updateGuide = guideService.updateGuide(id, guideDTO);

        return ResponseEntity.ok(updateGuide);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GuideDTO> deleteGuide(@PathVariable(name = "id") Long id) {
        GuideDTO deleteGuide = guideService.deleteGuide(id);

        return ResponseEntity.ok(deleteGuide);
    }
}
