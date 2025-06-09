package com.shopsphere.productservice.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.shopsphere.productservice.repository.read",
        entityManagerFactoryRef = "productReadEMF",
        transactionManagerRef = "productReadTM"
)
public class ReplicaConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.read")
    protected DataSource readDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    protected LocalContainerEntityManagerFactoryBean productReadEMF(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(readDatasource())
                .packages("com.shopsphere.productservice.entity")
                .persistenceUnit("productRead")
                .build();
    }

    @Bean
    protected PlatformTransactionManager productReadTM(
            @Qualifier(value = "productReadEMF") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
