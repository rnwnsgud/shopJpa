package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    EntityManager em;

    @Test
    public void lazyLoading() throws Exception {
        //given
        Order order = createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        //when
        em.flush();
        em.clear();
        //then
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class = " + orderItem.getOrder().getClass());
        System.out.println("=============================");
        orderItem.getOrder().getOrderDate();
        System.out.println("=============================");
    }

    public Order createOrder() {
        Order order = new Order();
        for (int i=0; i<3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);

        }
        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;

    }

    @Test
    public void orphanRemoval() throws Exception {
        Order order = createOrder();
        order.getOrderItems().remove(0);
        em.flush();
    }

    public Item createItem() {
        Item item = new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    public void cascadeTest() throws Exception {
        //given
        Order order = new Order();
        for (int i=0; i<3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);

            orderItem.setOrder(order); // orderItem 에 외래키 설정했지

            order.getOrderItems().add(orderItem);

        }
        //when
        orderRepository.save(order);
        em.flush();
        em.clear();
        //then
        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertThat(savedOrder.getOrderItems().size()).isEqualTo(3);
    }





}