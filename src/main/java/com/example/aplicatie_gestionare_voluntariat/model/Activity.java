package com.example.aplicatie_gestionare_voluntariat.model;

import java.time.LocalDateTime;

public class Activity {
    private Integer idActivity;
    private Integer idCategory;
    private Integer idCoordinator;

    private String name;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer maxVolunteers;
    private String status;
    private Double donationsCollected;

    // Câmpuri tranzitorii (afișare)
    private String categoryName;
    private Integer pendingCount = 0;

    // Câmpuri pentru logica de voluntari (My Activities)
    private boolean enrolled = false;
    private String enrollmentStatus; // 'pending', 'accepted', 'rejected'

    // [NOU] Detalii Coordonator & ONG pentru afișare extinsă
    private String coordinatorName;
    private String coordinatorEmail;
    private String coordinatorPhone;
    private String ongName; // [NOU]

    public Activity() {}

    // Getters and Setters

    public Integer getIdActivity() { return idActivity; }
    public void setIdActivity(Integer idActivity) { this.idActivity = idActivity; }

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

    public Double getDonationsCollected() { return donationsCollected; }
    public void setDonationsCollected(Double donationsCollected) { this.donationsCollected = donationsCollected; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Integer getPendingCount() { return pendingCount; }
    public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }

    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    public String getCoordinatorName() { return coordinatorName; }
    public void setCoordinatorName(String coordinatorName) { this.coordinatorName = coordinatorName; }

    public String getCoordinatorEmail() { return coordinatorEmail; }
    public void setCoordinatorEmail(String coordinatorEmail) { this.coordinatorEmail = coordinatorEmail; }

    public String getCoordinatorPhone() { return coordinatorPhone; }
    public void setCoordinatorPhone(String coordinatorPhone) { this.coordinatorPhone = coordinatorPhone; }

    public String getOngName() { return ongName; } // [NOU]
    public void setOngName(String ongName) { this.ongName = ongName; } // [NOU]
}