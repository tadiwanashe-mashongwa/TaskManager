package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import org.springframework.data.domain.Page;     // 💡 FIX: Use Spring's Page import
import org.springframework.data.domain.Pageable; // 💡 FIX: Import Pageable for input properties
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
   List<Task> findByStatus(Status status);

   List<Task> findByDueDateBetween(Instant firstDate, Instant secondDate);

   List<Task> findByStatusOrderByDueDateAsc(Status status);

   List<Task> findByTitleContainingIgnoreCase(String title);


   Page<Task> findByStatus(Status status, Pageable pageable);
   Page<Task> findByStatusOrderByDueDateAsc(Status status,Pageable pageable);
}