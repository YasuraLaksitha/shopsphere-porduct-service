package com.shopsphere.productservice.controller;

import com.shopsphere.productservice.dto.CategoryDTO;
import com.shopsphere.productservice.dto.ErrorResponseDTO;
import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.dto.ResponseDTO;
import com.shopsphere.productservice.service.ICategoryService;
import com.shopsphere.productservice.utils.ApplicationDefaultConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(
        name = "Category controller",
        description = "REST APIs to perform CRUD operations on category"
)

@RestController
@RequestMapping(value = "/api/category", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(
            summary = "Create category",
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
    @PostMapping("/admin/save")
    public ResponseEntity<ResponseDTO> post(@Valid @RequestBody CategoryDTO category) {
        categoryService.persistCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.CREATED)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_201)
                        .build());
    }

    @Operation(
            summary = "Update category",
            description = "REST API to update category details"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK",
            content = @Content(
                    schema = @Schema(implementation = ResponseDTO.class)
            )
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
    public ResponseEntity<ResponseDTO> updateCategoryDetails(@Valid @RequestBody CategoryDTO category) {
        categoryService.updateCategoryByName(category);
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                .build());
    }

    @Operation(
            summary = "disable category",
            description = "REST API to disable enabled category details"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK",
            content = @Content(
                    schema = @Schema(implementation = ResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "403",
            description = "HTTP Status Code FORBIDDEN",
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
    @PutMapping("/admin/{categoryName}/disable")
    public ResponseEntity<ResponseDTO> disableCategory(
            @Pattern(regexp = "[a-zA-Z]+", message = "Invalid category name")
            @PathVariable final String categoryName
    ) {
        return categoryService.disableCategoryByName(categoryName) ?
                ResponseEntity.ok().body(ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                        .build()) :

                ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ResponseDTO.builder()
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_417)
                        .build());
    }

    @Operation(
            summary = "enable category",
            description = "REST API to enable disabled category"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK",
            content = @Content(
                    schema = @Schema(implementation = ResponseDTO.class)
            )
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
    @PutMapping("/admin/{categoryName}/enable")
    public ResponseEntity<ResponseDTO> enableCategory(
            @Pattern(regexp = "[a-zA-Z]+", message = "Invalid category name")
            @PathVariable final String categoryName
    ) {
        return categoryService.enableCategoryByName(categoryName) ?
                ResponseEntity.ok().body(ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                        .build()) :

                ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ResponseDTO.builder()
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .timestamp(LocalDateTime.now())
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_417)
                        .build());
    }

    @Operation(
            summary = "fetch category ",
            description = "REST API to fetch category by its name"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK",
            content = @Content(
                    schema = @Schema(implementation = ResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "403",
            description = "HTTP Status Code FORBIDDEN",
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
    @GetMapping("/public/get/{name}")
    public ResponseEntity<CategoryDTO> getByName(
            @Pattern(regexp = "[a-zA-Z]+", message = "Invalid category name")
            @PathVariable final String name
    ) {
        final CategoryDTO category = categoryService.retrieveCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Filter categories",
            description = "REST API to filter categories"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
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
