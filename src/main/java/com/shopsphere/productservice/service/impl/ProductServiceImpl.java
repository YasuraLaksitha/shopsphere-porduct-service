package com.shopsphere.productservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.dto.ProductDTO;
import com.shopsphere.productservice.entity.CategoryEntity;
import com.shopsphere.productservice.entity.ProductEntity;
import com.shopsphere.productservice.exceptions.ResourceNotFoundException;
import com.shopsphere.productservice.repository.read.CategoryRepository;
import com.shopsphere.productservice.repository.read.ProductReadRepository;
import com.shopsphere.productservice.repository.write.ProductWriteRepository;
import com.shopsphere.productservice.service.IProductService;
import com.shopsphere.productservice.utils.ApplicationDefaultConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductReadRepository productReadRepository;

    private final ProductWriteRepository productWriteRepository;

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    @Value("${images.products.url}")
    private String productImageUrl;

    @Override
    @Cacheable(value = "products", key = "#productName")
    public ProductDTO retrieveProductByName(final String productName) {
        final ProductEntity productEntity =
                productReadRepository.findByProductNameStartsWithIgnoreCase(productName).orElseThrow(
                        () -> new ResourceNotFoundException("Product", "product name", productName)
                );

        final ProductDTO productDTO = objectMapper.convertValue(productEntity, ProductDTO.class);
        productDTO.setProductDiscountPrice(calculateProductDiscountPrice(
                productEntity.getProductSpecialPrice(),
                productEntity.getProductPrice()
        ));

        return productDTO;
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

        final Page<ProductEntity> productEntityPage = productReadRepository.findAll(spec, pageRequest);

        final List<ProductDTO> productDTOList = productEntityPage.getContent()
                .stream().map(productEntity -> {
                    final ProductDTO productDTO = objectMapper.convertValue(productEntity, ProductDTO.class);
                    productDTO.setProductImage(createImageUrl(productEntity.getProductImage()));
                    productDTO.setProductDiscountPrice(calculateProductDiscountPrice(
                            productEntity.getProductPrice(),
                            productEntity.getProductSpecialPrice()
                    ));
                    return productDTO;
                }).toList();

        return PaginationResponseDTO.<List<ProductDTO>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortOrder(sortOrder)
                .sortBy(sortBy)
                .data(productDTOList)
                .isLastPage(productEntityPage.isLast())
                .build();
    }

    @Override
    public boolean isProductQuantityAvailable(final String productName, final Integer quantity) {
        final ProductEntity productEntity =
                productReadRepository.findByProductNameStartsWithIgnoreCase(productName).orElseThrow(
                        () -> new ResourceNotFoundException("Product", "product name", productName)
                );
        return productEntity.getProductQuantity() - quantity >= ApplicationDefaultConstants.MINIMUM_PRODUCT_THRESHOLD_COUNT;
    }

    @Override
    @CachePut(value = "products", key = "#productName")
    public void updateProductQuantities(Map<String, Integer> productQuantityMap) {
        productQuantityMap.forEach((productName, quantity) ->
                productWriteRepository.findByProductNameStartsWithIgnoreCase(productName).ifPresent(productEntity -> {

                    productEntity.setProductQuantity(productEntity.getProductQuantity() - quantity);
                    if (productEntity.getMinimumThreshHoldCount() >= productEntity.getProductQuantity())
                        productEntity.setUnavailable(true);

                    productWriteRepository.save(productEntity);
                }));
    }

    private double calculateProductDiscountPrice(final Double productSpecialPrice, final Double productPrice) {
        Objects.requireNonNull(productSpecialPrice);
        Objects.requireNonNull(productPrice);

        return productPrice - productSpecialPrice;
    }

    private String createImageUrl(final String string) {
        return productImageUrl.endsWith("/") ? productImageUrl + string : productImageUrl + "/" + string;
    }
}
