//package com.projectX.backend.Controller;
//
//import com.projectX.backend.Payloads.UserDTO;
//import com.projectX.backend.Service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Collections;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api")
//public class AuthController {
//    @Autowired private UserService us;
//    @Autowired private PasswordEncoder pE;
//    @Autowired
//    private AuthenticationManager am;
//
//    @Autowired private JwtUtil jwtUtil;
//
//    public ResponseEntity<Map<String,Object>> registerHandler(@RequestBody UserDTO userDTO) throws UsernameNotFoundException {
//        String encodedPass = pE.encode(userDTO.getPassword());
//        userDTO.setPassword(encodedPass);
//        UserDTO uD = us.registerUser(userDTO);
//        String token = jwtUtil.generateToken(userDTO.getEmail());
//        return new ResponseEntity<Map<String,Object>>(Collections.singletonMap("jwt-token",token), HttpStatus.CREATED); */
//    }
//}
