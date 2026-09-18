package com.nourallah.saasapp.requests;


import com.nourallah.saasapp.entities.TypeMvt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMvtRequest {

    @NotBlank(message = "Type of movement should not be empty")
    private TypeMvt typeMvt;

    @Positive(message = "quantity should be positive number")
    private Integer quantity;

    @NotNull(message = "Date of movement should not be null")
    @PastOrPresent(message = "Date of movement should be on past or present")
    private LocalDateTime dateMvt;

    private String comment;

    @NotBlank(message = "product id should not be empty")
    private String productId;

}
