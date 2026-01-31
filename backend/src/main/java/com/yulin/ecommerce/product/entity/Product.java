package com.yulin.ecommerce.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Boolean enabled;
    private Long categoryId;
    private Date createTime;
    private Date updateTime;
}