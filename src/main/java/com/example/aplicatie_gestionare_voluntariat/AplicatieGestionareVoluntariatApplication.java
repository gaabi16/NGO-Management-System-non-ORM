package com.example.aplicatie_gestionare_voluntariat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AplicatieGestionareVoluntariatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AplicatieGestionareVoluntariatApplication.class, args);
    }

}