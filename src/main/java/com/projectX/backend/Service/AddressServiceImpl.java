package com.projectX.backend.Service;

import com.projectX.backend.Entity.Address;
import com.projectX.backend.Entity.User;
import com.projectX.backend.Exceptions.APIException;
import com.projectX.backend.Exceptions.ResourceNotFoundException;
import com.projectX.backend.Payloads.AddressDTO;
import com.projectX.backend.Repository.AddressRepository;
import com.projectX.backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class AddressServiceImpl implements AddressService{

    @Autowired private AddressRepository ar;
    @Autowired private UserRepository ur;
    @Autowired private ModelMapper mM;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {

        String city = addressDTO.getCity();
        String pincode = addressDTO.getPincode();
        String street = addressDTO.getStreet();
        String building = addressDTO.getBuildingName();

        Address addressFromDb = ar.findByCityAndPincodeAndStreetAndBuildingName(city,pincode,street,building);
        if(addressFromDb != null) throw new APIException("Address already exists");

        Address address = mM.map(addressDTO, Address.class);
        Address savedAddress = ar.save(address);

        return mM.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        
        List<Address> addresses = ar.findAll();
        List<AddressDTO> aDs = addresses.stream().map(address -> mM.map(address,AddressDTO.class)).collect(Collectors.toList());
        
        return aDs;
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
        Address address = ar.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
        return mM.map(address, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddress(Long addressId, Address address) {

        Address addressFromDb = ar.findByCityAndPincodeAndStreetAndBuildingName(address.getCity(),address.getPincode(),address.getStreet(),address.getBuildingName());

        if(addressFromDb == null) {
            addressFromDb = ar.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));

            addressFromDb.setCity(address.getCity());
            addressFromDb.setPincode(address.getPincode());
            addressFromDb.setStreet(address.getStreet());
            addressFromDb.setBuildingName(address.getBuildingName());

            Address updatedAddress = ar.save(addressFromDb);
            return mM.map(updatedAddress, AddressDTO.class);
        }

        //else code

        List<User> users = ur.findByAddress(addressId);
        final Address adrs = addressFromDb;
        users.forEach(user -> user.getAddresses().add(adrs));
        deleteAddress(addressId);

        return mM.map(addressFromDb, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDb = ar.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
        List<User> users = ur.findByAddress(addressId);
        users.forEach(user -> {
            user.getAddresses().remove(addressFromDb);
            ur.save(user);
        });
        ar.deleteById(addressId);
        return "address with addressId:"+addressId+" deleted.";
    }
}
