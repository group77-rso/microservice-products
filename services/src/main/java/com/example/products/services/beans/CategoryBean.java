package com.example.products.services.beans;


import com.example.products.lib.Category;
import com.example.products.models.converters.CategoryConverter;
import com.example.products.models.entities.CategoryEntity;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class CategoryBean {

    private Logger log = Logger.getLogger(CategoryBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Category> getCategories() {

        TypedQuery<CategoryEntity> query = em.createNamedQuery(
                "CategorysEntity.getAll", CategoryEntity.class);

        List<CategoryEntity> resultList = query.getResultList();

        return resultList.stream().map(CategoryConverter::toDto).collect(Collectors.toList());

    }

    public List<Category> getCategoriesFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, CategoryEntity.class, queryParameters).stream()
                .map(CategoryConverter::toDto).collect(Collectors.toList());
    }

    public Category getCategories(Integer id) {

        CategoryEntity categoryEntity = em.find(CategoryEntity.class, id);

        if (categoryEntity == null) {
            throw new NotFoundException();
        }

        Category category = CategoryConverter.toDto(categoryEntity, false);

        return category;
    }

    public Category createCategory(Category category) {

        CategoryEntity categoryEntity = CategoryConverter.toEntity(category);

        try {
            beginTx();
            em.persist(categoryEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (categoryEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return CategoryConverter.toDto(categoryEntity);
    }

    public Category putCategory(Integer id, Category category) {

        CategoryEntity c = em.find(CategoryEntity.class, id);

        if (c == null) {
            return null;
        }

        CategoryEntity updatedCategoryEntity = CategoryConverter.toEntity(category);

        try {
            beginTx();
            updatedCategoryEntity.setId(c.getId());
            updatedCategoryEntity = em.merge(updatedCategoryEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return CategoryConverter.toDto(updatedCategoryEntity);
    }

    public boolean deleteCategory(Integer id) {

        CategoryEntity category = em.find(CategoryEntity.class, id);

        if (category != null) {
            try {
                beginTx();
                em.remove(category);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
