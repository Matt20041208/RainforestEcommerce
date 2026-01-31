package com.yulin.ecommerce.address.service.impl;

import com.yulin.ecommerce.address.entity.Address;
import com.yulin.ecommerce.address.mapper.AddressMapper;
import com.yulin.ecommerce.address.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    public Address createAddress(Address address) {
        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.isDefault()) {
            List<Address> userAddresses = getAddressesByUserId(address.getUserId());
            for (Address userAddress : userAddresses) {
                if (userAddress.isDefault()) {
                    userAddress.setDefault(false);
                    addressMapper.updateById(userAddress);
                }
            }
        }
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    public Address updateAddress(Long id, Address address) {
        Address existingAddress = getAddressById(id);
        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.isDefault()) {
            List<Address> userAddresses = getAddressesByUserId(existingAddress.getUserId());
            for (Address userAddress : userAddresses) {
                if (userAddress.isDefault() && !userAddress.getId().equals(id)) {
                    userAddress.setDefault(false);
                    addressMapper.updateById(userAddress);
                }
            }
        }
        existingAddress.setReceiver(address.getReceiver());
        existingAddress.setPhone(address.getPhone());
        existingAddress.setProvince(address.getProvince());
        existingAddress.setCity(address.getCity());
        existingAddress.setDistrict(address.getDistrict());
        existingAddress.setDetailAddress(address.getDetailAddress());
        existingAddress.setDefault(address.isDefault());
        addressMapper.updateById(existingAddress);
        return existingAddress;
    }
    
    @Override
    public void deleteAddress(Long id) {
        addressMapper.deleteById(id);
    }
    
    @Override
    public Address getAddressById(Long id) {
        return addressMapper.findById(id);
    }
    
    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }
    
    @Override
    public Address getDefaultAddressByUserId(Long userId) {
        List<Address> addresses = getAddressesByUserId(userId);
        for (Address address : addresses) {
            if (address.isDefault()) {
                return address;
            }
        }
        return addresses.isEmpty() ? null : addresses.get(0);
    }
}