package com.shopsphere.porductservice.service;

import com.shopsphere.porductservice.dto.ProductDTO;

public interface IProductService {

    /**
     *
     * @param productDTO - productDTO object
     */
    void persistProduct(final ProductDTO productDTO,final String category);

    /**
     *
     * @param productName - name of product
     * @return productDTO object
     */
    ProductDTO retrieveProductByName(final String productName);
}
