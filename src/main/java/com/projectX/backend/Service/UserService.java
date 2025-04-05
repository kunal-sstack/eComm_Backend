package com.projectX.backend.Service;

import com.projectX.backend.Payloads.UserDTO;
import com.projectX.backend.Payloads.UserResponse;

public interface UserService {
  /**/  UserDTO registerUser(UserDTO userDTO);
    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    UserDTO getUserById(Long userId);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    String deleteUser(Long userId);
}
