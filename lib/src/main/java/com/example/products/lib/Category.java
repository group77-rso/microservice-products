package com.example.products.lib;


import java.util.Set;

public class Category {

    private Integer categoryId;
    private String name;
    private Set<Product> produscts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Set<Product> getProduscts() {
        return produscts;
    }

    public void setProduscts(Set<Product> products) {
        this.produscts = products;
    }
}
