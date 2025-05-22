package com.shopsphere.porductservice.controller;

import com.shopsphere.porductservice.dto.CategoryDTO;
import com.shopsphere.porductservice.dto.PaginationResponseDTO;
import com.shopsphere.porductservice.dto.ResponseDTO;
import com.shopsphere.porductservice.service.ICategoryService;
import com.shopsphere.porductservice.utils.ApplicationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/category", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping("/admin/save")
    public ResponseEntity<ResponseDTO> post(@Valid @RequestBody CategoryDTO category) {
        categoryService.persistCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.CREATED)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationConstants.RESPONSE_MESSAGE_201)
                        .build());
    }

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
            @RequestParam(required = false, defaultValue = ApplicationConstants.PAGE_NUMBER) int pageNumber,
            @RequestParam(required = false, defaultValue = ApplicationConstants.CATEGORY_PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = ApplicationConstants.CATEGORY_SORT_ORDER) String sortOrder,
            @RequestParam(required = false, defaultValue = ApplicationConstants.CATEGORY_ORDER_BY) String orderBy
    ) {
        return ResponseEntity.ofNullable
                (categoryService.retrieveAllCategories(orderBy, sortOrder, pageNumber, pageSize, keyword));
    }

    @PutMapping("/admin/update")
    public ResponseEntity<ResponseDTO> update(@Valid @RequestBody CategoryDTO category) {
        categoryService.updateCategoryByName(category);
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(ApplicationConstants.RESPONSE_MESSAGE_200)
                .build());
    }

    @DeleteMapping("/admin/delete/{name}")
    public ResponseEntity<ResponseDTO> delete(
            @Pattern(regexp = "[a-zA-Z]+", message = "Invalid category name")
            @PathVariable final String name
    ) {
        return categoryService.deleteCategoryByName(name) ?
                ResponseEntity.ok().body(ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationConstants.RESPONSE_MESSAGE_200)
                        .build()) :

                ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ResponseDTO.builder()
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationConstants.RESPONSE_MESSAGE_417)
                        .build());
    }
}
