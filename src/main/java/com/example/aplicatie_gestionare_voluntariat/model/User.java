package com.example.aplicatie_gestionare_voluntariat.model;

public class User {

    private Integer idUser;
    private String email;
    private String passwordHash;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;

    public enum Role {
        volunteer, coordinator, admin
    }

    public User() {}

    public User(String email, String passwordHash, String firstName, String lastName, String phoneNumber, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public Integer getId_user() { return idUser; }
    public void setId_user(Integer idUser) { this.idUser = idUser; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getFirst_name() { return firstName; }
    public void setFirst_name(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getLast_name() { return lastName; }
    public void setLast_name(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPhone_number() { return phoneNumber; }
    public void setPhone_number(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}