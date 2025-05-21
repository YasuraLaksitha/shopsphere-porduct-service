package com.shopsphere.porductservice.controller;

import com.shopsphere.porductservice.dto.CategoryDTO;
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
}
