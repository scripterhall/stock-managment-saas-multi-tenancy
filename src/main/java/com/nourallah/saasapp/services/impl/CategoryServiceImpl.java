package com.nourallah.saasapp.services.impl;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.entities.Category;
import com.nourallah.saasapp.exceptions.DuplicateResourceException;
import com.nourallah.saasapp.mappers.CategoryMapper;
import com.nourallah.saasapp.repositories.CategoryRepository;
import com.nourallah.saasapp.requests.CategoryRequest;
import com.nourallah.saasapp.responses.CategoryResponse;
import com.nourallah.saasapp.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void create(final CategoryRequest request) {
        if(categoryRepository.findByNameIgnoreCase(request.getName()).isPresent()){
            log.info("Category already exists");
            throw new DuplicateResourceException("Category already exists !"); // TODO custom exception
        }
        this.categoryRepository.save(categoryMapper.toEntity(request));
    }

    @Override
    public void update(String id, CategoryRequest request) {
        // check if category already exists by id
        Optional<Category> categoryExist = categoryRepository.findById(id);
        if(categoryExist.isEmpty()){
            log.debug("Category does not exist");
            throw new EntityNotFoundException("Category does not exist");
        }
        // exist by name
        if(categoryExist.get().getName().equals(request.getName())){
            log.info("Category already exists with the same name");
            throw new DuplicateResourceException("Category already exists !");
        }

        final Category categoryToUpdate = categoryMapper.toEntity(request);
        categoryToUpdate.setId(id);
        this.categoryRepository.save(categoryToUpdate);

    }

    @Override
    public PageResponse<CategoryResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Category> categories = categoryRepository.findAll(pageRequest);
        final Page<CategoryResponse> categoryResponses = categories.map(categoryMapper::toResponse);
        return PageResponse.of(categoryResponses);
    }

    @Override
    public CategoryResponse findById(String id) {

       return this.categoryRepository.findById(id)
               .map(categoryMapper::toResponse)
               .orElseThrow(() -> new EntityNotFoundException("Category does not exist"));

    }

    @Override
    public void delete(String id) {
        final Category category = this.categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category does not exist"));
        this.categoryRepository.delete(category);
    }
}
