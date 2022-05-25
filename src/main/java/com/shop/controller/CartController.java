package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder(); //string 을 연결해줌
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity(sb.toString(), HttpStatus.BAD_REQUEST);

        }

        String email = principal.getName();
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(cartItemId, HttpStatus.OK);
    }

    @GetMapping("/cart")
    public String orderHist(Principal principal, Model model, Pageable pageable) {
        Page<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName(), pageable);
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }

    @PatchMapping("/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable Long cartItemId, int count, Principal principal) {

        if (count <= 0) {
            return new ResponseEntity("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping("/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable Long cartItemId, Principal principal) {

        if(!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity(cartItemId, HttpStatus.OK);
    }

    @PostMapping("/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal) {
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0) {
            return new ResponseEntity("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())) {
                return new ResponseEntity("주문 권한이 없습니다", HttpStatus.FORBIDDEN);
            }
        }
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        return new ResponseEntity(orderId, HttpStatus.OK);
    }
}
