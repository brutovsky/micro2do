package com.brtvsk.todoservice.migration;

import com.brtvsk.todoservice.model.entity.Todo;
import com.brtvsk.todoservice.repository.TodoRepository;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ChangeLog
public class MongoChangelog {

    @ChangeSet(order = "001", id = "testChange", author = "brtvsk")
    public void testChangeLog(TodoRepository todoRepository) {
        List<Todo> todoList = List.of(
                createNewTodo("Title 1", "Description 1", Set.of("Tag 1", "Tag 2")),
                createNewTodo("Title 2", "Description 2", Set.of("Tag 2", "Tag 1")),
                createNewTodo("Title 3", "Description 3", Set.of("Tag 3", "Tag 4")),
                createNewTodo("Title 4", "Description 4", Set.of("Tag 4", "Tag 5"))
        );
        todoList.forEach(todoRepository::save);
    }

    private Todo createNewTodo(String title, String description, Set<String> tags) {
        Todo todo = new Todo();
        todo.setId(UUID.randomUUID());
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setDone(Boolean.FALSE);
        todo.setTags(tags);
        todo.setCreationTime(Date.from(Instant.now()));
        return todo;
    }

}
