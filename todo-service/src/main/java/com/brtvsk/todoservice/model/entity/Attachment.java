package com.brtvsk.todoservice.model.entity;

import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Attachment {
    @NotNull
    @Indexed(unique = true)
    private String resourceKey;
    @NotNull
    private Long sizeInKb;
    private String description;

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public Long getSizeInKb() {
        return sizeInKb;
    }

    public void setSizeInKb(Long sizeInKb) {
        this.sizeInKb = sizeInKb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Attachment that = (Attachment) o;
        return resourceKey.equals(that.resourceKey) && sizeInKb.equals(that.sizeInKb) && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceKey, sizeInKb, description);
    }

    @Override
    public String toString() {
        return "Attachment{"
                + "resourceKey='" + resourceKey + '\''
                + ", sizeInKb=" + sizeInKb
                + ", description='" + description + '\''
                + '}';
    }
}
