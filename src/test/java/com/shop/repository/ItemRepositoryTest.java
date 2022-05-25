package com.shop.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.shop.entity.QItem.item;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager em;



    @Test
    public void createItem() throws Exception {
        //given
        LocalDateTime createDate = LocalDateTime.of(2022, 5, 17,11,11);
        LocalDateTime updateDate = LocalDateTime.of(2022, 5, 17,11,11);

        Item item = new Item("테스트 상품", 10000, 100, "상세 설명", ItemSellStatus.SELL, createDate, updateDate);
        //when
        Item savedItem = itemRepository.save(item);
        //then
        assertThat(item.getId()).isEqualTo(savedItem.getId());
    }


    @Test
    public void findItem() throws Exception {
        //given
        LocalDateTime createDate = LocalDateTime.of(2022, 5, 17,11,11);
        LocalDateTime updateDate = LocalDateTime.of(2022, 5, 17,11,11);

        Item item = new Item("테스트 상품", 10000, 100, "상세 설명", ItemSellStatus.SELL, createDate, updateDate);
        //when
        Item savedItem = itemRepository.save(item);
//        List<Item> result = itemRepository.findByItemName(savedItem.getItemName());
//        List<Item> result = itemRepository.findByItemNameOrItemDetail(null, "상세 설명");
        List<Item> result = itemRepository.findByPriceLessThan(10005);

        //then
        assertThat(result.get(0).getItemName()).isEqualTo("테스트 상품");
    }

    public void createItemList() {
        LocalDateTime createDate = LocalDateTime.of(2022, 5, 17,11,11);
        LocalDateTime updateDate = LocalDateTime.of(2022, 5, 17,11,11);

        for (int i= 1; i<=10;i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setCreateTime(createDate);
            item.setUpdateTime(updateDate);
            Item savedItem = itemRepository.save(item);
        }
    }

    public void createItemList2() {
        LocalDateTime createDate = LocalDateTime.of(2022, 5, 17,11,11);
        LocalDateTime updateDate = LocalDateTime.of(2022, 5, 17,11,11);

        for (int i= 1; i<=5;i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setCreateTime(createDate);
            item.setUpdateTime(updateDate);
            Item savedItem = itemRepository.save(item);
        }
        for (int i= 6; i<=10;i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setCreateTime(createDate);
            item.setUpdateTime(updateDate);
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    public void findItemDetailUsingQuery() throws Exception {
        //given
        this.createItemList();
        //when
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        //then
        for (Item item : itemList) {
            System.out.println("item = " + item.toString());
        }
    }

    @Test
    public void queryDslTest() throws Exception {
        //given
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //when
        List<Item> result = queryFactory
                .selectFrom(item)
                .where(item.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(item.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(item.price.desc())
                .fetch();
        //then
        for (Item item1 : result) {
            System.out.println("item1 = " + item1.toString());
        }
    }

    @BeforeEach
    public void before() {

    }
    
    @Test
    public void dynamicQuery_WhereParam() throws Exception {
        //given
        this.createItemList();

        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        ItemSellStatus itemSellStat = ItemSellStatus.SELL;
        //when
        List<Item> result = searchItem(itemDetail, price, itemSellStat);
        //then
        for (Item item1 : result) {
            System.out.println("item1 = " + item1.toString());
        }
//        assertThat(result.size()).isEqualTo(1);
    }

    private List<Item> searchItem(String itemDetailCond, Integer priceCond, ItemSellStatus itemSellStatCond) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .selectFrom(item)
                .where(itemDetailEq(itemDetailCond), priceEq(priceCond), itemSellStatEq(itemSellStatCond))
                .fetch();

    }

    private BooleanExpression itemSellStatEq(ItemSellStatus itemSellStatCond) {
        return  itemSellStatCond != null ? item.itemSellStatus.eq(itemSellStatCond) : null;
    }

    private BooleanExpression priceEq(Integer priceCond) {
        return priceCond != null ? item.price.eq(priceCond) : null;
    }

    //아 왜 안나오나 했는데 eq로 하니깐 안나오지 ㅋㅋ
    private BooleanExpression itemDetailEq(String itemDetailCond) {
        return itemDetailCond != null ? item.itemDetail.like("%"+itemDetailCond+"%") : null;
    }


}