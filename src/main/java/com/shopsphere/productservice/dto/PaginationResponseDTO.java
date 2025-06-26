package com.shopsphere.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(
        name = "Pagination Response",
        description = "Schema to hold pagination response information"
)
public class PaginationResponseDTO<T> {

    @Schema(description = "Sort field name", example = "productName")
    private String sortBy;

    @Schema(description = "Sort order", example = "ASC")
    private String sortOrder;

    @Schema(description = "page number", example = "2")
    private int pageNumber;

    @Schema(description = "page size", example = "20")
    private int pageSize;

    @Schema(description = "determine if it is the last page", example = "true")
    private boolean isLastPage;

    @Schema(description = "requested data")
    private T data;
}
