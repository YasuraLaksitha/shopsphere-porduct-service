package com.shopsphere.productservice.controller;

import com.shopsphere.productservice.dto.CategoryDTO;
import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.service.ICategoryService;
import com.shopsphere.productservice.utils.ApplicationDefaultConstants;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/category", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("/public/get/{name}")
    public ResponseEntity<CategoryDTO> getByName(
            @Pattern(regexp = "[a-zA-Z]+", message = "Invalid category name")
            @PathVariable final String name
    ) {
        final CategoryDTO category = categoryService.retrieveCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<PaginationResponseDTO<List<CategoryDTO>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.PAGE_NUMBER) int pageNumber,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.CATEGORY_PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.CATEGORY_SORT_ORDER) String sortOrder,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.CATEGORY_SORT_BY) String orderBy
    ) {
        return ResponseEntity.ofNullable
                (categoryService.retrieveAllCategories(orderBy, sortOrder, pageNumber, pageSize, keyword));
    }
}
