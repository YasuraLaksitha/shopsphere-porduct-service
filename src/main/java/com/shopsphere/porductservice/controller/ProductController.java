package com.shopsphere.porductservice.controller;

import com.shopsphere.porductservice.dto.ProductDTO;
import com.shopsphere.porductservice.dto.ResponseDTO;
import com.shopsphere.porductservice.service.IProductService;
import com.shopsphere.porductservice.utils.ApplicationDefaultConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/products", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class ProductController {

    private final IProductService productService;

    @PostMapping("/admin/categories/{category}/save")
    public ResponseEntity<ResponseDTO> save(
            @Valid @RequestBody ProductDTO product,
            @NotEmpty(message = "category name is required") @PathVariable final String category) {
        productService.persistProduct(product,category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.CREATED)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_201)
                        .build());
    }
}
