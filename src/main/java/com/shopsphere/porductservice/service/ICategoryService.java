package com.shopsphere.porductservice.service;

import com.shopsphere.porductservice.dto.CategoryDTO;
import com.shopsphere.porductservice.dto.PaginationResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface ICategoryService {

    /**
     *
     * @param category - categoryDTO object
     */
    void persistCategory(CategoryDTO category);

    /**
     *
     * @param categoryName - name of category
     * @return categoryDTO
     */
    CategoryDTO retrieveCategoryByName(final String categoryName);

    /**
     *
     * @return List of categoryDTOs
     */
    PaginationResponseDTO<List<CategoryDTO>> retrieveAllCategories(final String orderBy, final String sortOrder,
                                                                   final int pageNumber, final int pageSize,
                                                                   final String keyword);

    /**
     *
     * @param category
     */
    void updateCategoryByName(@Valid CategoryDTO category);

    /**
     *
     * @param categoryName - name of category
     */
    boolean deleteCategoryByName(@Valid String categoryName);
}
