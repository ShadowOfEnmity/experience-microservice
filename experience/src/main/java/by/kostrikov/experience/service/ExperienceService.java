package by.kostrikov.experience.service;

import by.kostrikov.experience.dto.ExperienceRequestDto;
import by.kostrikov.experience.dto.ExperienceResponseDto;

public interface ExperienceService {
    ExperienceResponseDto saveExperience(ExperienceRequestDto requestDto);
}
