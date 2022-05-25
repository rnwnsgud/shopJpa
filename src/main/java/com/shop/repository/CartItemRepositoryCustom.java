package com.shop.repository;

import com.shop.dto.CartDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartItemRepositoryCustom {

    Page<CartDetailDto> findCartDetailDtoList(Long cartId, Pageable pageable);
}
