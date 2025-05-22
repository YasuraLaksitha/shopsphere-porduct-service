package com.shopsphere.porductservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.porductservice.dto.PaginationResponseDTO;
import com.shopsphere.porductservice.dto.ProductDTO;
import com.shopsphere.porductservice.entity.CategoryEntity;
import com.shopsphere.porductservice.entity.ProductEntity;
import com.shopsphere.porductservice.exceptions.ResourceAlreadyExistException;
import com.shopsphere.porductservice.exceptions.ResourceNotFoundException;
import com.shopsphere.porductservice.repository.CategoryRepository;
import com.shopsphere.porductservice.repository.ProductRepository;
import com.shopsphere.porductservice.service.IProductService;
import com.shopsphere.porductservice.utils.ApplicationDefaultConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void persistProduct(final ProductDTO productDTO, final String category) {
        final CategoryEntity categoryEntity = categoryRepository.findByCategoryNameIgnoreCase(category).orElseThrow(
                () -> new ResourceNotFoundException("Category", "category name", category)
        );

        productRepository.findByProductNameEndingWithIgnoreCase(productDTO.getProductName()).ifPresent((entity) -> {
            throw new ResourceAlreadyExistException("Product", "product name", entity.getProductName());
        });

        final ProductEntity productEntity = objectMapper.convertValue(productDTO, ProductEntity.class);
        productEntity.setCategoryId(categoryEntity.getCategoryId());

        if (productEntity.getProductSpecialPrice() == null)
            productEntity.setProductSpecialPrice(ApplicationDefaultConstants.PRODUCT_SPECIAL_PRICE);
        if (productEntity.getProductQuantity() == null)
            productEntity.setProductQuantity(ApplicationDefaultConstants.PRODUCT_QUANTITY);
        if (productEntity.getMinimumThreshHoldCount() == null)
            productEntity.setMinimumThreshHoldCount(ApplicationDefaultConstants.MINIMUM_PRODUCT_THRESHOLD_COUNT);

        if (productEntity.getProductQuantity() <= ApplicationDefaultConstants.MINIMUM_PRODUCT_THRESHOLD_COUNT)
            productEntity.setUnavailable(true);

        productRepository.save(productEntity);
    }

    @Override
    public ProductDTO retrieveProductByName(final String productName) {
        final ProductEntity productEntity =
                productRepository.findByProductNameEndingWithIgnoreCase(productName).orElseThrow(
                        () -> new ResourceNotFoundException("Product", "product name", productName)
                );
        return objectMapper.convertValue(productEntity, ProductDTO.class);
    }

    @Override
    public PaginationResponseDTO<List<ProductDTO>> retrieveAllProduct(final String category, final int pageNumber,
                                                                      final int pageSize, final String sortBy,
                                                                      final String sortOrder, final String keyword) {

        final CategoryEntity categoryEntity = categoryRepository.findByCategoryNameIgnoreCase(category).orElseThrow(
                () -> new ResourceNotFoundException("Category", "category name", category)
        );
        Specification<ProductEntity> spec = Specification.where(null);

        if (StringUtils.hasText(keyword))
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), keyword.toLowerCase() + "%")
            );

        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("categoryId"), categoryEntity.getCategoryId())
        ).and((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isUnavailable"))
        );

        final Sort.Direction sortDirection =
                sortOrder.equalsIgnoreCase(ApplicationDefaultConstants.PRODUCT_SORT_ORDER) ?
                        Sort.Direction.ASC :
                        Sort.Direction.DESC;
        final Sort sortOrderBy = Sort.by(sortDirection, sortBy);
        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sortOrderBy);

        final Page<ProductEntity> productEntityPage = productRepository.findAll(spec, pageRequest);

        final List<ProductDTO> productDTOList = productEntityPage.getContent()
                .stream().map((productEntity) -> objectMapper.convertValue(productEntity, ProductDTO.class))
                .toList();

        return PaginationResponseDTO.<List<ProductDTO>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortOrder(sortOrder)
                .sortBy(sortBy)
                .data(productDTOList)
                .isLastPage(productEntityPage.isLast())
                .build();
    }
}
