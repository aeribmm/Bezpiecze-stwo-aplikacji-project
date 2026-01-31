package com.s58703.demo.service;

import com.s58703.demo.dto.TodoRequest;
import com.s58703.demo.entities.Todo;
import com.s58703.demo.entities.User;
import com.s58703.demo.exception.ResourceNotFoundException;
import com.s58703.demo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Enhanced Todo Service
 * Implements business logic for CRUD + PATCH operations
 *
 * Features:
 * - Full CRUD operations
 * - Partial updates (PATCH)
 * - Access control validation
 * - Transactional operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * Get all todos for a user
     */
    public List<Todo> getTodosByUser(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    /**
     * Get single todo by ID (with ownership check)
     */
    public Optional<Todo> getTodoById(Long todoId, Long userId) {
        return todoRepository.findByIdAndUserId(todoId, userId);
    }

    /**
     * Create new todo
     */
    public Todo createTodo(TodoRequest request, User user) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        todo.setUser(user);

        return todoRepository.save(todo);
    }

    /**
     * Full update (PUT) - replaces entire resource
     * Idempotent: multiple identical requests produce same result
     */
    public Todo updateTodo(Long todoId, TodoRequest request, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

        // Replace all fields
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);

        return todoRepository.save(todo);
    }

    /**
     * Partial update (PATCH) - updates only specified fields
     *
     * Supports updating:
     * - title
     * - description
     * - completed
     *
     * Example usage:
     * {"completed": true} - only updates completion status
     * {"title": "New Title", "completed": true} - updates title and completion
     */
    public Todo patchTodo(Long todoId, Map<String, Object> updates, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

        // Apply partial updates
        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    if (value instanceof String) {
                        todo.setTitle((String) value);
                    }
                    break;

                case "description":
                    if (value instanceof String) {
                        todo.setDescription((String) value);
                    }
                    break;

                case "completed":
                    if (value instanceof Boolean) {
                        todo.setCompleted((Boolean) value);
                    }
                    break;

                default:
                    // Ignore unknown fields
                    break;
            }
        });

        return todoRepository.save(todo);
    }

    /**
     * Delete todo (idempotent)
     * Deleting non-existent resource still returns success
     */
    public void deleteTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

        todoRepository.delete(todo);
    }

    /**
     * Get todos by completion status
     */
    public List<Todo> getTodosByStatus(Long userId, Boolean completed) {
        return todoRepository.findByUserIdAndCompleted(userId, completed);
    }

    /**
     * Count total todos for user
     */
    public long countUserTodos(Long userId) {
        return todoRepository.countByUserId(userId);
    }
}