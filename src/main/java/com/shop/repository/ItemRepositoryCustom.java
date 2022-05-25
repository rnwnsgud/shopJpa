package com.shop.repository;

import com.shop.dto.ItemDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<ItemDto> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
