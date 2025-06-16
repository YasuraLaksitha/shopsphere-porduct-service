package com.shopsphere.productservice.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.productservice.dto.CategoryDTO;
import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.entity.CategoryEntity;
import com.shopsphere.productservice.exceptions.NoModificationRequiredException;
import com.shopsphere.productservice.exceptions.ResourceAlreadyExistException;
import com.shopsphere.productservice.exceptions.ResourceAlreadyUnavailableException;
import com.shopsphere.productservice.exceptions.ResourceNotFoundException;
import com.shopsphere.productservice.repository.read.CategoryReadRepository;
import com.shopsphere.productservice.repository.write.CategoryWriteRepository;
import com.shopsphere.productservice.service.ICategoryService;
import com.shopsphere.productservice.utils.ApplicationDefaultConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryReadRepository categoryReadRepository;

    private final CategoryWriteRepository categoryWriteRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void persistCategory(final CategoryDTO category) {
        categoryWriteRepository.findByCategoryNameIgnoreCase(category.getCategoryName()).ifPresent((existingCategory) -> {
            throw new ResourceAlreadyExistException("Category", "Category name", existingCategory.getCategoryName());
        });

        if (StringUtil.isNullOrEmpty(category.getCategoryDescription()))
            category.setCategoryDescription(category.getCategoryName());

        categoryWriteRepository.save(objectMapper.convertValue(category, CategoryEntity.class));
    }

    @Override
    public void updateCategoryByName(final CategoryDTO category) {
        final CategoryEntity categoryEntity =
                categoryWriteRepository.findByCategoryNameIgnoreCase(category.getCategoryName())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Category", "category name", category.getCategoryName())
                        );
        if (categoryEntity.getCategoryDescription().equals(category.getCategoryDescription())) {
            throw new NoModificationRequiredException("Category", "category name", category.getCategoryName());
        }
        categoryEntity.setCategoryDescription(category.getCategoryDescription());
        categoryWriteRepository.save(categoryEntity);
    }

    @Override
    public boolean disableCategoryByName(final String categoryName) {
        final CategoryEntity categoryEntity =
                categoryWriteRepository.findByCategoryNameIgnoreCase(categoryName).orElseThrow(
                        () -> new ResourceNotFoundException("Category", "category name", categoryName)
                );

        if (categoryEntity.isUnavailable())
            throw new ResourceAlreadyUnavailableException("Category", "category name", categoryName);

        categoryEntity.setUnavailable(true);
        categoryWriteRepository.save(categoryEntity);

        return true;
    }

    @Override
    public boolean enableCategoryByName(String categoryName) {
        final CategoryEntity categoryEntity =
                categoryWriteRepository.findByCategoryNameIgnoreCase(categoryName).orElseThrow(
                        () -> new ResourceNotFoundException("Category", "category name", categoryName)
                );

        if (!categoryEntity.isUnavailable())
            throw new ResourceAlreadyExistException("Category", "category name", categoryName);

        categoryEntity.setUnavailable(false);
        categoryWriteRepository.save(categoryEntity);

        return true;
    }

    @Override
    public CategoryDTO retrieveCategoryByName(final String categoryName) {
        final CategoryEntity categoryEntity =
                categoryReadRepository.findByCategoryNameIgnoreCase(categoryName).orElseThrow(
                        () -> new ResourceNotFoundException("Category", "category name", categoryName)
                );
        return objectMapper.convertValue(categoryEntity, CategoryDTO.class);
    }

    @Override
    public PaginationResponseDTO<List<CategoryDTO>> retrieveAllCategories(final String sortBy, final String sortOrder,
                                                                          final int pageNumber, final int pageSize,
                                                                          final String keyword) {
        Specification<CategoryEntity> spec = Specification.where(null);

        spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isUnavailable")));

        if (StringUtils.hasText(keyword)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("categoryName")),
                            keyword.toLowerCase() + "%"
                    )
            );
        }
        final Sort.Direction sortDirection = sortOrder.equalsIgnoreCase(ApplicationDefaultConstants.CATEGORY_SORT_ORDER) ?
                Sort.Direction.ASC :
                Sort.Direction.DESC;
        final Sort sortOrderBy = Sort.by(sortDirection, sortBy);

        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sortOrderBy);
        final Page<CategoryEntity> categoryEntityPage = categoryReadRepository.findAll(spec, pageRequest);
        final List<CategoryDTO> categoryDTOList = categoryEntityPage.getContent().stream()
                .map(categoryEntity -> objectMapper.convertValue(categoryEntity, CategoryDTO.class)).toList();

        return PaginationResponseDTO.<List<CategoryDTO>>builder()
                .data(categoryDTOList)
                .sortOrder(sortOrder)
                .sortBy(sortBy)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .isLastPage(categoryEntityPage.isLast())
                .build();
    }
}
