package com.shopsphere.productservice;

import com.shopsphere.productservice.dto.ContactDetailsDTO;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableConfigurationProperties(value = {ContactDetailsDTO.class})
@EnableCaching
@OpenAPIDefinition(
        info = @Info(
                title = "Product Service REST API Documentation",
                version = "v1",
                description = "ShopSphere service for perform product operations",
                contact = @Contact(
                        name = "Yasura Laksitha",
                        email = "yasura.dev@gmail.com",
                        url = "https://github.com/YasuraLaksitha/shopsphere-product-service.git"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Postman collection for product API operations",
                url = "https://github.com/YasuraLaksitha/shopsphere-product-service.git"
        )
)
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

}
