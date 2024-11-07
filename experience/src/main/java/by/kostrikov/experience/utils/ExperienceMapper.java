package by.kostrikov.experience.utils;

import by.kostrikov.experience.dto.ExperienceRequestDto;
import by.kostrikov.experience.dto.ExperienceResponseDto;
import by.kostrikov.experience.persistence.model.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {
    // Преобразование из ExperienceRequestDto в Experience
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "industryId", ignore = true)
    @Mapping(target = "description", source = "description")
    // Устанавливаем industryId позже
    Experience toEntity(ExperienceRequestDto requestDto);

    // Преобразование из Experience в ExperienceResponseDto
    @Mapping(target = "industry", ignore = true)
    ExperienceResponseDto toResponseDto(Experience experience);
}
