package com.nourallah.saasapp.controllers;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.requests.CategoryRequest;
import com.nourallah.saasapp.responses.CategoryResponse;
import com.nourallah.saasapp.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<Void> createCategory(
            @RequestBody
            @Valid
            final CategoryRequest request
    ) {
        this.service.create(request);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{category-id}")
    public ResponseEntity<Void> updateCategory(
            @RequestBody
            @Valid
            final CategoryRequest request,
            @PathVariable("category-id")
            @NotNull(message = "category id cannot be null")
            final String categoryId
    ){
        this.service.update(categoryId, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> findCategoryById(
            @PathVariable("category-id")
            @NotNull(message = "category id cannot be null")
            final String categoryId
    ){
        return ResponseEntity.ok(this.service.findById(categoryId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> findAllCategories(
            @RequestParam(name = "page" , defaultValue = "0")
            final int page,
            @RequestParam(name = "size" , defaultValue = "10")
            final int size
    ){
        return  ResponseEntity.ok(this.service.findAll(page, size));
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("category-id")
            @NotNull(message = "category id cannot be null")
            final String categoryId
    ){
        this.service.delete(categoryId);
        return ResponseEntity.noContent().build();
    }




}
