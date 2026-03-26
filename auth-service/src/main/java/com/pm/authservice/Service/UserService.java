package com.pm.authservice.Service;


import com.pm.authservice.Dto.SignupDto;
import com.pm.authservice.Dto.UserDto;
import com.pm.authservice.entity.User;
import com.pm.authservice.repositories.UserRepositry;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepositry userRepository;

    @Override//already existed
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User with email "+ username +" not found"));
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with id "+ id +" not found"));
    }
    public User getUsrByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public UserDto signUp(SignupDto signupDto){
        Optional<User> user=userRepository.findByEmail(signupDto.getEmail());
        if(user.isPresent()){
            throw new  BadCredentialsException("User with email "+ signupDto.getEmail() +" already exists");
        }
        User tocreate=modelMapper.map(signupDto,User.class);
        tocreate.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        User saveduser=userRepository.save(tocreate);
        return modelMapper.map(saveduser,UserDto.class);
    }

}
