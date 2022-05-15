package com.brtvsk.todoservice.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;

@Document("todos")
public class Todo {

    @Id
    private UUID id;
    private UUID ownerId;
    private String title;
    private String description;
    private Boolean done;
    private Set<String> tags = Collections.emptySet();
    private Date creationTime;
    private Date completionTime;
    private List<Attachment> attachments = Collections.emptyList();

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final UUID ownerId) {
        this.ownerId = ownerId;
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
        return Set.copyOf(tags);
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

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Todo todo = (Todo) o;
        return id.equals(todo.id) && ownerId.equals(todo.ownerId) && title.equals(todo.title) && description.equals(todo.description) && done.equals(todo.done) && tags.equals(todo.tags) && creationTime.equals(todo.creationTime) && completionTime.equals(todo.completionTime) && attachments.equals(todo.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, title, description, done, tags, creationTime, completionTime, attachments);
    }

    @Override
    public String toString() {
        return "Todo{"
                + "id=" + id
                + ", ownerId=" + ownerId
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", done=" + done
                + ", tags=" + tags
                + ", creationTime=" + creationTime
                + ", completionTime=" + completionTime
                + ", attachments=" + attachments
                + '}';
    }
}
