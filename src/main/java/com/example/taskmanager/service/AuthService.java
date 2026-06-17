package com.example.taskmanager.service;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.entity.User;
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
    public AuthResponseDTO createUser(AuthRequestDTO authRequestDTO){
        userRepository.findByEmail(authRequestDTO.email())
                .ifPresent(existingUser -> {
                    throw new RuntimeException("User already exists");
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
        User savedUser=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()

        );

    }
    public AuthResponseDTO updateUser(Long id,AuthRequestDTO authRequestDTO){
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
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
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        userRepository.delete(user);
    }

}
