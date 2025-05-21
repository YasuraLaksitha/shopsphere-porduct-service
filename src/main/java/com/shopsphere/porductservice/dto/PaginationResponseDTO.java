package com.shopsphere.porductservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationResponseDTO<T> {

    private String orderBy;

    private String sortOrder;

    private int pageNumber;

    private int pageSize;

    private boolean isLastPage;

    private T data;
}
