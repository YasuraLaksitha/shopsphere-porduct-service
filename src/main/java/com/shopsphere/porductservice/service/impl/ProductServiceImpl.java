package com.shopsphere.porductservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;

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
            productEntity.setMinimumThreshHoldCount(ApplicationDefaultConstants.MINIMUM_PRODUCT_THRESHHOLD_COUNT);

        if (productEntity.getProductQuantity() <= ApplicationDefaultConstants.MINIMUM_PRODUCT_THRESHHOLD_COUNT)
            productEntity.setUnavailable(true);

        productRepository.save(productEntity);
    }
}
