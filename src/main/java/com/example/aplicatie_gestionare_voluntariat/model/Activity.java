package com.example.aplicatie_gestionare_voluntariat.model;

import java.time.LocalDateTime;

public class Activity {
    private Integer idActivity;
    private Integer idOng;
    private Integer idCategory;
    private Integer idCoordinator;
    private String name;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxVolunteers;
    private String status;

    // Câmpuri extra pentru afișare (populate prin JOIN-uri)
    private String categoryName;

    public Activity() {}

    // Getteri și Setteri
    public Integer getIdActivity() { return idActivity; }
    public void setIdActivity(Integer idActivity) { this.idActivity = idActivity; }

    public Integer getIdOng() { return idOng; }
    public void setIdOng(Integer idOng) { this.idOng = idOng; }

    public Integer getIdCategory() { return idCategory; }
    public void setIdCategory(Integer idCategory) { this.idCategory = idCategory; }

    public Integer getIdCoordinator() { return idCoordinator; }
    public void setIdCoordinator(Integer idCoordinator) { this.idCoordinator = idCoordinator; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getMaxVolunteers() { return maxVolunteers; }
    public void setMaxVolunteers(Integer maxVolunteers) { this.maxVolunteers = maxVolunteers; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}