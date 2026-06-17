package com.example.taskmanager.service;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void hashPasswordAndcreateNewUser_ifNotExisting(){
        //Arrange

        AuthRequestDTO userRequestDTO = new AuthRequestDTO("mukundi", "example@gmail.com", "password123");
        User user = new User(userRequestDTO.username(),userRequestDTO.email(),"hashedPassword123");
        when(userRepository.findByEmail("example@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRequestDTO.password())).thenReturn("hashedPassword123");
        user.setCreatedAt(Instant.parse("2026-06-17T08:00:00Z"));
        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        //Act
        AuthResponseDTO savedUser = authService.createUser(userRequestDTO);

        //Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.email()).as("email should be example.com").isEqualTo("example@gmail.com");
        assertThat(savedUser.id()).isEqualTo(1L);
        assertThat(savedUser.createdAt()).isEqualTo(Instant.parse("2026-06-17T08:00:00Z"));
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");

    }

    /*@Test
    void createNewUser_ifNotExisting() {
        //Arrange

        AuthRequestDTO userRequestDTO = new AuthRequestDTO("mukundi", "example@gmail.com", "password123");
        User user = new User(userRequestDTO.username(),userRequestDTO.email(),userRequestDTO.password());
        when(userRepository.findByEmail("example@gmail.com")).thenReturn(Optional.empty());
        user.setCreatedAt(Instant.parse("2026-06-17T08:00:00Z"));
        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        //Act
        AuthResponseDTO savedUser = authService.createUser(userRequestDTO);
        //Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.email()).as("email should be example.com").isEqualTo("example@gmail.com");
        assertThat(savedUser.id()).isEqualTo(1L);
        assertThat(savedUser.createdAt()).isEqualTo(Instant.parse("2026-06-17T08:00:00Z"));
        verify(userRepository).save(any(User.class));

    }*/


    @Test
    void findUserById_shouldReturnMatchingUser(){
        //Arrange
        Long id =1L;
        User user=new User("mukundi","example@gmail.com","password123");
        user.setCreatedAt(Instant.parse("2026-06-17T08:00:00Z"));
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //Act
        AuthResponseDTO authResponseDTO=authService.findUserById(id);

        //Assert
        assertThat(authResponseDTO.id()).isEqualTo(1L);
        assertThat(authResponseDTO.email()).isEqualTo("example@gmail.com");
        assertThat(authResponseDTO.createdAt()).isEqualTo(Instant.parse("2026-06-17T08:00:00Z"));


        verify(userRepository).findById(id);

    }
    @Test
    void updateAUser_shouldUpdateAnExistingUserAndReturnANewUser(){
        //Arrange
        User user=new User("mukundi","example@gmail.com","passwprd123");
        user.setId(1L);
        User newUser=new User("marisa","example@gmail.com","password235");
        newUser.setId(1L);
        newUser.setCreatedAt(Instant.parse("2026-06-17T08:00:00Z"));
        AuthRequestDTO authRequestDTO=new AuthRequestDTO(newUser.getUsername(),newUser.getEmail(),newUser.getPassword());
        when(passwordEncoder.encode(authRequestDTO.password())).thenReturn("hashedPassword123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(newUser);


        //Act
        AuthResponseDTO updatedUser=authService.updateUser(1L,authRequestDTO);

        //Assert
        assertThat(updatedUser.email()).isEqualTo("example@gmail.com");
        assertThat(updatedUser.createdAt()).isEqualTo(Instant.parse("2026-06-17T08:00:00Z"));
        assertThat(updatedUser.username()).isEqualTo("marisa");

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(authRequestDTO.password());
    }

    @Test
    void findAllUsers_shouldReturnAllSavedUsers(){
        User user=new User("mukundi","example@gmail.com","passwprd123");
        user.setId(1L);
        user.setCreatedAt(Instant.parse("2026-06-17T08:00:00Z"));
        User user1=new User("marisa","marisa@gmail.com","password235");
        user1.setId(2L);
        user1.setCreatedAt(Instant.parse("2026-06-18T08:00:00Z"));

        List<User> users=List.of(user,user1);

        when(userRepository.findAll()).thenReturn(users);

        //Act
        List<AuthResponseDTO> userList=authService.findAllUsers();

        //Assert
        assertThat(userList.get(0).id()).isEqualTo(1L);
        assertThat(userList.get(1).id()).isEqualTo(2L);
        assertThat(userList.get(0).email()).isEqualTo("example@gmail.com");
        assertThat(userList.get(1).email()).isEqualTo("marisa@gmail.com");

        assertThat(userList.get(0).createdAt()).isEqualTo(Instant.parse("2026-06-17T08:00:00Z"));
        assertThat(userList.get(1).createdAt()).isEqualTo(Instant.parse("2026-06-18T08:00:00Z"));

        verify(userRepository).findAll();
    }

    @Test
    void deleteUser_IfUserExists(){
        //Assert
        Long id=1L;
        User user=new User("mukundi","example@gmail.com","passwprd123");
        user.setId(id);
        user.setCreatedAt(Instant.parse("2026-06-17T08:00:00Z"));
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //Act
        authService.deleteUserById(id);

        //Assert
        verify(userRepository).findById(id);
        verify(userRepository).delete(user);


    }


}
