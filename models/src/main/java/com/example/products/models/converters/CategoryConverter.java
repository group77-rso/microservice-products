package com.example.products.models.converters;

import com.example.products.lib.Category;
import com.example.products.lib.Product;
import com.example.products.models.entities.CategoryEntity;
import com.example.products.models.entities.ProductEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoryConverter {

    public static Category toDto(CategoryEntity entity, boolean compact) {

        if (compact){
            return toDto(entity);
        }
        Category dto = new Category();
        dto.setCategoryId(entity.getId());
        dto.setName(entity.getName());
        dto.setProducts(getProductsFromCategoryEntity(entity.getProducts()));

        return dto;
    }

    public static Category toDto(CategoryEntity entity) {

        Category dto = new Category();
        dto.setCategoryId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    public static CategoryEntity toEntity(Category dto) {

        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName());
        if (dto.getProducts() != null) {
            Set<ProductEntity> productEntities = dto.getProducts().stream().map(ProductsConverter::toEntity).collect(Collectors.toSet());
            entity.setProducts(productEntities);
        }

        return entity;
    }

    private static Set<Product> getProductsFromCategoryEntity(Set<ProductEntity> productEntitySet){
        Set<Product> products = new HashSet<>();
        for (ProductEntity pe : productEntitySet){
            products.add(ProductsConverter.toDto(pe));
        }
        return products;
    }

}
