package com.nourallah.saasapp.controllers;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.requests.ProductRequest;
import com.nourallah.saasapp.responses.ProductResponse;
import com.nourallah.saasapp.services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<Void> createProduct(
            @RequestBody
            @Valid
            final ProductRequest request
    ) {
        this.service.create(request);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{product-id}")
    public ResponseEntity<Void> updateProduct(
            @RequestBody
            @Valid
            final ProductRequest request,
            @PathVariable("product-id")
            @NotNull(message = "Product id cannot be null")
            final String productId
    ){
        this.service.update(productId, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findProductById(
            @PathVariable("product-id")
            @NotNull(message = "Product id cannot be null")
            final String productId
    ){
        return ResponseEntity.ok(this.service.findById(productId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAllProducts(
            @RequestParam(name = "page" , defaultValue = "0")
            final int page,
            @RequestParam(name = "size" , defaultValue = "10")
            final int size
    ){
        return  ResponseEntity.ok(this.service.findAll(page, size));
    }

    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("product-id")
            @NotNull(message = "Product id cannot be null")
            final String productId
    ){
        this.service.delete(productId);
        return ResponseEntity.noContent().build();
    }




}
