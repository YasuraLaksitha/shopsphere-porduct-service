package com.shopsphere.productservice.functions;

import com.shopsphere.productservice.service.IProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class ProductFunctions {

    @Bean
    public Consumer<Map<String, Integer>> productQuantityUpdate(IProductService productService) {
        return productService::updateProductQuantites;
    }
}
