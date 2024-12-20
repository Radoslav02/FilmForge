package com.example.project_spring.mapper;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.entity.Category;

public class CategoryMapper {

    public static CategoryDTO mapToCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getCreationDate()
        );
    }

    public static Category mapToCategory(CategoryDTO categoryDTO) {
        return new Category(
                categoryDTO.getId(),
                categoryDTO.getName(),
                categoryDTO.getCreationDate()
        );
    }



}
