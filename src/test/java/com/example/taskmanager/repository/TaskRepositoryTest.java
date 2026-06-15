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
        entityManager.clear();
        // 2. ACT
        // Trigger our custom repository query method
        List<Task> result = taskRepository.findByStatus(Status.ACTIVE);

        // 3. ASSERT
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getTitle).containsExactlyInAnyOrder("Task 1", "Task 2");
    }
    @Test
    void findByDueDateBetween_shouldReturnTaskWithMatchingDueDate(){

        //Arrange
        Instant now = Instant.parse("2026-06-12T00:00:00Z");
        Instant later= Instant.parse("2026-08-12T00:00:00Z");
        Instant later2 =Instant.parse("2026-10-12T00:00:00Z");

        Task task1= new Task("Task 1", "Active description", Status.ACTIVE, now);
        Task task2 = new Task("Task 2", "Another active one", Status.ACTIVE, later);
        Task task3 = new Task("Task 3", "Done description", Status.DONE, later2);
        List<Task> tasks=List.of(task1,task2,task3);
        tasks.forEach(task -> entityManager.persist(task));
        entityManager.flush();
        entityManager.clear();

        //Act
        List<Task> tasksList=taskRepository.findByDueDateBetween(now,later);

        //Assert
        List<Task> result=List.of(task1,task2);
        assertThat(tasksList).hasSize(2);

    }
    @Test
    void findsTasksByStatusOrderedByDueDateAscending_shouldReturnMatchingTasks(){
        //Arrange
        Instant now = Instant.parse("2026-06-12T00:00:00Z");
        Instant later= Instant.parse("2026-08-12T00:00:00Z");
        Instant later2 =Instant.parse("2026-10-12T00:00:00Z");

        Task task1= new Task("Task 1", "Active description", Status.ACTIVE, now);
        Task task2 = new Task("Task 2", "Another active one", Status.ACTIVE, later);
        Task task3= new Task("Task 1", "Active description", Status.ACTIVE, now);
        Task task4 = new Task("Task 2", "Another active one", Status.ACTIVE, later);
        Task task5 = new Task("Task 3", "Done description", Status.DONE, later2);
        List<Task> tasks=List.of(task1,task2,task3,task4,task5);
        tasks.forEach(task -> entityManager.persist(task));
        entityManager.flush();
        entityManager.clear();
        //Act
        List<Task> taskList=taskRepository.findByStatusOrderByDueDateAsc(Status.ACTIVE);

        //Assert
        List<Task> result=List.of(task1,task2,task3,task4);
        assertThat(taskList).as("Should return only active tasks").hasSize(4);

    }

    @Test
 void  findsTasksByTitleContainingIgnoreCase(){
     //Arrange
     Instant now = Instant.parse("2026-06-12T00:00:00Z");
     Instant later= Instant.parse("2026-08-12T00:00:00Z");
     Instant later2 =Instant.parse("2026-10-12T00:00:00Z");

     Task task1= new Task("Task 1", "Active description", Status.ACTIVE, now);
     Task task2 = new Task("Task 2", "Another active one", Status.ACTIVE, later);
     Task task3= new Task("You", "Active description", Status.ACTIVE, now);
     Task task4 = new Task("Task 2", "Another active one", Status.ACTIVE, later);
     Task task5 = new Task("Me", "Done description", Status.DONE, later2);
     List<Task> tasks=List.of(task1,task2,task3,task4,task5);
     tasks.forEach(task -> entityManager.persist(task));
     entityManager.flush();
     entityManager.clear();
     //Act
     List<Task> taskList=taskRepository.findByTitleContainingIgnoreCase("aSK");

     //Assert

     assertThat(taskList).as("Should return tasks containig 'ask' in their title").hasSize(3);
     assertThat(taskList)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Task 1", "Task 2", "Task 2");

 }
}