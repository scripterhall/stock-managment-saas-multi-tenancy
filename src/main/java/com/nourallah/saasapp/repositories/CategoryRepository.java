package com.nourallah.saasapp.repositories;

import com.nourallah.saasapp.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByNameIgnoreCase(String categoryName);
}
