package com.nourallah.saasapp.responses;


import com.nourallah.saasapp.entities.TypeMvt;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMvtResponse {

    private TypeMvt typeMvt;
    private Integer quantity;
    private LocalDateTime dateMvt;
    private String comment;
}
