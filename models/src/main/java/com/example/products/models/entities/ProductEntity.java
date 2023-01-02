package com.example.products.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "products")
@NamedQueries(value =
        {
                @NamedQuery(name = "ProductEntity.getAll",
                        query = "SELECT prod FROM ProductEntity prod")
        })
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", referencedColumnName = "categoryid")
    private CategoryEntity category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    @ManyToOne
    public CategoryEntity getCategory() {
        return category;
    }
}