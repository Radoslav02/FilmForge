package com.example.project_spring.service.impl;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.mapper.CategoryMapper;
import com.example.project_spring.repository.CategoryRepository;
import com.example.project_spring.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.ReadOnlyFileSystemException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category category = CategoryMapper.mapToCategory(categoryDTO);
        category.setCreationDate(new Date());
        Category savedCategory = categoryRepository.save(category);

        return CategoryMapper.mapToCategoryDTO(savedCategory);
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryID) {
        Category category = categoryRepository.findById(categoryID).orElseThrow(() ->
                new ResourceNotFoundException("Category with given id does not exist: " + categoryID));

        return CategoryMapper.mapToCategoryDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map((category) -> CategoryMapper.mapToCategoryDTO(category))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO updatedCategory) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category is not exists with given id: " + categoryId));

        category.setName(updatedCategory.getName());
        category.setCreationDate(updatedCategory.getCreationDate());

        Category updatedCategoryObj = categoryRepository.save(category);

        return CategoryMapper.mapToCategoryDTO(updatedCategoryObj);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category is not exists with given id: " + categoryId));

        categoryRepository.deleteById(categoryId);
    }
}