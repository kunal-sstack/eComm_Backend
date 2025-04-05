package com.projectX.backend.Service;

import com.projectX.backend.Payloads.CartDTO;


import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long cardId, Long productId, Integer qty);
    List<CartDTO> getAllCarts();
    CartDTO getCart(String emailId,Long cartId);
    CartDTO updateProductQuantityInCart(Long cartId, Long productId, Integer qty);
    String deleteProductFromCart(Long cartId, Long productId);
}
