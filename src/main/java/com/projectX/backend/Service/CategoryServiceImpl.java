package com.projectX.backend.Service;

import com.projectX.backend.Entity.Category;
import com.projectX.backend.Entity.Product;
import com.projectX.backend.Exceptions.APIException;
import com.projectX.backend.Exceptions.ResourceNotFoundException;
import com.projectX.backend.Payloads.CategoryDTO;
import com.projectX.backend.Payloads.CategoryResponse;
import com.projectX.backend.Repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired private CategoryRepository cr;
    @Autowired private ProductService ps;
    @Autowired private ModelMapper mM;

    @Override
    public CategoryDTO createCategory(Category category) {

        Category savedC = cr.findByCategoryName(category.getCategoryName());
        if(savedC != null) throw new APIException("Category already exists");
        savedC = cr.save(category);
        return mM.map(savedC,CategoryDTO.class);

    }

    @Override
    public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> page = cr.findAll(pageDetails);
        List<Category> categories = page.getContent();
        if(categories.isEmpty()) throw new APIException("No such category");

        List<CategoryDTO> cDs = categories.stream().map(category -> mM.map(category,CategoryDTO.class)).collect(Collectors.toList());

        CategoryResponse ctr = new CategoryResponse();

        ctr.setContent(cDs);
        ctr.setPageNumber(page.getNumber());
        ctr.setPageSize(page.getSize());
        ctr.setTotalElements(page.getTotalElements());
        ctr.setTotalPages(page.getTotalPages());
        ctr.setLastPage(page.isLast());

        return ctr;
    }

    @Override
    public CategoryDTO updateCategory(Category category, Long categoryId) {

        Category savedCategory = cr.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        category.setId(categoryId);
        savedCategory = cr.save(savedCategory);
        return mM.map(savedCategory, CategoryDTO.class);

    }

    @Override
    public String deleteCategory(Long categoryId) {

        Category savedCategory = cr.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        List<Product> products = savedCategory.getProducts();
        products.forEach(product -> {
            ps.deleteProduct(product.getProductId());
        });
        cr.delete(savedCategory);

        return "Category with categoryId: "+categoryId+" deleted successfully";
    }
}
