package com.example.aplicatie_gestionare_voluntariat.model;

import java.time.LocalDate;

public class Volunteer {

    private Integer idVolunteer;
    private Integer idUser;
    private LocalDate birthDate;
    private String skills;
    private String availability;
    private String emergencyContact;

    // Relație pentru afișare
    private User user;

    // Constructori
    public Volunteer() {}

    public Volunteer(Integer idUser) {
        this.idUser = idUser;
    }

    public Volunteer(Integer idUser, LocalDate birthDate, String skills,
                     String availability, String emergencyContact) {
        this.idUser = idUser;
        this.birthDate = birthDate;
        this.skills = skills;
        this.availability = availability;
        this.emergencyContact = emergencyContact;
    }

    // Getteri și Setteri
    public Integer getIdVolunteer() {
        return idVolunteer;
    }

    public void setIdVolunteer(Integer idVolunteer) {
        this.idVolunteer = idVolunteer;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}