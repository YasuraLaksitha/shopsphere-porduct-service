package com.shopsphere.porductservice;

import com.shopsphere.porductservice.dto.ContactDetailsDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableConfigurationProperties(value = {ContactDetailsDTO.class})
public class PorductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PorductServiceApplication.class, args);
    }

}
