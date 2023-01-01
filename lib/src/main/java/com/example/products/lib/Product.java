package com.example.products.lib;


public class Product {

    private Integer productId;
    private String name;

    // Ko prikazujemo produkte je zaenkrat dovolj, da navedemo samo ime kategorije.
    private String category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
