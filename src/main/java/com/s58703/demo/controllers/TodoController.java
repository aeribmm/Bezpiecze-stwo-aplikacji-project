package com.s58703.demo.controllers;

import com.s58703.demo.entities.Todo;
import com.s58703.demo.entities.User;
import com.s58703.demo.service.TodoService;
import com.s58703.demo.dto.TodoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    @GetMapping(produces = {"application/json", "application/xml"})
    public ResponseEntity<List<Todo>> getAllTodos(@AuthenticationPrincipal User user) {
        List<Todo> todos = todoService.getTodosByUser(user.getId());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(todos.size()))
                .body(todos);
    }

    @GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<Todo> getTodoById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return todoService.getTodoById(id, user.getId())
                .map(todo -> ResponseEntity.ok()
                        .header("Last-Modified", todo.getUpdatedAt().toString())
                        .body(todo))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Todo> createTodo(
            @Valid @RequestBody TodoRequest request,
            @AuthenticationPrincipal User user) {

        Todo created = todoService.createTodo(request, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/todos/" + created.getId())
                .body(created);
    }

    @PutMapping(value = "/{id}",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Todo> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request,
            @AuthenticationPrincipal User user) {

        Todo updated = todoService.updateTodo(id, request, user.getId());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping(value = "/{id}",
            consumes = {"application/json"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Todo> patchTodo(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal User user) {

        Todo patched = todoService.patchTodo(id, updates, user.getId());
        return ResponseEntity.ok(patched);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        todoService.deleteTodo(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
                .allow(
                        HttpMethod.GET,
                        HttpMethod.POST,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS,
                        HttpMethod.HEAD
                )
                .build();
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> optionsById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .allow(
                        HttpMethod.GET,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS,
                        HttpMethod.HEAD
                )
                .build();
    }

    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> headAllTodos(@AuthenticationPrincipal User user) {
        List<Todo> todos = todoService.getTodosByUser(user.getId());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(todos.size()))
                .header("Content-Type", "application/json")
                .build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Object> headTodoById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return todoService.getTodoById(id, user.getId())
                .map(todo -> ResponseEntity.ok()
                        .header("Last-Modified", todo.getUpdatedAt().toString())
                        .header("Content-Type", "application/json")
                        .build())
                .orElse(ResponseEntity.notFound().build());
    }
}