package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@NoArgsConstructor
@ToString
public class Item extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;


    public Item(String itemName, int price, int stockNumber, String itemDetail, ItemSellStatus itemSellStatus, LocalDateTime createTime, LocalDateTime updateTime) {

        this.itemName = itemName;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
//        this.createTime = createTime;
//        this.updateTime = updateTime;
    }

    public void updateItem(ItemFormDto itemFormDto) {
        itemName = itemFormDto.getItemName();
        price = itemFormDto.getPrice();
        stockNumber = itemFormDto.getStockNumber();
        itemDetail = itemFormDto.getItemDetail();
        itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber +")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    }
}
