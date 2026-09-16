package com.nourallah.saasapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "categories")
public class Category extends AbstractEntity{

    @Column(name = "category_name" , nullable = false , unique = true)
    private String name;

    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "category" , fetch = FetchType.EAGER)
    private List<Product> products;
}
