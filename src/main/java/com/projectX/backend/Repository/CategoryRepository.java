package com.projectX.backend.Repository;

import com.projectX.backend.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByCategoryName(String categoryName);
}
