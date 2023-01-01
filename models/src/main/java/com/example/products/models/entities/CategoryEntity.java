package com.example.products.models.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@NamedQueries(value =
        {
                @NamedQuery(name = "CategoryEntity.getAll",
                        query = "SELECT cat FROM CategoryEntity cat")
        })
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryid;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category", targetEntity = ProductEntity.class)
    private Set<ProductEntity> products = new HashSet<>();

    public void setProducts(Set<ProductEntity> products) {
        this.products = products;
    }

    public Set<ProductEntity> getProducts() {
        return products;
    }

    public void setId(Integer id) {
        this.categoryid = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return categoryid;
    }

    public String getName() {
        return name;
    }
}
