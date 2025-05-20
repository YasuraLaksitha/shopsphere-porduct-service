package com.shopsphere.porductservice.controller;

import com.shopsphere.porductservice.dto.CategoryDTO;
import com.shopsphere.porductservice.dto.ResponseDTO;
import com.shopsphere.porductservice.service.ICategoryService;
import com.shopsphere.porductservice.utils.ApplicationContants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api/category", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping("/admin/save")
    public ResponseEntity<ResponseDTO> post(@Valid @RequestBody CategoryDTO category) {
        categoryService.persistCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(ApplicationContants.RESPONSE_MESSAGE_200)
                .build());
    }
}
