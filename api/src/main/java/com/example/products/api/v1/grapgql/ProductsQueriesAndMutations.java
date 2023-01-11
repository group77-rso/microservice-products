package com.example.products.api.v1.grapgql;

import com.example.products.lib.Product;
import com.example.products.services.beans.ProductBean;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestScoped
@GraphQLApi
public class ProductsQueriesAndMutations {

    @Inject
    private ProductBean productBean;

    // QUERIES

    @Query
    public List<Product> getProducts() {

        return productBean.getProducts();
    }

    @Query
    public Product getProduct(@Name("Product_id") Integer id) {
        return productBean.getProducts(id);
    }

    // MUTATIONS

    @Mutation
    public Product addNewProduct(@Name("productName") String productName) {
        Product newProduct = new Product();
        newProduct.setName(productName);
        return productBean.createProduct(newProduct);
    }

    @Mutation
    public DeleteResponse deleteProduct(@NotNull Integer id) {
        return new DeleteResponse(productBean.deleteProduct(id));
    }

}
