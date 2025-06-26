package com.shopsphere.productservice.controller;

import com.shopsphere.productservice.dto.ErrorResponseDTO;
import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.dto.ProductDTO;
import com.shopsphere.productservice.dto.ResponseDTO;
import com.shopsphere.productservice.service.IProductService;
import com.shopsphere.productservice.utils.ApplicationDefaultConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "Product Controller",
        description = "REST APIs to perform CRUD operations on products"
)

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class ProductController {

    private final IProductService productService;

    @Operation(
            summary = "Create Product",
            description = "REST API to create a new category"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status Code CREATED"
    )
    @ApiResponse(
            responseCode = "409",
            description = "HTTP Status Code CONFLICT",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
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

    @Operation(
            summary = "Update product",
            description = "REST API to update category details"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @ApiResponse(
            responseCode = "404",
            description = "HTTP Status Code NOT_FOUND",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Code BAD_REQUEST",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
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

    @Operation(
            summary = "Update product image",
            description = "REST API to update product image"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @ApiResponse(
            responseCode = "404",
            description = "HTTP Status Code NOT_FOUND",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Code BAD_REQUEST",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PutMapping(path = "/admin/{productName}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateImage(
            @NotEmpty(message = "Product name is required") @PathVariable String productName,
            @RequestPart("file") MultipartFile image) throws Exception {

        productService.updateProductImage(image, productName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                        .build());
    }

    @Operation(
            summary = "enable product",
            description = "REST API to enable disabled product"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @ApiResponse(
            responseCode = "409",
            description = "HTTP Status Code CONFLICT",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Code BAD_REQUEST",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "417",
            description = "HTTP Status Code EXCEPTION_FAILED",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PutMapping("/admin/{productName}/enable")
    public ResponseEntity<ResponseDTO> enableProduct(@PathVariable final String productName) {
        return productService.enableProductByName(productName) ?
                ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseDTO.builder()
                                .status(HttpStatus.OK)
                                .timestamp(LocalDateTime.now())
                                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                                .build()) :

                ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body(ResponseDTO.builder()
                                .status(HttpStatus.EXPECTATION_FAILED)
                                .timestamp(LocalDateTime.now())
                                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_417)
                                .build());
    }

    @Operation(
            summary = "disable product",
            description = "REST API to disable enabled product"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @ApiResponse(
            responseCode = "410",
            description = "HTTP Status Code GONE",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Code BAD_REQUEST",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PutMapping("/admin/{productName}/disable")
    public ResponseEntity<ResponseDTO> disableProduct(
            @NotEmpty(message = "Product name is required") @PathVariable final String productName) {
        return productService.disableProductByName(productName) ?
                ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseDTO.builder()
                                .status(HttpStatus.OK)
                                .timestamp(LocalDateTime.now())
                                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                                .build()) :

                ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body(ResponseDTO.builder()
                                .status(HttpStatus.EXPECTATION_FAILED)
                                .timestamp(LocalDateTime.now())
                                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_417)
                                .build());
    }

    @Operation(
            summary = "fetch product",
            description = "REST API to fetch product by its name"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK",
            content = @Content(
                    schema = @Schema(implementation = ResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "410",
            description = "HTTP Status Code GONE",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "HTTP Status Code BAD_REQUEST",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "HTTP Status Code NOT_FOUND",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/public/get")
    public ResponseEntity<ProductDTO> getByName(
            @NotEmpty(message = "product name is required") @RequestParam final String productName) {
        final ProductDTO productDTO = productService.retrieveProductByName(productName);
        return ResponseEntity.ok(productDTO);
    }

    @Operation(
            summary = "Filter products",
            description = "REST API to filter products"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @ApiResponse(
            responseCode = "404",
            description = "HTTP Status Code NOT_FOUND",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "410",
            description = "HTTP Status Code GONE",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
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

    @Operation(
            summary = "Check product availability",
            description = "REST API to check if the product available"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @ApiResponse(
            responseCode = "404",
            description = "HTTP Status Code NOT_FOUND",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/public/check/{productName}")
    public ResponseEntity<Boolean> checkProductAvailability(
            @NotEmpty(message = "Product name is required") @PathVariable final String productName,
            @PositiveOrZero(message = "Quantity should be positive") @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.isProductQuantityAvailable(productName, quantity));
    }
}
