package com.shopsphere.porductservice.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.porductservice.dto.CategoryDTO;
import com.shopsphere.porductservice.dto.PaginationResponseDTO;
import com.shopsphere.porductservice.entity.CategoryEntity;
import com.shopsphere.porductservice.exceptions.NoModificationRequiredException;
import com.shopsphere.porductservice.exceptions.ResourceAlreadyExistException;
import com.shopsphere.porductservice.exceptions.ResourceNotFoundException;
import com.shopsphere.porductservice.repository.CategoryRepository;
import com.shopsphere.porductservice.service.ICategoryService;
import com.shopsphere.porductservice.utils.ApplicationConstants;
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

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void persistCategory(final CategoryDTO category) {
        categoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName()).ifPresent((existingCategory) -> {
            throw new ResourceAlreadyExistException("Category", "Category name", existingCategory.getCategoryName());
        });

        if (StringUtil.isNullOrEmpty(category.getCategoryDescription()))
            category.setCategoryDescription(category.getCategoryName());

        categoryRepository.save(objectMapper.convertValue(category, CategoryEntity.class));
    }

    @Override
    public CategoryDTO retrieveCategoryByName(final String categoryName) {
        final CategoryEntity categoryEntity =
                categoryRepository.findByCategoryNameIgnoreCase(categoryName).orElseThrow(
                        () -> new ResourceNotFoundException("Category", "category name", categoryName)
                );
        return objectMapper.convertValue(categoryEntity, CategoryDTO.class);
    }

    @Override
    public PaginationResponseDTO<List<CategoryDTO>> retrieveAllCategories(final String orderBy, final String sortOrder,
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
        final Sort.Direction sortDirection = sortOrder.equalsIgnoreCase(ApplicationConstants.CATEGORY_SORT_ORDER) ?
                Sort.Direction.ASC :
                Sort.Direction.DESC;
        final Sort sortOrderBy = Sort.by(sortDirection, orderBy);

        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sortOrderBy);
        final Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(spec, pageRequest);
        final List<CategoryDTO> categoryDTOList = categoryEntityPage.getContent().stream()
                .map(categoryEntity -> objectMapper.convertValue(categoryEntity, CategoryDTO.class)).toList();

        return PaginationResponseDTO.<List<CategoryDTO>>builder()
                .data(categoryDTOList)
                .sortOrder(sortOrder)
                .orderBy(orderBy)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .isLastPage(categoryEntityPage.isLast())
                .build();
    }

    @Override
    public void updateCategoryByName(final CategoryDTO category) {
        final CategoryEntity categoryEntity =
                categoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Category", "category name", category.getCategoryName())
                        );
        if (categoryEntity.getCategoryDescription().equals(category.getCategoryDescription())) {
            throw new NoModificationRequiredException("Category", "category name", category.getCategoryName());
        }
        categoryEntity.setCategoryDescription(category.getCategoryDescription());
        categoryRepository.save(categoryEntity);
    }
}
