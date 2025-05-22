package com.shopsphere.porductservice.service;

import com.shopsphere.porductservice.dto.ProductDTO;

public interface IProductService {

    /**
     *
     * @param productDTO - productDTO object
     */
    void persistProduct(final ProductDTO productDTO,final String category);
}
