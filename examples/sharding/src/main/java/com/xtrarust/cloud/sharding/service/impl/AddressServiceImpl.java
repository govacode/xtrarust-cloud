package com.xtrarust.cloud.sharding.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xtrarust.cloud.sharding.entity.Address;
import com.xtrarust.cloud.sharding.mapper.AddressMapper;
import com.xtrarust.cloud.sharding.service.IAddressService;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}
