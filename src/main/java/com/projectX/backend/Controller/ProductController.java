package com.projectX.backend.Controller;

import com.projectX.backend.Configuration.AppConstants;
import com.projectX.backend.Entity.Product;
import com.projectX.backend.Payloads.ProductDTO;
import com.projectX.backend.Payloads.ProductResponse;
import com.projectX.backend.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/aoi")
public class ProductController {

    @Autowired private ProductService ps;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody Product product, @PathVariable Long categoryId) {
        ProductDTO pd = ps.addProduct(categoryId,product);
        return new ResponseEntity<ProductDTO>(pd, HttpStatus.CREATED);
    }

    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.P_N, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.P_S, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.S_BY_PID, required = false) String sortBy,
            @RequestParam(name = "SortOrder", defaultValue = "ASC", required = false) String sortOrder
            ) {
        ProductResponse pr = ps.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<ProductResponse>(pr, HttpStatus.FOUND);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.P_N, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.P_S, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.S_BY_PID, required = false) String sortBy,
            @RequestParam(name = "SortOrder", defaultValue = "ASC", required = false) String sortOrder
                                                                ) {

        ProductResponse pr = ps.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<ProductResponse>(pr, HttpStatus.FOUND);

    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.P_N, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.P_S, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.S_BY_PID, required = false) String sortBy,
            @RequestParam(name = "SortOrder", defaultValue = "ASC", required = false) String sortOrder
    ) {

        ProductResponse pr = ps.searchProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<ProductResponse>(pr, HttpStatus.FOUND);

    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody Product product,
                                                    @PathVariable Long productId) {
        ProductDTO updatedProduct = ps.updateProduct(productId, product);

        return new ResponseEntity<ProductDTO>(updatedProduct, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException, IOException {
        ProductDTO updatedProduct = ps.updateProductImage(productId, image);

        return new ResponseEntity<ProductDTO>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProductByCategory(@PathVariable Long productId) {
        String status = ps.deleteProduct(productId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }


}
