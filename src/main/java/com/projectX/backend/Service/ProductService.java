package com.projectX.backend.Service;

import com.projectX.backend.Entity.Product;
import com.projectX.backend.Payloads.ProductDTO;
import com.projectX.backend.Payloads.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, Product p);
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductDTO updateProduct(Long productId, Product product);
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    String deleteProduct(Long productId);
}
