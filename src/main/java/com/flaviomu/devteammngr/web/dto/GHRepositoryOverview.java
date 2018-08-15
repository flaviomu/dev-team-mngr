package com.flaviomu.devteammngr.web.dto;

/**
 * Defines a GitHub repository overview
 *
 */
public class GHRepositoryOverview {

    private String ownerName;
    private String name;
    private String description;
    private String language;

    public GHRepositoryOverview() {}

    public GHRepositoryOverview(String ownerName, String name, String description, String language) {
        this.ownerName = ownerName;
        this.name = name;
        this.description = description;
        this.language = language;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    @Override
    public String toString() {
        return "GHRepositoryOverview{" +
                "ownerName='" + ownerName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                '}';
    }

}
