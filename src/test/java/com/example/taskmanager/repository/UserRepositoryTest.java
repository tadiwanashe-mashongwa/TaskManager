package com.example.taskmanager.repository;

import com.example.taskmanager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void findUserByEmail_shouldReturnUserWithMatchingEmail(){
        //Arrange
        Instant now = Instant.parse("2026-06-12T00:00:00Z");
        User user=new User("mukundi","example@gmail.com","123password");
        testEntityManager.persist(user);
        testEntityManager.flush();
        testEntityManager.clear();
        //Act
        User user1 =userRepository.findByEmail(user.getEmail()).orElseThrow(()->new RuntimeException("not found"));

        //Assert
        assertThat(user1.getEmail()).as("should be 'example@gmail.com'").isEqualTo("example@gmail.com");

    }
}
