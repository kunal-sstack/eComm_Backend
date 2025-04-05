package com.projectX.backend.Service;

import com.projectX.backend.Entity.Cart;
import com.projectX.backend.Entity.CartItem;
import com.projectX.backend.Entity.Product;
import com.projectX.backend.Exceptions.APIException;
import com.projectX.backend.Exceptions.ResourceNotFoundException;
import com.projectX.backend.Payloads.CartDTO;
import com.projectX.backend.Payloads.ProductDTO;
import com.projectX.backend.Repository.CartItemRepository;
import com.projectX.backend.Repository.CartRepository;
import com.projectX.backend.Repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService{

    @Autowired private CartRepository cr;
    @Autowired private ProductRepository pr;
    @Autowired private CartItemRepository cir;
    @Autowired private ModelMapper mM;

    @Override
    public CartDTO addProductToCart(Long cardId, Long productId, Integer qty) {

        Cart cart = cr.findById(cardId).orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cardId));
        Product product = pr.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        CartItem cartItem = cir.findCartItemByProductIdAndCartId(cardId,productId);
        if(cartItem==null) throw new APIException("no such product with pid:"+productId+"in cart with cid:"+cardId+"exists.");
        if(product.getQty() < qty){
            throw new APIException("no such quantity available");
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQty(qty);
        newCartItem.setCart(cart);
        newCartItem.setCartItemPrice(product.getPrice() - product.getPrice()*cartItem.getDiscount());

        cir.save(newCartItem);
        product.setQty(product.getQty()-qty);
        cart.setTotalCartPrice(cart.getTotalCartPrice()+qty*newCartItem.getCartItemPrice());
        CartDTO cD = mM.map(cart, CartDTO.class);
        List<ProductDTO> pDs = cart.getCi().stream().map(item -> mM.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());

        cD.setProducts(pDs);
        cD.setTotalPrice(cart.getTotalCartPrice());
        return cD;
    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cr.findAll();
        if(carts.isEmpty()) throw new APIException("no cart exist");

        List<CartDTO> cDs = carts.stream().map(cart -> {
            CartDTO cDTO = mM.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCi().stream().map(product->mM.map(product.getProduct(),ProductDTO.class)).collect(Collectors.toList());
            cDTO.setProducts(products);
            cDTO.setTotalPrice(cart.getTotalCartPrice());
            return cDTO;
        }).collect(Collectors.toList());

        return cDs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId){

        Cart cart = cr.findCartByEmailAndCartId(emailId, cartId);
        if(cart==null) throw new ResourceNotFoundException("Cart","cartId",cartId);
        CartDTO cartDTO = mM.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCi().stream().map(item -> mM.map(item, ProductDTO.class)).collect(Collectors.toList());

        cartDTO.setProducts(products);
        cartDTO.setTotalPrice(cart.getTotalCartPrice());

        return cartDTO;
    }

    @Override
    public CartDTO updateProductQuantityInCart(Long cartId, Long productId, Integer qty) {

        Cart cart = cr.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));
        Product product = pr.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        CartItem cartItem = cir.findCartItemByProductIdAndCartId(productId,cartId);

        if(cartItem == null) throw new APIException("no such product with pid:"+productId+" with cartid: "+cartId+" found.");

        cartItem.setProduct(product);
        cartItem.setCartItemPrice(0.0);

        if(product.getQty() < qty) throw new APIException("no that quantity available");

        product.setQty(product.getQty() + cartItem.getQty() - qty);

        cart.setTotalCartPrice(cart.getTotalCartPrice() - product.getPrice()*cartItem.getQty());//initial qty
        cart.getCi().remove(cartItem);

        cartItem.setQty(qty);
        cartItem.setDiscount(product.getPrice()*qty*0.1);

        cart.getCi().add(cartItem);
        cart.setTotalCartPrice(cart.getTotalCartPrice() + product.getPrice()*qty - cartItem.getDiscount());

        cir.save(cartItem);
        CartDTO cDTO = mM.map(cart, CartDTO.class);
        cDTO.setTotalPrice(cartItem.getCartItemPrice());

        List<ProductDTO> products = cart.getCi().stream().map(prod->mM.map(prod.getProduct(),ProductDTO.class)).collect(Collectors.toList());

        cDTO.setProducts(products);
        return cDTO;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cr.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));
        CartItem cartItem = cir.findCartItemByProductIdAndCartId(productId,cartId);
        if(cartItem == null) throw new APIException("fujc");

        Product product = cartItem.getProduct();

        cart.setTotalCartPrice(cart.getTotalCartPrice() - product.getPrice()*cartItem.getQty() + cartItem.getDiscount());

        product.setQty(product.getQty() + cartItem.getQty());

        cir.deleteCartItemByProductIdAndCartId(productId,cartId);

        return "products with pid: "+productId+" successfully deleted from cart with cid: "+cartId;
    }
}
