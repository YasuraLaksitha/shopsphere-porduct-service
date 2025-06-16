package com.shopsphere.productservice.service;

import com.shopsphere.productservice.dto.CategoryDTO;
import com.shopsphere.productservice.dto.PaginationResponseDTO;

import java.util.List;

public interface ICategoryService {

    void persistCategory(CategoryDTO category);

    void updateCategoryByName(CategoryDTO category);

    boolean disableCategoryByName(String categoryName);

    /**
     *
     * @param categoryName - category name
     * @return - true if enabled
     */
    boolean enableCategoryByName(String categoryName);

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

}
