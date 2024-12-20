package com.todomanager.to_do_manager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todomanager.to_do_manager_backend.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}