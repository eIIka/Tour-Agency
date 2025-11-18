package ua.ellka.touragency.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.ellka.touragency.dto.AppInfoDTO;

@RestController
@RequestMapping("/v1/info")
public class InfoController {
    private static final String VERSION = System.getProperty("app.version");

    @GetMapping
    public ResponseEntity<AppInfoDTO> getInfo() {
        AppInfoDTO appInfoDTO = new AppInfoDTO();
        appInfoDTO.setAppVersion(VERSION);
        appInfoDTO.setAppName("Tour agency backend");
        return ResponseEntity.ok(appInfoDTO);
    }
}
