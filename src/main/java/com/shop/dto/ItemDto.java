package com.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class ItemDto extends BaseEntity {

    private Long id;

    private String itemName;

    private Integer price;

    private String itemDetail;

    private String itemSellStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @QueryProjection
    public ItemDto(Long id, String itemName, LocalDateTime createTime, ItemSellStatus itemSellStatus) {
        this.id = id;
        this.itemName = itemName;
        this.createTime = LocalDateTime.now();
        this.itemSellStatus = itemSellStatus.name();
    }
}
