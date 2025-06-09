package com.shopsphere.productservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.productservice.dto.CategoryDTO;
import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.entity.CategoryEntity;
import com.shopsphere.productservice.exceptions.ResourceNotFoundException;
import com.shopsphere.productservice.repository.read.CategoryRepository;
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

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    @Override
    public CategoryDTO retrieveCategoryByName(final String categoryName) {
        final CategoryEntity categoryEntity =
                categoryRepository.findByCategoryNameIgnoreCase(categoryName).orElseThrow(
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
        final Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(spec, pageRequest);
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
