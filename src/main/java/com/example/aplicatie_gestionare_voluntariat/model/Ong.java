package com.example.aplicatie_gestionare_voluntariat.model;

import java.time.LocalDate;

public class Ong {

    private Integer idOng;
    private String name;
    private String description;
    private String address;
    private String registrationNumber;
    private String phone;
    private String email;
    private LocalDate foundingDate;

    // Constructori
    public Ong() {}

    public Ong(String name, String description, String address, String registrationNumber,
               String phone, String email, LocalDate foundingDate) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.registrationNumber = registrationNumber;
        this.phone = phone;
        this.email = email;
        this.foundingDate = foundingDate;
    }

    // Getteri și Setteri
    public Integer getIdOng() {
        return idOng;
    }

    public void setIdOng(Integer idOng) {
        this.idOng = idOng;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFoundingDate() {
        return foundingDate;
    }

    public void setFoundingDate(LocalDate foundingDate) {
        this.foundingDate = foundingDate;
    }
}