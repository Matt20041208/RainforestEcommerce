package com.yulin.ecommerce.address.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Address implements Serializable {
    private Long id;
    private Long userId;
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private boolean isDefault;
}