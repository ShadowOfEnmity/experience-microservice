package by.kostrikov.experience.dto;

public class ExperienceRequestDto {
    private String description;
    private IndustryRequestDto industry;

    public String getDescription() {
        return description;
    }

    public void setDescription(String name) {
        this.description = description;
    }

    public IndustryRequestDto getIndustry() {
        return industry;
    }

    public void setIndustry(IndustryRequestDto industry) {
        this.industry = industry;
    }

    public ExperienceRequestDto() {
    }
}
