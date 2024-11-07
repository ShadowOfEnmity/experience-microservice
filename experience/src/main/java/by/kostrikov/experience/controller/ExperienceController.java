package by.kostrikov.experience.controller;

import by.kostrikov.experience.dto.ExperienceRequestDto;
import by.kostrikov.experience.dto.ExperienceResponseDto;
import by.kostrikov.experience.service.ExperienceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/experience")
public class ExperienceController {
    private final ExperienceService experienceService;

    public ExperienceController(@Qualifier("experienceServiceImpl")ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping("/create")
    public ResponseEntity<ExperienceResponseDto> createExperience(@RequestBody ExperienceRequestDto requestDto) throws ExecutionException, InterruptedException {
        // Создание experience делегируется слою сервиса
        ExperienceResponseDto experienceResponse = experienceService.saveExperience(requestDto);
        return ResponseEntity.ok(experienceResponse);
    }
}
