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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    // Получить все TODO пользователя
    public List<Todo> getTodosByUser(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    // Получить одно TODO (с проверкой владельца)
    public Optional<Todo> getTodoById(Long todoId, Long userId) {
        return todoRepository.findByIdAndUserId(todoId, userId);
    }

    // Создать TODO
    public Todo createTodo(TodoRequest request, User user) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        todo.setUser(user);

        return todoRepository.save(todo);
    }

    // Обновить TODO
    public Todo updateTodo(Long todoId, TodoRequest request, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted());

        return todoRepository.save(todo);
    }

    // Удалить TODO
    public void deleteTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

        todoRepository.delete(todo);
    }
}