package com.nourallah.saasapp.repositories;

import com.nourallah.saasapp.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByReferenceIgnoreCase(String refrence);
}
