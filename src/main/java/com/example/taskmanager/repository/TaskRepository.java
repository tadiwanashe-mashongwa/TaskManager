package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import org.springframework.data.domain.Page;     // 💡 FIX: Use Spring's Page import
import org.springframework.data.domain.Pageable; // 💡 FIX: Import Pageable for input properties
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
   //Reading data
   List<Task> findByStatus(Status status);
   List<Task> findByDueDateBetween(Instant firstDate, Instant secondDate);
   List<Task> findByStatusOrderByDueDateAsc(Status status);
   List<Task> findByTitleContainingIgnoreCase(String title);

   //Reading data as pages
   Page<Task> findByStatus(Status status, Pageable pageable);
   Page<Task> findByStatusOrderByDueDateAsc(Status status,Pageable pageable);

   //Custom JPQL search
   @Query("select t from Task t where t.status=:status")
   List<Task> fetchTasksByStatusCustom(@Param("status")Status status);

   @Query(value="select t from Task t where lower(t.title) like lower(:title)",
           countQuery="select count(t) from Task t  where lower(t.title) like lower(:title)")
   Page<Task> fetchTasksByTitleContaining(@Param("title") String title,Pageable pageable);


   //Custom native query
   @Query(value = "select * from task t where lower(t.title) like lower(:title)",
   countQuery = "select count(*) from task t where lower(t.title) like lower(:title)",
   nativeQuery = true)
   Page<Task> fetchTasksByNativeQueryTitleContaining(@Param("title") String title,Pageable pageable);
}
