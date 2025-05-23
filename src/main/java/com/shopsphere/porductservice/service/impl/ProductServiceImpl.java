package com.shopsphere.porductservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.porductservice.dto.PaginationResponseDTO;
import com.shopsphere.porductservice.dto.ProductDTO;
import com.shopsphere.porductservice.entity.CategoryEntity;
import com.shopsphere.porductservice.entity.ProductEntity;
import com.shopsphere.porductservice.exceptions.NoModificationRequiredException;
import com.shopsphere.porductservice.exceptions.ResourceAlreadyExistException;
import com.shopsphere.porductservice.exceptions.ResourceNotFoundException;
import com.shopsphere.porductservice.repository.CategoryRepository;
import com.shopsphere.porductservice.repository.ProductRepository;
import com.shopsphere.porductservice.service.IFileService;
import com.shopsphere.porductservice.service.IProductService;
import com.shopsphere.porductservice.utils.ApplicationDefaultConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {


    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    private final IFileService fileService;

    @Value("${images.products.url}")
    private String productImageUrl;

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

        final Page<ProductEntity> productEntityPage = productRepository.findAll(spec, pageRequest);

        final List<ProductDTO> productDTOList = productEntityPage.getContent()
                .stream().map((productEntity) -> {
                    final ProductDTO productDTO = objectMapper.convertValue(productEntity, ProductDTO.class);
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
    public void updateProduct(final ProductDTO productDTO) {
        final ProductEntity productEntity =
                productRepository.findByProductNameEndingWithIgnoreCase(productDTO.getProductName()).orElseThrow(
                        () -> new ResourceNotFoundException("Product", "product name", productDTO.getProductName())
                );

        if (Objects.equals(productEntity.getProductSpecialPrice(), productDTO.getProductSpecialPrice()) &&
                Objects.equals(productEntity.getProductPrice(), productDTO.getProductPrice()) &&
                Objects.equals(productEntity.getProductDescription(), productDTO.getProductDescription()) &&
                Objects.equals(productEntity.getProductQuantity(), productDTO.getProductQuantity()) &&
                Objects.isNull(productDTO.getProductDiscountPrice()))
            throw new NoModificationRequiredException("Product", "product name", productDTO.getProductName());

        productEntity.setProductPrice(productDTO.getProductPrice());
        productEntity.setProductSpecialPrice(productDTO.getProductSpecialPrice());
        productEntity.setProductQuantity(productDTO.getProductQuantity());
        productEntity.setProductDescription(productDTO.getProductDescription());

        productEntity.setProductSpecialPrice(calculateProductSpecialPrice(
                productDTO.getProductDiscountPrice(),
                productDTO.getProductPrice()
        ));
        if (productEntity.getProductQuantity() <= ApplicationDefaultConstants.MINIMUM_PRODUCT_THRESHOLD_COUNT)
            productEntity.setUnavailable(true);

        productRepository.save(productEntity);
    }

    @Override
    public void updateProductImage(final MultipartFile image, final String productName) throws Exception {
        final ProductEntity productEntity =
                productRepository.findByProductNameEndingWithIgnoreCase(productName).orElseThrow(
                () -> new ResourceNotFoundException("Product", "product name", productName)
        );


        final String uploadImage = fileService.uploadImage(image, productImageUrl);
        productEntity.setProductImage(uploadImage);
        productRepository.save(productEntity);
    }

    private double calculateProductSpecialPrice(final Double productDiscountPrice, final Double productPrice) {
        Objects.requireNonNull(productDiscountPrice);
        Objects.requireNonNull(productPrice);

        return productPrice - productDiscountPrice;
    }

    private double calculateProductDiscountPrice(final Double productSpecialPrice, final Double productPrice) {
        Objects.requireNonNull(productSpecialPrice);
        Objects.requireNonNull(productPrice);

        return productPrice - productSpecialPrice;
    }
}
