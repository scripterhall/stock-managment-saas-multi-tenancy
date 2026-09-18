package com.nourallah.saasapp.services.impl;


import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.entities.Product;
import com.nourallah.saasapp.entities.StockMvt;
import com.nourallah.saasapp.mappers.StockMvtMapper;
import com.nourallah.saasapp.repositories.ProductRepository;
import com.nourallah.saasapp.repositories.StockMvtRepository;
import com.nourallah.saasapp.requests.StockMvtRequest;
import com.nourallah.saasapp.responses.StockMvtResponse;
import com.nourallah.saasapp.services.StockMvtService;
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
public class StockMvtServiceImpl implements StockMvtService {

    private final StockMvtRepository stockMvtRepository;
    private final StockMvtMapper  stockMvtMapper;
    private final ProductRepository  productRepository;

    @Override
    public void create(StockMvtRequest request) {
        //check if product exists
        checkIfProductExistsById(request.getProductId());

        //save entity
        stockMvtRepository.save(this.stockMvtMapper.toEntity(request));

    }

    @Override
    public void update(String id, StockMvtRequest request) {
        final  Optional<StockMvt> stockMvtOptional = this.stockMvtRepository.findById(id);
        if (stockMvtOptional.isEmpty()){
            log.debug("StockMvt not found with id {}", id);
            throw new EntityNotFoundException("StockMvt not found with id " + id);
        }

        checkIfProductExistsById(request.getProductId());

        final StockMvt stockMvtToUpdate = this.stockMvtMapper.toEntity(request);
        stockMvtToUpdate.setId(id);
        this.stockMvtRepository.save(stockMvtToUpdate);

    }

    @Override
    public PageResponse<StockMvtResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvtPage = this.stockMvtRepository.findAll(pageRequest);
        final Page<StockMvtResponse> stockMvtResponses = stockMvtPage.map(stockMvtMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }

    @Override
    public StockMvtResponse findById(String id) {
        return this.stockMvtRepository.findById(id)
                .map(stockMvtMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt not found with id " + id));
    }

    @Override
    public void delete(String id) {
        this.stockMvtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "StockMvt not found with id " + id
                ));
        this.stockMvtRepository.deleteById(id);
    }


    private void checkIfProductExistsById(final String productId) {
        final Optional<Product> productOptional = this.productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            log.debug("Product with id {} does not exists", productId);
            throw new EntityNotFoundException("Product with id " + productId + " does not exists");
        }
    }
}
