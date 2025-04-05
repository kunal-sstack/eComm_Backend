package com.projectX.backend.Controller;

import com.projectX.backend.Configuration.AppConstants;
import com.projectX.backend.Payloads.UserDTO;
import com.projectX.backend.Payloads.UserResponse;
import com.projectX.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService us;

    @GetMapping("/admin/users")
    public ResponseEntity<UserResponse> getUsers(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.P_N,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.P_S, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.S_BY_UID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder ) {

        UserResponse ur = us.getAllUsers(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<UserResponse>(ur, HttpStatus.FOUND);
    }

    @GetMapping("/public/user/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId){
        UserDTO ud = us.getUserById(userId);
        return new ResponseEntity<UserDTO>(ud, HttpStatus.FOUND);
    }

    @PutMapping("/public/user/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO){
        UserDTO uD = us.updateUser(userId,userDTO);
        return new ResponseEntity<UserDTO>(uD,HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        String s = us.deleteUser(userId);
        return new ResponseEntity<String>(s, HttpStatus.OK);
    }


}
