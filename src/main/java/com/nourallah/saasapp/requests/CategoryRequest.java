package com.nourallah.saasapp.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @NotBlank(message = "Category name should not be empty")
    @Size(min = 3 , max = 50 , message = "Category name should be between 3 and 50 characters")
    private String name;

    private String description;


}
