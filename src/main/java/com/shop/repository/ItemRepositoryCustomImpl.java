package com.shop.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.*;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImg.itemImg;


public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ItemDto> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<ItemDto> content = queryFactory
                .select(new QItemDto(
                        item.id,
                        item.itemName,
                        item.createTime,
                        item.itemSellStatus
                ))
                .from(item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                )
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                );

        return PageableExecutionUtils.getPage(content,pageable, () -> countQuery.fetchOne());


    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (searchBy != null) {
            if (searchBy.equals("itemName")) {
                return item.itemName.like("%"+searchQuery+"%");
            } else if (searchBy.equals("createdBy")) {
                return item.createdBy.like("%"+searchQuery+"%");
            }
        }

        return null;
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (searchDateType != null) {
            if (searchDateType.equals("all") || searchDateType == null) {
                return null;
            } else if (searchDateType.equals("1d")) {
                dateTime = dateTime.minusDays(1);
            } else if (searchDateType.equals("1w")) {
                dateTime = dateTime.minusWeeks(1);
            } else if (searchDateType.equals("1m")) {
                dateTime = dateTime.minusMonths(1);
            } else if (searchDateType.equals("6m")) {
                dateTime = dateTime.minusMonths(6);
            }

            return item.createTime.after(dateTime);
        }
        return null;

    }

        private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : item.itemSellStatus.eq(searchSellStatus);
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<MainItemDto> content = queryFactory
                .select(new QMainItemDto(
                        item.id,
                        item.itemName,
                        item.itemDetail,
                        itemImg.imgUrl,
                        item.price
                ))
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(itemImg.count())
                .from(itemImg)
                .where(itemImg.repimgYn.eq("Y")) //상품 이미지는 대표 이미지만 불러온다.
                .where(itemNameLike(itemSearchDto.getSearchQuery()));

        return PageableExecutionUtils.getPage(content,pageable, () -> countQuery.fetchOne());
    }

    private BooleanExpression itemNameLike(String searchQuery) {
        return !StringUtils.hasText(searchQuery) ? null : item.itemName.like("%"+searchQuery+"%");
    }
}
