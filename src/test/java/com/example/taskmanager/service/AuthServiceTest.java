package com.example.taskmanager.service;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.dto.LoginRequestDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
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
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private JwtService jwtService;


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

    @Test
    void login_shouldReturnValidJWT(){
        //Act
        String email="example@gmail.com";
        String password="password123";
        LoginRequestDTO loginRequestDTO=new LoginRequestDTO(email,password);
        User user=new User("mukundi",email,"hashedPassword");
        user.setId(1L);
        user.setCreatedAt(Instant.parse("2026-06-23T08:00:00Z"));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.issueToken(email)).thenReturn("token issued");
        when(passwordEncoder.matches(password,user.getPassword())).thenReturn(true);


        //Act
        String token=authService.loginUser(email,password);

        //Assert
        assertThat(token).isNotBlank();

        verify(userRepository).findByEmail(anyString());
        verify(jwtService).issueToken(anyString());
        verify(passwordEncoder).matches(anyString(),anyString());

    }

}


/*

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@MockitoBean
private TaskService taskService;

@Test
void creatTask_shouldReturn201AndTaskResponseDTO() throws Exception {
// Arrange
Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
TaskRequestDTO requestDTO = new TaskRequestDTO("cook", "cook a hot meal", Status.ACTIVE, timeMarker);
TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

when(taskService.createTask(requestDTO)).thenReturn(responseDTO);

// Act and Assert
mockMvc.perform(post("/api/tasks")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(requestDTO)))
.andExpect(status().isCreated())
// 💡 UPDATED: Verifying global envelope metadata properties
.andExpect(jsonPath("$.status").value("SUCCESS"))
.andExpect(jsonPath("$.message").value("Task created successfully"))
.andExpect(jsonPath("$.timestamp").exists())
// 💡 UPDATED: Drilled deep into the nested $.data slot to find payload details
.andExpect(jsonPath("$.data.id").value(1L))
.andExpect(jsonPath("$.data.title").value("cook"))
.andExpect(jsonPath("$.data.description").value("cook a hot meal"));

verify(taskService).createTask(any(TaskRequestDTO.class));
}

@Test
void findTaskById_shouldReturn200AndTaskResponseDTO() throws Exception {
// Arrange
Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
Long id = 1L;
TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

when(taskService.findTaskById(id)).thenReturn(responseDTO);

// Act and Assert
mockMvc.perform(get("/api/tasks/{id}", id))
.andExpect(status().isOk())
// 💡 UPDATED: Verifying envelope shell properties
.andExpect(jsonPath("$.status").value("SUCCESS"))
.andExpect(jsonPath("$.message").value("Task retrieved successfully"))
// 💡 UPDATED: Navigating inside the single object generic type container
.andExpect(jsonPath("$.data.id").value(id))
.andExpect(jsonPath("$.data.title").value("cook"))
.andExpect(jsonPath("$.data.description").value("cook a hot meal"));

verify(taskService).findTaskById(id);
}

@Test
void getAllTasks_shouldReturn200AllTasksorEmptyList() throws Exception {
// Arrange
Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

when(taskService.findAllTasks()).thenReturn(List.of(
responseDTO, responseDTO, responseDTO
));

// Act and Assert
mockMvc.perform(get("/api/tasks"))
.andExpect(status().isOk())
// 💡 UPDATED: Enforcing the corrected string message contract
.andExpect(jsonPath("$.status").value("SUCCESS"))
.andExpect(jsonPath("$.message").value("All tasks retrieved successfully"))
// 💡 UPDATED: Navigating into the data payload which is now an array nested under $.data
.andExpect(jsonPath("$.data.length()").value(3))
.andExpect(jsonPath("$.data[0].title").value("cook"))
.andExpect(jsonPath("$.data[2].description").value("cook a hot meal"));

verify(taskService).findAllTasks();
}

@Test
void deleteTaskById_shouldReturn200() throws Exception {
// Arrange
Long id = 1L;

// Act and Assert
mockMvc.perform(delete("/api/tasks/{id}", id))
.andExpect(status().isOk()) // 💡 UPDATED: Enforcing 200 OK block since we return an ApiResponse
.andExpect(jsonPath("$.status").value("SUCCESS"))
.andExpect(jsonPath("$.message").value("Task deleted successfully"))
.andExpect(jsonPath("$.data").isEmpty()); // 💡 Asserting that the Void payload container holds no internal elements

verify(taskService).deleteTaskById(id);
}

@Test
void updateTask_shouldUpdateTaskandReturn200andNewTask() throws Exception {
// Arrange
Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
TaskRequestDTO requestDTO = new TaskRequestDTO("cook", "cook a hot meal", Status.ACTIVE, timeMarker);
TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);
Long id = 1L;

when(taskService.updateTask(id, requestDTO)).thenReturn(responseDTO);

// Act and Assert
mockMvc.perform(put("/api/tasks/{id}", id)
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(requestDTO)))
.andExpect(status().isOk())
// 💡 UPDATED: Asserting the payload envelope structure for our Update transaction mapping
.andExpect(jsonPath("$.status").value("SUCCESS"))
.andExpect(jsonPath("$.message").value("Task updated successfully"))
.andExpect(jsonPath("$.data.id").value(1L))
.andExpect(jsonPath("$.data.title").value("cook"))
.andExpect(jsonPath("$.data.description").value("cook a hot meal"));

verify(taskService).updateTask(id, requestDTO);
}
*/