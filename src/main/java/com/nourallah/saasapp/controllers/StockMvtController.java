package com.nourallah.saasapp.controllers;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.requests.StockMvtRequest;
import com.nourallah.saasapp.responses.StockMvtResponse;
import com.nourallah.saasapp.services.StockMvtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/stocks")
public class StockMvtController {

    private final StockMvtService service;

    @PostMapping
    public ResponseEntity<Void> createStockMvt(
            @RequestBody
            @Valid
            final StockMvtRequest request
    ) {
        this.service.create(request);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{stock-mvt-id}")
    public ResponseEntity<Void> updateStockMvt(
            @RequestBody
            @Valid
            final StockMvtRequest request,
            @PathVariable("stock-mvt-id")
            @NotNull(message = "StockMvt id cannot be null")
            final String stockMvtId
    ){
        this.service.update(stockMvtId, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{stock-mvt-id}")
    public ResponseEntity<StockMvtResponse> findStockMvtById(
            @PathVariable("stock-mvt-id")
            @NotNull(message = "stock mvt id cannot be null")
            final String stockMvtId
    ){
        return ResponseEntity.ok(this.service.findById(stockMvtId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<StockMvtResponse>> findAllStockMvt(
            @RequestParam(name = "page" , defaultValue = "0")
            final int page,
            @RequestParam(name = "size" , defaultValue = "10")
            final int size
    ){
        return  ResponseEntity.ok(this.service.findAll(page, size));
    }

    @DeleteMapping("/{stock-mvt-id}")
    public ResponseEntity<Void> deleteStockMvt(
            @PathVariable("stock-mvt-id")
            @NotNull(message = "stock mvt id cannot be null")
            final String stockMvtId
    ){
        this.service.delete(stockMvtId);
        return ResponseEntity.noContent().build();
    }

}
