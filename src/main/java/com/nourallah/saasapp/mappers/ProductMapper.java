package com.nourallah.saasapp.mappers;

import com.nourallah.saasapp.entities.Category;
import com.nourallah.saasapp.entities.Product;
import com.nourallah.saasapp.requests.ProductRequest;
import com.nourallah.saasapp.responses.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request){
        return Product.builder()
                .name(request.getName())
                .reference(request.getReference())
                .description(request.getDescription())
                .alertThreshold(request.getAlertThreshold())
                .price(request.getPrice())
                .category(Category.builder().id(request.getCategoryId()).build())
                .build();
    }

    public ProductResponse toResponse(Product entity){
        return ProductResponse.builder()
                .name(entity.getName())
                .reference(entity.getReference())
                .description(entity.getDescription())
                .alertThreshold(entity.getAlertThreshold())
                .price(entity.getPrice())
                .categoryName(entity.getCategory().getName())
                //.availableQuantity() TODO
                .build();
    }
}
