package com.shopsphere.productservice.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.shopsphere.productservice.repository.write",
        entityManagerFactoryRef = "productWriteEMF",
        transactionManagerRef = "productWriteTM"
)
public class MasterDatasourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.write")
    protected DataSource masterDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    protected LocalContainerEntityManagerFactoryBean productWriteEMF(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(masterDatasource())
                .packages("com.shopsphere.productservice.entity")
                .persistenceUnit("productWrite")
                .build();
    }

    @Primary
    @Bean
    protected PlatformTransactionManager productWriteTM(
            @Qualifier(value = "productWriteEMF") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
