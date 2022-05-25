package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType; //상품 등록일
    private ItemSellStatus searchSellStatus;
    private String searchBy; //itemName OR createdBy 로 조회
    private String searchQuery = "";


}
