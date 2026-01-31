package com.yulin.ecommerce.address.service;

import com.yulin.ecommerce.address.entity.Address;

import java.util.List;

public interface AddressService {
    Address createAddress(Address address);
    Address updateAddress(Long id, Address address);
    void deleteAddress(Long id);
    Address getAddressById(Long id);
    List<Address> getAddressesByUserId(Long userId);
    Address getDefaultAddressByUserId(Long userId);
}