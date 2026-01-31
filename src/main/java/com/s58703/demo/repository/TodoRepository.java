package com.s58703.demo.repository;

import com.s58703.demo.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserId(Long userId);

    Optional<Todo> findByIdAndUserId(Long id, Long userId);

    void deleteByUserId(Long userId);

    long countByUserId(Long userId);

    List<Todo> findByUserIdAndCompleted(Long userId, Boolean completed);
}