package com.example.taskmanager.service;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ResourceConflictException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthResponseDTO createUser(AuthRequestDTO authRequestDTO){
        userRepository.findByEmail(authRequestDTO.email())
                .ifPresent(existingUser -> {
                    throw new ResourceConflictException("User with email " + authRequestDTO.email() + " already exists");
                });
      User user =new User(authRequestDTO.username(),authRequestDTO.email(),passwordEncoder.encode(authRequestDTO.password()));

      User savedUser=userRepository.save(user);
      return new AuthResponseDTO(
              savedUser.getId(),
              savedUser.getUsername(),
              savedUser.getEmail(),
              savedUser.getCreatedAt()

      );

    }
    public AuthResponseDTO findUserById(Long id){
        User savedUser=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with ID " + id + " not found"));
        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()

        );

    }
    public AuthResponseDTO updateUser(Long id,AuthRequestDTO authRequestDTO){
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with ID " + id + " not found"));
         user.setUsername(authRequestDTO.username());
         user.setEmail(authRequestDTO.email());
         user.setPassword(passwordEncoder.encode(authRequestDTO.password()));
        User savedUser=userRepository.save(user);
        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    public List<AuthResponseDTO> findAllUsers(){
        List<User> userList=userRepository.findAll();
        List<AuthResponseDTO> authResponseDTOList =new ArrayList<>();
        userList.forEach(user -> {
            authResponseDTOList.add(
                    new AuthResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()));

        });
        return authResponseDTOList;

    }
    public void  deleteUserById(Long id){
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with ID " + id + " not found"));
        userRepository.delete(user);
    }

    public String loginUser(String email,String password){
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user email: "+email+" not found"));
         if(!passwordEncoder.matches(password,user.getPassword())){
             throw new IllegalArgumentException("Invalid email or password credentials");
         }
         String token = jwtService.issueToken(email);
         return token;
    }
}
