package by.kostrikov.ws.core;

public class CreateIndustryEvent {
    private String name;


    public CreateIndustryEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CreateIndustryEvent() {
    }
}
