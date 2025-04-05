package com.projectX.backend.Service;

import com.projectX.backend.Configuration.AppConstants;
import com.projectX.backend.Entity.*;
import com.projectX.backend.Exceptions.APIException;
import com.projectX.backend.Exceptions.ResourceNotFoundException;
import com.projectX.backend.Payloads.*;
import com.projectX.backend.Repository.AddressRepository;
import com.projectX.backend.Repository.RoleRepository;
import com.projectX.backend.Repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired private RoleRepository roleRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired
    private ModelMapper mM;
    @Autowired
    private UserRepository userRepository;
    @Autowired private PasswordEncoder pE;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        try{
            User u = mM.map(userDTO, User.class);
            Cart c = new Cart();
            u.setCart(c);

            Role role = roleRepository.findById(u.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User","userId",u.getUserId()));
            u.getRoles().add(role);

            String city = userDTO.getAddress().getCity();
            String pincode = userDTO.getAddress().getPincode();
            String street = userDTO.getAddress().getStreet();
            String buildingName = userDTO.getAddress().getBuildingName();

            Address a = addressRepository.findByCityAndPincodeAndStreetAndBuildingName(city,pincode,street,buildingName);
            if(a==null){
                a = new Address(city,pincode,street,buildingName);
                a = addressRepository.save(a);
            }

            u.setAddresses(List.of(a));
            User regU = userRepository.save(u);
            c.setUser(regU);
            userDTO = mM.map(regU, UserDTO.class);
            userDTO.setAddress(mM.map(u.getAddresses().stream().findFirst().get(), AddressDTO.class));
            return userDTO;

        }catch(DataIntegrityViolationException e){
            throw new APIException("User already exists with email:"+userDTO.getEmail());
        }
    }

    @Override
    public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<User> userPage = userRepository.findAll((pageDetails));

        List<User> users = userPage.getContent();

        if(users.isEmpty()) throw new APIException("No user exists!");

        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO uD = mM.map(user, UserDTO.class);
            if(!user.getAddresses().isEmpty()){
                uD.setAddress(mM.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

                CartDTO cD = mM.map(user.getCart(), CartDTO.class);
                List<ProductDTO> prods = user.getCart().getCi().stream().map(prod ->
                        mM.map(prod.getProduct(), ProductDTO.class)).collect(Collectors.toList());

                uD.setCart(cD);
                uD.getCart().setProducts(prods);
            }

            return uD;
        }).collect(Collectors.toList());

        UserResponse ur = new UserResponse();
        ur.setContent(userDTOs);
        ur.setPageNumber(userPage.getNumber());
        ur.setPageSize(userPage.getSize());
        ur.setTotalElements(userPage.getTotalElements());
        ur.setTotalPages(userPage.getTotalPages());
        ur.setLastPage(userPage.isLast());

        return ur;
    }

    @Override
    public UserDTO getUserById(Long userId) {

        User u = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","userId",userId));
        UserDTO userDTO = mM.map(u, UserDTO.class);
        userDTO.setAddress(mM.map(u.getAddresses().stream().findFirst().get(),AddressDTO.class));

        CartDTO cart = mM.map(u.getCart(), CartDTO.class);
        List<ProductDTO> prods = u.getCart().getCi().stream().map(prod -> mM.map(prod.getProduct(), ProductDTO.class)).collect(Collectors.toList());

        userDTO.setCart(cart);
        userDTO.getCart().setProducts(prods);

        return userDTO;
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO uD) {

        User u = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","userId",userId));

        String encPass = pE.encode(uD.getPassword());
        u.setName(uD.getName());
        u.setEmail(uD.getEmail());
        u.setMobile(uD.getMobile());
        u.setPassword(encPass);

        if(uD.getAddress() != null){
            String city = uD.getAddress().getCity();
            String pincode = uD.getAddress().getPincode();
            String street = uD.getAddress().getStreet();
            String buildingName = uD.getAddress().getBuildingName();

            Address a = addressRepository.findByCityAndPincodeAndStreetAndBuildingName(city,pincode,street, buildingName);

            if(a == null){
                a = new Address(city,pincode, street,buildingName);
                a = addressRepository.save(a);
                u.setAddresses(List.of(a));
            }
        }

        uD = mM.map(u, UserDTO.class);
        uD.setAddress(mM.map(u.getAddresses().stream().findFirst().get(), AddressDTO.class));
        CartDTO cD = mM.map(u.getCart(), CartDTO.class);
        List<ProductDTO> prods = u.getCart().getCi().stream().map(prod->mM.map(prod.getProduct(), ProductDTO.class)).collect(Collectors.toList());
        uD.setCart(cD);
        uD.getCart().setProducts(prods);

        return uD;
    }

    @Override
    public String deleteUser(Long userId) {

        User u = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User","userId",userId));

        List<CartItem> ci = u.getCart().getCi();
        Long cid = u.getCart().getId();

        ci.forEach(prod -> {
            Long pid = prod.getProduct().getProductId();
            // cs.deleteProductFromCart(cid,pid);
        });
        userRepository.delete(u);

        return "User with userId"+userId+"successfully deleted";
    }

 /*   public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }  */
}
