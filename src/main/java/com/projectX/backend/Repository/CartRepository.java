package com.projectX.backend.Repository;

import com.projectX.backend.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("SELECT c from Cart c WHERE c.user.email = ?1 AND c.id = ?2")
    Cart findCartByEmailAndCartId(String email, Long cartId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.ci ci JOIN FETCH ci.product p WHERE p.productId = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
