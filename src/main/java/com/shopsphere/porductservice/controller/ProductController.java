package com.shopsphere.porductservice.controller;

import com.shopsphere.porductservice.dto.PaginationResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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
        productService.persistProduct(product, category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.CREATED)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_201)
                        .build());
    }

    @GetMapping("/public/get")
    public ResponseEntity<ProductDTO> getByName(
            @NotEmpty(message = "product name is required") @RequestParam final String productName) {
        final ProductDTO productDTO = productService.retrieveProductByName(productName);
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/public/products")
    public ResponseEntity<PaginationResponseDTO<List<ProductDTO>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = ApplicationDefaultConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = ApplicationDefaultConstants.PRODUCT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = ApplicationDefaultConstants.PRODUCT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = ApplicationDefaultConstants.PRODUCT_SORT_ORDER, required = false) String sortOrder
    ) {
        final PaginationResponseDTO<List<ProductDTO>> retrieved = productService.retrieveAllProduct(
                category,
                pageNumber, pageSize, sortBy, sortOrder, keyword
        );
        return ResponseEntity.ok(retrieved);
    }

    @PutMapping("/admin/update")
    public ResponseEntity<ResponseDTO> updateProductDetails(@Valid @RequestBody ProductDTO productDTO) {
        productService.updateProduct(productDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                        .build());
    }

    @PutMapping("/admin/{productName}/image")
    public ResponseEntity<ResponseDTO> updateImage(@PathVariable String productName,
                                                   @RequestParam MultipartFile image) throws Exception {

        productService.updateProductImage(image, productName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                        .build());
    }
}
