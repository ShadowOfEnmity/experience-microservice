package by.kostrikov.experience.dto;

public class ExperienceResponseDto {
    private String description;
    private IndustryResponseDto industry;

    public ExperienceResponseDto() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IndustryResponseDto getIndustry() {
        return industry;
    }

    public void setIndustry(IndustryResponseDto industry) {
        this.industry = industry;
    }
}
