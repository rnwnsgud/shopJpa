package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ItemImg extends BaseEntity{

    @Id
    @Column(name = "item_img_id")
    @GeneratedValue
    private Long id;

    private String imgName;
    private String oriImgName; //원본 이미지 파일명
    private String imgUrl;
    private String repimgYn; //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void updateItemImg(String imgName, String oriImgName, String imgUrl) {
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
    }
}
