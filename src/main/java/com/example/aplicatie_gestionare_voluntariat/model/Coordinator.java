package com.example.aplicatie_gestionare_voluntariat.model;

public class Coordinator {

    private Integer idCoordinator;
    private Integer idUser;
    private String ongRegistrationNumber;
    private String department;
    private Integer experienceYears;
    private String employmentType;

    private User user;
    private Ong ong;

    public Coordinator() {}

    public Integer getIdCoordinator() { return idCoordinator; }
    public void setIdCoordinator(Integer idCoordinator) { this.idCoordinator = idCoordinator; }

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public String getOngRegistrationNumber() { return ongRegistrationNumber; }
    public void setOngRegistrationNumber(String ongRegistrationNumber) { this.ongRegistrationNumber = ongRegistrationNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Ong getOng() { return ong; }
    public void setOng(Ong ong) { this.ong = ong; }
}