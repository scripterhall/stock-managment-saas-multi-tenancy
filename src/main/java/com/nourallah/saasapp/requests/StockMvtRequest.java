package com.nourallah.saasapp.requests;


import com.nourallah.saasapp.entities.TypeMvt;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMvtRequest {

    private TypeMvt typeMvt;

    private Integer quantity;

    private LocalDateTime dateMvt;

    private String comment;

    private String productId;

}
