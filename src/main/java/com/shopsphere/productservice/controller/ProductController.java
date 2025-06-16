package com.shopsphere.productservice.controller;

import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.dto.ProductDTO;
import com.shopsphere.productservice.service.IProductService;
import com.shopsphere.productservice.utils.ApplicationDefaultConstants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class ProductController {

    private final IProductService productService;

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


    @GetMapping("/public/check/{productName}")
    public ResponseEntity<Boolean> checkProductAvailability(
            @NotEmpty(message = "Product name is required") @PathVariable final String productName,
            @PositiveOrZero(message = "Quantity should be positive") @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.isProductQuantityAvailable(productName, quantity));
    }
}
