package com.projectX.backend.Service;

import com.projectX.backend.Entity.Cart;
import com.projectX.backend.Entity.Category;
import com.projectX.backend.Entity.Product;
import com.projectX.backend.Exceptions.APIException;
import com.projectX.backend.Exceptions.ResourceNotFoundException;
import com.projectX.backend.Payloads.CartDTO;
import com.projectX.backend.Payloads.ProductDTO;
import com.projectX.backend.Payloads.ProductResponse;
//import com.projectX.backend.Repository.CartRepository;
import com.projectX.backend.Repository.CartRepository;
import com.projectX.backend.Repository.CategoryRepository;
import com.projectX.backend.Repository.ProductRepository;
import lombok.Value;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{


    @Autowired private ProductRepository pr;
    @Autowired private CartRepository cr;
    @Autowired private CategoryRepository ctr;
    @Autowired private ModelMapper mM;
    @Autowired private FileService fs;
    @Autowired private CartService cs;

    private String path = "image/";


    @Override
    public ProductDTO addProduct(Long categoryId, Product p) {

        Category ct = ctr.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));

        boolean isPNP = true;
        List<Product> prods = ct.getProducts();

        for(int i = 0; i < prods.size();i++){
            if(prods.get(i).getName().equalsIgnoreCase(p.getName()) && prods.get(i).getDescription().equalsIgnoreCase(p.getDescription())){
                isPNP = false;
                break;
            }
        }

        if(isPNP){

            p.setImage("default.png");
            p.setCategory(ct);

            Product savedProd = pr.save(p);
            return mM.map(savedProd, ProductDTO.class);

        }else {
            throw new APIException("Product Already Exists!");
        }


    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable page = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> prodPage = pr.findAll(page);
        List<Product> prods = prodPage.getContent();
        List<ProductDTO> pDs = prods.stream().map(prod-> mM.map(prod,ProductDTO.class)).collect(Collectors.toList());

        ProductResponse prr = new ProductResponse();
        prr.setContent(pDs);
        prr.setPageNumber(prodPage.getNumber());
        prr.setPageSize(prodPage.getSize());
        prr.setTotalPages((long) prodPage.getTotalPages());
        prr.setLastPage(prodPage.isLast());

        return prr;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category ct = ctr.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable page = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> prodPage = pr.findAll(page);
        List<Product> prods = prodPage.getContent();
        if(prods.isEmpty()) throw new APIException("Category with id:"+categoryId+" has no products");
        List<ProductDTO> pDs = prods.stream().map(prod -> mM.map(prod, ProductDTO.class)).collect(Collectors.toList());

        ProductResponse prr = new ProductResponse();

        prr.setContent(pDs);
        prr.setPageNumber(prodPage.getNumber());
        prr.setPageSize(prodPage.getSize());
        prr.setTotalPages((long)prodPage.getTotalPages());
        prr.setLastPage(prodPage.isLast());

        return prr;
    }

    @Override
    public ProductDTO updateProduct(Long productId, Product product) {

        // Fetch product from the database or throw an exception if not found
        Product prodFromDB = pr.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Update only necessary fields from the incoming product
        prodFromDB.setName(product.getName());
        prodFromDB.setDescription(product.getDescription());
        prodFromDB.setPrice(product.getPrice());
        prodFromDB.setCategory(product.getCategory());
        // Do not override the image unless specifically required
        if (product.getImage() != null) {
            prodFromDB.setImage(product.getImage());
        }

        // Save the updated product
        Product savedProd = pr.save(prodFromDB);

        // Handle carts associated with this product
        List<Cart> carts = cr.findCartsByProductId(productId);

        // Map carts to DTOs if required (optional)
        List<CartDTO> cDs = carts.stream()
                .map(cart -> {
                    CartDTO cD = mM.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCi().stream()
                            .map(prod -> mM.map(prod.getProduct(), ProductDTO.class))
                            .collect(Collectors.toList());
                    cD.setProducts(products);
                    return cD;
                }).collect(Collectors.toList());

        // Uncomment and implement if necessary
//         cDs.forEach(cD -> cs.updateProductInCart(cD.getCartId(), productId));

        // Return the updated product as a DTO
        return mM.map(savedProd, ProductDTO.class);
    }


    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product prodFromDB = pr.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        if(prodFromDB==null) throw new APIException("no such prod");

        String fileName = fs.uploadImage(path, image);
        prodFromDB.setImage(fileName);
        Product updatedProduct = pr.save(prodFromDB);
        return mM.map(updatedProduct,ProductDTO.class);

    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable page = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> prodPage = pr.findByNameLike(keyword,page);
        List<Product> prods = prodPage.getContent();
        if(prods.isEmpty()) throw new APIException("No product with keyword:"+keyword+" has no products");
        List<ProductDTO> pDs = prods.stream().map(prod -> mM.map(prod, ProductDTO.class)).collect(Collectors.toList());

        ProductResponse prr = new ProductResponse();

        prr.setContent(pDs);
        prr.setPageNumber(prodPage.getNumber());
        prr.setPageSize(prodPage.getSize());
        prr.setTotalPages((long)prodPage.getTotalPages());
        prr.setLastPage(prodPage.isLast());

        return prr;


    }

    @Override
    public String deleteProduct(Long productId) {
        Product prodFromDB = pr.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        List<Cart> carts = cr.findCartsByProductId(productId);
        carts.forEach(cart -> cs.deleteProductFromCart(cart.getId(), productId));
        pr.delete(prodFromDB);

        return "item with productid:"+productId+"deleted successfully";
    }
}
