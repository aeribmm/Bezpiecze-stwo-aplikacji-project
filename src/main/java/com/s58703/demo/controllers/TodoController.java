package com.s58703.demo.controllers;

import com.s58703.demo.entities.Todo;
import com.s58703.demo.entities.User;
import com.s58703.demo.service.TodoService;
import com.s58703.demo.dto.TodoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // GET все TODO пользователя
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(@AuthenticationPrincipal User user) {
        List<Todo> todos = todoService.getTodosByUser(user.getId());
        return ResponseEntity.ok(todos);
    }

    // GET одно TODO
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return todoService.getTodoById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST создать TODO
    @PostMapping
    public ResponseEntity<Todo> createTodo(
            @Valid @RequestBody TodoRequest request,
            @AuthenticationPrincipal User user) {

        Todo created = todoService.createTodo(request, user);
        return ResponseEntity.status(201).body(created);
    }

    // PUT обновить TODO
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request,
            @AuthenticationPrincipal User user) {

        Todo updated = todoService.updateTodo(id, request, user.getId());
        return ResponseEntity.ok(updated);
    }

    // DELETE удалить TODO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        todoService.deleteTodo(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}