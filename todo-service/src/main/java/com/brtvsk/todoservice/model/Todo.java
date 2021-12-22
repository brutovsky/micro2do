package com.brtvsk.todoservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Document("todos")
@Data
public class Todo {

    @Id
    private String id;
    private String title;
    private String description;
    private boolean isDone;
    private Set<String> tags = new HashSet<>();
    private Date creationTime;
    private Date completionTime;

}
