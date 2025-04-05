package com.projectX.backend.Service;

import com.projectX.backend.Entity.Category;
import com.projectX.backend.Payloads.CategoryDTO;
import com.projectX.backend.Payloads.CategoryResponse;

public interface CategoryService {
    CategoryDTO createCategory(Category category);
    CategoryResponse getCategories(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO updateCategory(Category category, Long categoryId);
    String deleteCategory(Long categoryId);
}
