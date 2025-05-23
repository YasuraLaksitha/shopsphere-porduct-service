package com.shopsphere.porductservice.service;

import com.shopsphere.porductservice.dto.PaginationResponseDTO;
import com.shopsphere.porductservice.dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {

    /**
     *
     * @param productDTO - productDTO object
     */
    void persistProduct(final ProductDTO productDTO, final String category);

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
     * @param productDTO - productDTO object
     */
    void updateProduct(final ProductDTO productDTO);

    /**
     *
     * @param image - image
     * @param productName - name of the product
     */
    void updateProductImage(final MultipartFile image, final String productName) throws Exception;

    /**
     *
     * @param productName - name of the product
     * @return - True if product was deleted successfully
     */
    boolean removeProductByName(final String productName);
}
