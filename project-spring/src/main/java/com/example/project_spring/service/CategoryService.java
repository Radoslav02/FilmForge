package com.example.project_spring.service;

import com.example.project_spring.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long categoryID);

    List<CategoryDTO> getAllCategories();

    CategoryDTO updateCategory(Long categoryId, CategoryDTO updatedCategory);

    void deleteCategory(Long categoryID);
}
