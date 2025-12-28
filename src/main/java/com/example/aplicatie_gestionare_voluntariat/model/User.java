package com.example.aplicatie_gestionare_voluntariat.model;

public class User {

    private Integer id_user;
    private String email;
    private String passwordHash;
    private String password; // câmp temporar pentru înregistrare
    private String first_name;
    private String last_name;
    private String phone_number;
    private Role role;

    public enum Role {
        volunteer, coordinator, admin
    }

    // Constructor fără parametri
    public User() {}

    // Constructor cu parametri
    public User(String email, String passwordHash, String first_name, String last_name, String phone_number, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.role = role;
    }

    // Getteri și setteri
    public Integer getId_user() { return id_user; }
    public void setId_user(Integer id_user) { this.id_user = id_user; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }
    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }
    public String getPhone_number() { return phone_number; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}