package com.nourallah.saasapp.responses;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private String name;
    private String description;
    private int nbProducts;
}
