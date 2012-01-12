package com.idega.block.article.data.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.article.data.CategoryEntity;
import com.idega.block.article.data.dao.CategoryDao;
import com.idega.content.business.categories.event.CategoryAddedEvent;
import com.idega.content.business.categories.event.CategoryDeletedEvent;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.SimpleQuerier;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/**
 * Data Access Object class for accessing "ART_CATEGORY" table
 * @author martynas
 * Last changed: 2011.09.22
 * You can report about problems to: martynas@idega.com
 * You can expect to find some test cases notice in the end of the file.
 */
@Repository
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CategoryDaoImpl extends GenericDaoImpl implements CategoryDao, ApplicationListener {

    private static Logger LOGGER = Logger.getLogger(CategoryDaoImpl.class.getName());

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#addCategory(java.lang.String)
     */
    @Override
    @Transactional(readOnly = false)
    public boolean addCategory(String category) {
        if (StringUtil.isEmpty(category)) {
            LOGGER.warning("Category name is not provided!");
            return false;
        }

        if (!this.isCategoryExists(category)) {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCategory(category);

            try {
                this.persist(categoryEntity);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to add category to database: " 
                        + categoryEntity, e);
            }

            return categoryEntity.getId() != null;
        }
        return true;
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#addCategories(java.util.List)
     */
    @Override
    @Transactional(readOnly = false)
    public List<String> addCategories(List<String> categories) {
        if (ListUtil.isEmpty(categories))
            return null;

        List<String> categoriesNotAdded = new ArrayList<String>();
        for (String s : categories) {
            if (!this.addCategory(s)) {
                categoriesNotAdded.add(s);
            }
        }
        return categoriesNotAdded;
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#deleteCategory(java.lang.String)
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteCategory(String category) {
        if (StringUtil.isEmpty(category)) {
            return false;
        }

        return this.deleteCategories(Arrays.asList(category));
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#deleteCategories(java.util.List)
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteCategories(List<String> categories) {
        if (ListUtil.isEmpty(categories))
            return false;
        List<CategoryEntity> categoryEntitiesToDelete = this.getCategories(categories);

        String numbers = "";

        if (ListUtil.isEmpty(categoryEntitiesToDelete)) {
            return Boolean.FALSE;
        }

        for (CategoryEntity s : categoryEntitiesToDelete) {
            try {
                numbers = numbers + s.getId()+",";
                this.remove(s);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to remove category from database: " + s, e);
                return false;
            }
        }
        numbers = numbers.substring(0, numbers.lastIndexOf(","));

        String query = "DELETE FROM jnd_article_category WHERE jnd_article_category.category_fk IN ("+ numbers +")";
        try{
            SimpleQuerier.executeUpdate(query, true);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to remove connections between categories and articles from database: " + query, e);
            return false;
        }

        return true;
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#getCategories()
     */
    @Override
    public List<CategoryEntity> getCategories() {
        return this.getResultList(CategoryEntity.GET_ALL, CategoryEntity.class);
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#getCategories(java.util.List)
     */
    @Override
    public List<CategoryEntity> getCategories(List<String> categories) {
        if (ListUtil.isEmpty(categories))
            return null;

        return getResultList(CategoryEntity.GET_BY_NAMES, CategoryEntity.class,
                new Param(CategoryEntity.categoryProp, categories));
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#getCategory(java.lang.String)
     */
    @Override
    public CategoryEntity getCategory(String category) {
        CategoryEntity categoryEntity = null;
        categoryEntity = this.getSingleResult(CategoryEntity.GET_BY_NAME,
                CategoryEntity.class, new Param(CategoryEntity.categoryProp,
                        category));

        return categoryEntity;
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#isCategoryExists(java.lang.String)
     */
    @Override
    public boolean isCategoryExists(String category) {
        if (StringUtil.isEmpty(category))
            return false;

        List<String> categoryEntities = this.getNotExistingCategoriesFromThisList(Arrays.asList(category));

        if (ListUtil.isEmpty(categoryEntities))
            return true;

        return false;
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#getNotExistingCategoriesFromThisList(java.util.List)
     */
    @Override
    public List<String> getNotExistingCategoriesFromThisList(List<String> categories) {
        if (ListUtil.isEmpty(categories))
            return null;

        List<String> nonExistingCategories = new ArrayList<String>(categories);
        List<CategoryEntity> categoryEntities = this.getCategories();
		
        if (ListUtil.isEmpty(categoryEntities)) {
            return categories;
        }
		
        for (CategoryEntity s : categoryEntities) {
            if (s == null) {
                continue;
            }
		    
            String category = s.getCategory();
            if (StringUtil.isEmpty(category)) {
                continue;
            }
		    
            if (categories.contains(category)) {
                nonExistingCategories.remove(s.getCategory());
            }
        }

        return nonExistingCategories;
    }

    /**
     * @see com.idega.block.article.data.dao.CategoryDao#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    @Transactional(readOnly = false)
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof CategoryDeletedEvent) {
            this.deleteCategory(((CategoryDeletedEvent) event).getCategoryId());
        }

        if (event instanceof CategoryAddedEvent) {
            this.addCategory(((CategoryAddedEvent) event).getCategoryId());
        }
    }
}
