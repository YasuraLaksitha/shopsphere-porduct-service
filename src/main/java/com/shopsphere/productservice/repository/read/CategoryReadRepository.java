package com.shopsphere.productservice.repository.read;

import com.shopsphere.productservice.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryReadRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {

    Optional<CategoryEntity> findByCategoryNameIgnoreCase(String categoryName);
}
