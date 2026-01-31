package com.yulin.ecommerce.product.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Category {
    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Boolean enabled;
    private Date createTime;
    private Date updateTime;
}