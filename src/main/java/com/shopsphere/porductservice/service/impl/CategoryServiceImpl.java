package com.shopsphere.porductservice.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.porductservice.dto.CategoryDTO;
import com.shopsphere.porductservice.entity.CategoryEntity;
import com.shopsphere.porductservice.exceptions.ResourceAlreadyExistException;
import com.shopsphere.porductservice.repository.CategoryRepository;
import com.shopsphere.porductservice.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void persistCategory(final CategoryDTO category) {
        categoryRepository.findByCategoryName(category.getCategoryName()).ifPresent((existingCategory) -> {
            throw new ResourceAlreadyExistException("Category", "Category name", existingCategory.getCategoryName());
        });

        if (StringUtil.isNullOrEmpty(category.getCategoryDescription()))
            category.setCategoryDescription(category.getCategoryName());

        categoryRepository.save(objectMapper.convertValue(category, CategoryEntity.class));
    }
}
