package com.shop.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.dto.CartDetailDto;
import com.shop.entity.QCartItem;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.shop.entity.QCartItem.*;
import static com.shop.entity.QItem.*;
import static com.shop.entity.QItemImg.*;

public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public CartItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CartDetailDto> findCartDetailDtoList(Long cartId, Pageable pageable) {
        List<CartDetailDto> content = queryFactory
                .select(Projections.constructor(CartDetailDto.class,
                        cartItem.id.as("cartItemId"),
                        item.itemName.as("itemName"),
                        item.price.as("price"),
                        cartItem.count.as("count"),
                        itemImg.imgUrl.as("imgUrl")
                ))
                .from(cartItem, itemImg)
                .join(cartItem.item, item)
                .where( cartItem.cart.id.eq(cartId),
                        itemImg.item.eq(cartItem.item),
                        itemImg.repimgYn.eq("Y")
                )
                .orderBy(cartItem.createTime.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(cartItem.count())
                .from(cartItem, itemImg)
                .join(cartItem.item, item)
                .where(cartItem.cart.id.eq(cartId),
                        itemImg.item.id.eq(cartItem.item.id),
//                        itemImg.item.eq(cartItem.item),
                        itemImg.repimgYn.eq("Y")
                );

        return PageableExecutionUtils.getPage(content,pageable, () -> countQuery.fetchOne());
    }


}
