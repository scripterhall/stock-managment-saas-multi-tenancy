package com.nourallah.saasapp.mappers;

import com.nourallah.saasapp.entities.Category;
import com.nourallah.saasapp.requests.CategoryRequest;
import com.nourallah.saasapp.responses.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(final CategoryRequest request){
        return Category.builder()
                       .name(request.getName())
                       .description(request.getDescription())
                       .build();
    }

    public CategoryResponse toResponse(final Category entity){
        int nbProducts = entity.getProducts() == null ? 0 : entity.getProducts().size();
        return CategoryResponse.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .nbProducts(nbProducts)
                .build();
    }
}
