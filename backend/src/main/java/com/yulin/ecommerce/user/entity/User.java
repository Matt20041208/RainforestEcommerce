package com.yulin.ecommerce.user.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private Integer gender;
    private Date birthday;
    private Date createTime;
    private Date updateTime;
    private String role;
}