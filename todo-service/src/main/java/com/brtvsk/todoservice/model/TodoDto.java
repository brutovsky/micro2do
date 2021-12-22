package com.brtvsk.todoservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class TodoDto {
    private String id;
    private String title;
    private String description;
    private boolean isDone;
    private Set<String> tags;
    private Date creationTime;
    private Date completionTime;
}
