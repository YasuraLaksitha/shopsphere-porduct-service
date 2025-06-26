package com.shopsphere.productservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.dto.ProductDTO;
import com.shopsphere.productservice.entity.CategoryEntity;
import com.shopsphere.productservice.entity.ProductEntity;
import com.shopsphere.productservice.exceptions.NoModificationRequiredException;
import com.shopsphere.productservice.exceptions.ResourceAlreadyExistException;
import com.shopsphere.productservice.exceptions.ResourceAlreadyUnavailableException;
import com.shopsphere.productservice.exceptions.ResourceNotFoundException;
import com.shopsphere.productservice.repository.read.CategoryReadRepository;
import com.shopsphere.productservice.repository.read.ProductReadRepository;
import com.shopsphere.productservice.repository.write.ProductWriteRepository;
import com.shopsphere.productservice.service.IFileService;
import com.shopsphere.productservice.service.IProductService;
import com.shopsphere.productservice.utils.ApplicationDefaultConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductReadRepository productReadRepository;

    private final ProductWriteRepository productWriteRepository;

    private final CategoryReadRepository categoryReadRepository;

    private final CacheManager cacheManager;

    private final ObjectMapper objectMapper;

    private final IFileService fileService;

    @Value("${images.products.url}")
    private String productImageUrl;

    @Override
    public void persistProduct(final ProductDTO productDTO, final String category) {
        final CategoryEntity categoryEntity = categoryReadRepository.findByCategoryNameIgnoreCase(category).orElseThrow(
                () -> new ResourceNotFoundException("Category", "category name", category)
        );

        productWriteRepository.findByProductNameStartsWithIgnoreCase(productDTO.getProductName()).ifPresent((entity) -> {
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

        productWriteRepository.save(productEntity);
    }

    @Override
    public void updateProduct(final ProductDTO productDTO) {
        final ProductEntity productEntity =
                productWriteRepository.findByProductNameStartsWithIgnoreCase(productDTO.getProductName()).orElseThrow(
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

        productWriteRepository.save(productEntity);
        updateCache(productEntity.getProductName(), productEntity);
    }

    @Override
    public void updateProductImage(final MultipartFile image, final String productName) throws Exception {
        final ProductEntity productEntity =
                productWriteRepository.findByProductNameStartsWithIgnoreCase(productName).orElseThrow(
                        () -> new ResourceNotFoundException("Product", "product name", productName)
                );

        final String uploadImage = fileService.uploadImage(image, productImageUrl);
        productEntity.setProductImage(createImageUrl(uploadImage));
        productWriteRepository.save(productEntity);

        updateCache(productName, productEntity);
    }

    private void updateCache(String productName, ProductEntity productEntity) {
        final Cache cache = cacheManager.getCache("product");
        if (cache != null) {
            final ProductDTO dto = objectMapper.convertValue(productEntity, ProductDTO.class);
            cache.put(productName, dto);
        }
    }

    @CacheEvict(value = "product", key = "#productName")
    @Override
    public boolean disableProductByName(final String productName) {
        final ProductEntity productEntity =
                productWriteRepository.findByProductNameStartsWithIgnoreCase(productName).orElseThrow(
                        () -> new ResourceNotFoundException("Product", "product name", productName)
                );
        if (productEntity.isUnavailable())
            throw new ResourceAlreadyUnavailableException("Product", "product name", productName);
        productEntity.setUnavailable(true);
        productWriteRepository.save(productEntity);

        return productEntity.isUnavailable();
    }

    @Override
    public boolean enableProductByName(String productName) {
        final ProductEntity productEntity =
                productWriteRepository.findByProductNameStartsWithIgnoreCase(productName).orElseThrow(
                        () -> new ResourceNotFoundException("Product", "product name", productName)
                );
        if (!productEntity.isUnavailable())
            throw new ResourceAlreadyExistException("Product", "product name", productName);
        productEntity.setUnavailable(false);
        productWriteRepository.save(productEntity);

        return !productEntity.isUnavailable();
    }

    private double calculateProductSpecialPrice(final Double productDiscountPrice, final Double productPrice) {
        Objects.requireNonNull(productDiscountPrice);
        Objects.requireNonNull(productPrice);

        return productPrice - productDiscountPrice;
    }

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

        final CategoryEntity categoryEntity = categoryReadRepository.findByCategoryNameIgnoreCase(category).orElseThrow(
                () -> new ResourceNotFoundException("Category", "category name", category)
        );
        if (categoryEntity.isUnavailable())
            throw new ResourceAlreadyUnavailableException("Category", "category name", category);

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
    public void updateProductQuantities(Map<String, Integer> productQuantityMap) {
        productQuantityMap.forEach((productName, quantity) ->
                productWriteRepository.findByProductNameStartsWithIgnoreCase(productName).ifPresent(productEntity -> {

                    productEntity.setProductQuantity(productEntity.getProductQuantity() - quantity);

                    if (productEntity.getMinimumThreshHoldCount() >= productEntity.getProductQuantity())
                        productEntity.setUnavailable(true);

                    productWriteRepository.save(productEntity);

                    final Cache cache = cacheManager.getCache("product");
                    if (cache != null) {
                        if (productEntity.isUnavailable())
                            cache.evict(productEntity.getProductName());
                        else {
                            final ProductDTO productDTO = objectMapper.convertValue(productEntity, ProductDTO.class);
                            productDTO.setProductImage(createImageUrl(productEntity.getProductImage()));
                            cache.put(productName, productDTO);
                        }
                    }
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
