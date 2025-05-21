package com.shopsphere.porductservice.service;

import com.shopsphere.porductservice.dto.CategoryDTO;

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
}
