package com.shopsphere.porductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class PorductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PorductServiceApplication.class, args);
    }

}
