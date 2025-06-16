package com.shopsphere.productservice.service;

import com.shopsphere.productservice.dto.PaginationResponseDTO;
import com.shopsphere.productservice.dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IProductService {

    /**
     *
     * @param productDTO - productDTO object
     * @param category - category name
     */
    void persistProduct(ProductDTO productDTO, String category);

    /**
     *
     * @param productDTO - productDTO object
     */
    void updateProduct(ProductDTO productDTO);

    /**
     *
     * @param image - product image
     * @param productName - product name
     * @throws Exception - exception
     */
    void updateProductImage(MultipartFile image, String productName) throws Exception;

    /**
     *
     * @param productName - product name
     * @return - true if removed
     */
    boolean removeProductByName(String productName);

    /**
     *
     * @param productName - product name
     * @return - true if enabled
     */
    boolean enableProductByName(String productName);

    /**
     *
     * @param productName - name of product
     * @return productDTO object
     */
    ProductDTO retrieveProductByName(final String productName);

    /**
     *
     * @param category   - category name
     * @param pageNumber - page number
     * @param pageSize   - page number
     * @param sortBy     - sort field
     * @param sortOrder  - sort order
     * @param keyword    - keyword
     * @return List of product DTOs
     */
    PaginationResponseDTO<List<ProductDTO>> retrieveAllProduct(final String category, final int pageNumber,
                                                               final int pageSize, final String sortBy,
                                                               final String sortOrder, final String keyword);
    /**
     *
     * @param productName - name of the product
     * @param quantity - quantity to be checked
     * @return - true if quantity is available
     */
    boolean isProductQuantityAvailable(String productName, Integer quantity);

    /**
     *
     * @param productQuantityMap - products to be updated
     */
    void updateProductQuantities(Map<String,Integer> productQuantityMap);
}
