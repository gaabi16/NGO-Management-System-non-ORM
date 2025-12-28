package com.example.aplicatie_gestionare_voluntariat.model;

public class Coordinator {

    private Integer idCoordinator;
    private Integer idUser;
    private Integer idOng;
    private String department;
    private Integer experienceYears;
    private String employmentType;

    // Relații pentru afișare
    private User user;
    private Ong ong;

    // Constructori
    public Coordinator() {}

    public Coordinator(Integer idUser, Integer idOng, String department,
                       Integer experienceYears, String employmentType) {
        this.idUser = idUser;
        this.idOng = idOng;
        this.department = department;
        this.experienceYears = experienceYears;
        this.employmentType = employmentType;
    }

    // Getteri și Setteri
    public Integer getIdCoordinator() {
        return idCoordinator;
    }

    public void setIdCoordinator(Integer idCoordinator) {
        this.idCoordinator = idCoordinator;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdOng() {
        return idOng;
    }

    public void setIdOng(Integer idOng) {
        this.idOng = idOng;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ong getOng() {
        return ong;
    }

    public void setOng(Ong ong) {
        this.ong = ong;
    }
}