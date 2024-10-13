package com.project.shopapp.services;

import com.project.shopapp.dto.CategoriesDTO;
import com.project.shopapp.model.Categories;

import java.util.List;

public interface ICategoriesService {

    Categories createCategories (CategoriesDTO categoriesDTO);

    Categories getCategoryById(Long id);

    List<Categories> getAllCategories();

    Categories updateCategories(Long id, CategoriesDTO categoriesDTO);

    void deleteCategories(Long id);

}
