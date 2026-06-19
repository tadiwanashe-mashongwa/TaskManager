package com.example.taskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//@Builder // 💡 ADD THIS: Generates a clean builder API for flexible object creation
@Table(name = "Task")
public class Task {
    public Task(String title,String description,Status status,Instant dueDate){
        this.title=title;
        this.description=description;
        this.status=status;
        this.dueDate=dueDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @FutureOrPresent(message = "due date should be today or in future")
    private Instant dueDate;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;


    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;




}
