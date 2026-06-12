package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // 💡 Boots an isolated in-memory database and wires up repository beans
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager; // 💡 A safe helper to insert raw entities directly into SQL

    @Test
    void findByStatus_shouldReturnTasksWithMatchingStatus() {
        // 1. ARRANGE
        Instant now = Instant.parse("2026-06-12T00:00:00Z");

        Task activeTask1 = new Task("Task 1", "Active description", Status.ACTIVE, now);
        Task activeTask2 = new Task("Task 2", "Another active one", Status.ACTIVE, now);
        Task completedTask = new Task("Task 3", "Done description", Status.DONE, now);

        // Persist our baseline data directly into the running in-memory database engine
        entityManager.persist(activeTask1);
        entityManager.persist(activeTask2);
        entityManager.persist(completedTask);
        entityManager.flush(); // Forces Spring to synchronize memory states down to the SQL tables

        // 2. ACT
        // Trigger our custom repository query method
        List<Task> result = taskRepository.findByStatus(Status.ACTIVE);

        // 3. ASSERT
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getTitle).containsExactlyInAnyOrder("Task 1", "Task 2");
    }
}