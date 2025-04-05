package com.projectX.backend.Repository;

import com.projectX.backend.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByCityAndPincodeAndStreetAndBuildingName(String city, String pincode, String street, String buildingName);
}
