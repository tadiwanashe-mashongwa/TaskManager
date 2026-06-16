package com.example.taskmanager.service;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthService {
    @InjectMocks
    private  AuthService authService;
    @Mock
    private UserRepository userRepository;

    @Test
    void createNewUser_ifNotExisting(){
  //Arrange
        User user=new User("mukundi","example.com","password123");
        when(userRepository.save(user)).thenReturn((new User("mukundi","example.com","password123"))),
        when(userRepository.findByEmail("example.com")).thenReturn(Optional.of(new User()));


    }


}
