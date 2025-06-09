package com.shopsphere.productservice.repository.write;

import com.shopsphere.productservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductWriteRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByProductNameStartsWithIgnoreCase(String productName);
}
