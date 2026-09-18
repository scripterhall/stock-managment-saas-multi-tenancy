package com.nourallah.saasapp.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name should not be empty")
    @Size(min = 3 , max = 50 , message = "Product  name should be between 3 and 50 characters")
    private String name;
    private String description;
    @NotBlank(message = "product reference should not be empty")
    @Size(min = 3 , max = 255 , message = "product reference should be between 3 and 50 characters")
    private String reference;
    @Positive(message = "alert threshold should be a positive number")
    private Integer alertThreshold;

    @Positive(message = "price should be a positive number")
    private BigDecimal price;

    @NotBlank(message = "Category id should not be empty")
    private String categoryId;
}
