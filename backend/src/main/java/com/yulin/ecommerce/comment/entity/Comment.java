package com.yulin.ecommerce.comment.entity;

import com.yulin.ecommerce.product.entity.Product;
import com.yulin.ecommerce.user.entity.User;
import lombok.Data;
import java.util.Date;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer rating;
    private String content;
    private String reply;
    private Date createTime;
    private Date updateTime;
}