package com.example.aplicatie_gestionare_voluntariat.model;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class Donation {
    private Integer idDonation;
    private String ongRegistrationNumber;
    private String donorName;
    private Double amount;

    // [NOU] Câmp pentru moneda selectată (nu se salvează în DB, se folosește la calcule)
    private String currency;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate donationDate;

    private String type; // e.g., 'cash', 'transfer'
    private String notes;

    public Donation() {}

    // Getters and Setters
    public Integer getIdDonation() { return idDonation; }
    public void setIdDonation(Integer idDonation) { this.idDonation = idDonation; }

    public String getOngRegistrationNumber() { return ongRegistrationNumber; }
    public void setOngRegistrationNumber(String ongRegistrationNumber) { this.ongRegistrationNumber = ongRegistrationNumber; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}