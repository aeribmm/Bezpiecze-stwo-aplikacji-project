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

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public List<Todo> getTodosByUser(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    public Optional<Todo> getTodoById(Long todoId, Long userId) {
        return todoRepository.findByIdAndUserId(todoId, userId);
    }

    public Todo createTodo(TodoRequest request, User user) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        todo.setUser(user);

        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long todoId, TodoRequest request, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);

        return todoRepository.save(todo);
    }

    public Todo patchTodo(Long todoId, Map<String, Object> updates, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

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
                    break;
            }
        });

        return todoRepository.save(todo);
    }
    public void deleteTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

        todoRepository.delete(todo);
    }
    public List<Todo> getTodosByStatus(Long userId, Boolean completed) {
        return todoRepository.findByUserIdAndCompleted(userId, completed);
    }
    public long countUserTodos(Long userId) {
        return todoRepository.countByUserId(userId);
    }
}