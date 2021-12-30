package com.brtvsk.todoservice.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;


@Document("todos")
public class Todo {

    @Id
    private UUID id;
    private String title;
    private String description;
    private Boolean done;
    private Set<String> tags;
    private Date creationTime;
    private Date completionTime;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean isDone() {
        return done;
    }

    public void setDone(final Boolean done) {
        this.done = done;
    }

    public Set<String> getTags() {
        if (tags == null) {
            return Set.of();
        } else {
            return Set.copyOf(tags);
        }
    }

    public void setTags(final Set<String> tags) {
        this.tags = tags;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(final Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(final Date completionTime) {
        this.completionTime = completionTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Todo todo = (Todo) o;
        return id.equals(todo.id) && Objects.equals(title, todo.title) && Objects.equals(description, todo.description) && Objects.equals(done, todo.done) && Objects.equals(tags, todo.tags) && Objects.equals(creationTime, todo.creationTime) && Objects.equals(completionTime, todo.completionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, done, tags, creationTime, completionTime);
    }

    @Override
    public String toString() {
        return "Todo{"
                + "id='" + id + '\''
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", isDone=" + done
                + ", tags=" + tags
                + ", creationTime=" + creationTime
                + ", completionTime=" + completionTime
                + '}';
    }
}
