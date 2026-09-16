package com.nourallah.saasapp.requests;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    private String name;
    private String description;
    private String reference;
    private Integer alertThreshold;
    private BigDecimal price;
    private String categoryId;
}
