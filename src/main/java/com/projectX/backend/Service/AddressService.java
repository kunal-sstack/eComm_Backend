package com.projectX.backend.Service;

import com.projectX.backend.Entity.Address;
import com.projectX.backend.Payloads.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);
    List<AddressDTO> getAddresses();
    AddressDTO getAddress(Long addressId);
    AddressDTO updateAddress(Long addressId, Address address);
    String deleteAddress(Long addressId);
}
