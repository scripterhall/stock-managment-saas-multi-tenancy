package com.nourallah.saasapp.services.impl;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.entities.Category;
import com.nourallah.saasapp.entities.Product;
import com.nourallah.saasapp.mappers.ProductMapper;
import com.nourallah.saasapp.repositories.CategoryRepository;
import com.nourallah.saasapp.repositories.ProductRepository;
import com.nourallah.saasapp.requests.ProductRequest;
import com.nourallah.saasapp.responses.ProductResponse;
import com.nourallah.saasapp.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;


    @Override
    public void create(ProductRequest request) {
        // 1- check if product already exists
        checkIfProductExists(request.getReference());

        // 2- check if category exists
        checkIfCategoryExistsById(request.getCategoryId());

        this.productRepository.save(productMapper.toEntity(request));
    }

    @Override
    public void update(String id, ProductRequest request) {
        // check if product exists
        final Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            log.debug("No product found with id {}", id);
            throw new RuntimeException("No product found with id " + id);
        }

        // 1- check if product already exists
        checkIfProductExists(request.getReference());

        // 2- check if category exists
        checkIfCategoryExistsById(request.getCategoryId());

        final Product productToUpdate = this.productMapper.toEntity(request);
        productToUpdate.setId(id);
        this.productRepository.save(productToUpdate);

    }

    @Override
    public PageResponse<ProductResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Product> products = productRepository.findAll(pageRequest);
        final Page<ProductResponse> productResponses = products.map(productMapper::toResponse);
        return PageResponse.of(productResponses) ;
    }

    @Override
    public ProductResponse findById(String id) {
        return   this.productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
    }

    @Override
    public void delete(String id) {
        final Product product = this.productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
        this.productRepository.delete(product);
    }

    private void checkIfProductExists(final String reference) {
        final Optional<Product> productOptional = this.productRepository.findByReferenceIgnoreCase(reference);
        if (productOptional.isPresent()) {
            log.debug("Product with reference {} already exists", reference);
            throw new RuntimeException("Product with reference " + reference + " already exists");
        }
    }

    private void checkIfCategoryExistsById(final String categoryId) {
        final Optional<Category> categoryOptional = this.categoryRepository.findById(categoryId);
        if(categoryOptional.isEmpty()) {
            log.debug("Category with id {} does not exist", categoryId);
            throw new RuntimeException("Category with id " + categoryId + " does not exist");
        }
    }
}
