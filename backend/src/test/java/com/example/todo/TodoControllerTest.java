package com.example.todo;

import com.example.todo.model.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.example.todo.repository.TodoRepository repository;

    @BeforeEach
    void clearStore() {
        repository.deleteAll();
    }

    // ── GET /todos ──────────────────────────────────────────────────────────────

    @Test
    void getAllTodos_returnsEmptyList_whenNoTodos() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllTodos_returnsAllCreatedTodos() throws Exception {
        create("Buy groceries", false);
        create("Read a book", true);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Buy groceries", "Read a book")));
    }

    // ── GET /todos/{id} ─────────────────────────────────────────────────────────

    @Test
    void getTodoById_returnsCorrectTodo() throws Exception {
        long id = extractId(create("Learn Spring", false));

        mockMvc.perform(get("/todos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Learn Spring"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getTodoById_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/todos/9999"))
                .andExpect(status().isNotFound());
    }

    // ── POST /todos ─────────────────────────────────────────────────────────────

    @Test
    void createTodo_returns201WithLocation() throws Exception {
        MvcResult result = create("Write tests", false);

        long id = extractId(result);

        mockMvc.perform(get("/todos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Write tests"));
    }

    @Test
    void createTodo_assignsUniqueIds() throws Exception {
        long id1 = extractId(create("Task 1", false));
        long id2 = extractId(create("Task 2", false));

        assert id1 != id2;
    }

    // ── PUT /todos/{id} ─────────────────────────────────────────────────────────

    @Test
    void updateTodo_updatesFieldsCorrectly() throws Exception {
        long id = extractId(create("Old title", false));

        Todo updated = new Todo();
        updated.setTitle("New title");
        updated.setCompleted(true);

        mockMvc.perform(put("/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void updateTodo_returns404_whenNotFound() throws Exception {
        Todo updated = new Todo();
        updated.setTitle("Ghost");
        updated.setCompleted(false);

        mockMvc.perform(put("/todos/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /todos/{id} ──────────────────────────────────────────────────────

    @Test
    void deleteTodo_returns204_andRemovesItem() throws Exception {
        long id = extractId(create("Delete me", false));

        mockMvc.perform(delete("/todos/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todos/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTodo_returns404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/todos/9999"))
                .andExpect(status().isNotFound());
    }

    // ── helpers ─────────────────────────────────────────────────────────────────

    private MvcResult create(String title, boolean completed) throws Exception {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setCompleted(completed);

        return mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private long extractId(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }
}
