package com.example.aplicatie_gestionare_voluntariat.model;

import java.time.LocalDate;

public class Volunteer {
    private Integer idVolunteer;
    private Integer idUser;
    private User user;

    private LocalDate birthDate;
    private String skills;
    private String availability;
    private String emergencyContact;

    public Volunteer() {}

    public Volunteer(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdVolunteer() { return idVolunteer; }
    public void setIdVolunteer(Integer idVolunteer) { this.idVolunteer = idVolunteer; }

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
}