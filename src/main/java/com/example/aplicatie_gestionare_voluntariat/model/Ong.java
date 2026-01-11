package com.example.aplicatie_gestionare_voluntariat.model;

import java.time.LocalDate;

public class Ong {

    private String registrationNumber; // PK (String)
    private String name;
    private String description;
    private String address;
    private String country;
    private String phone;
    private String email;
    private LocalDate foundingDate;
    private String imageUrl; // Camp nou

    public Ong() {}

    // Getteri și Setteri
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFoundingDate() { return foundingDate; }
    public void setFoundingDate(LocalDate foundingDate) { this.foundingDate = foundingDate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Metodă de compatibilitate
    public String getIdOng() { return registrationNumber; }
}