package com.projectX.backend.Service;

import com.projectX.backend.Entity.User;
import com.projectX.backend.Exceptions.ResourceNotFoundException;
import com.projectX.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
/*
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired private UserRepository ur;

    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException {
        Optional<User> u = ur.findByEmail(username);
        return u.map(userInfoConfig::new).orElseThrow(()->new ResourceNotFoundException("User","email",username));
    }
}  */
