package com.yulin.ecommerce.address.mapper;

import com.yulin.ecommerce.address.entity.Address;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressMapper {
    List<Address> findByUserId(Long userId);
    Address findById(Long id);
    int insert(Address address);
    int updateById(Address address);
    int deleteById(Long id);
}