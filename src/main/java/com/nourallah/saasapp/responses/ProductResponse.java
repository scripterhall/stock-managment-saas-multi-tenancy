package com.nourallah.saasapp.responses;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private String name;
    private String description;
    private String reference;
    private Integer alertThreshold;
    private BigDecimal price;
    private String categoryName; // simple attribute
    private int availableQuantity;
}
