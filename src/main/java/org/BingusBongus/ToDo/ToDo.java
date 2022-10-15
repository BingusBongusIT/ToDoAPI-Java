package org.BingusBongus.ToDo;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data structure for the ToDos
 *
 * @author colllijo
 * @version 1.0.0
 */
public class ToDo
{
    private final String id;
    private String taskDescription;
    private boolean isComplete;
    private String modifiedDate;
    private final String createdDate;

    public ToDo(String taskDescription)
    {
        this.id = UUID.randomUUID().toString();
        this.taskDescription = taskDescription;
        this.isComplete = false;
        this.modifiedDate = LocalDateTime.now().toString();
        this.createdDate = LocalDateTime.now().toString();
    }

    public ToDo(String id, String taskDescription, boolean isComplete, String modifiedDate, String createdDate)
    {
        this.id = id;
        this.taskDescription = taskDescription;
        this.isComplete = isComplete;
        this.modifiedDate = modifiedDate;
        this.createdDate = createdDate;
    }

    public String getId()
    {
        return id;
    }

    public LocalDateTime getCreatedDate()
    {
        return LocalDateTime.parse(createdDate);
    }

    public LocalDateTime getModifiedDate()
    {
        return LocalDateTime.parse(modifiedDate);
    }

    public void setModifiedDate(LocalDateTime modifiedDate)
    {
        this.modifiedDate = modifiedDate.toString();
    }

    public String getTaskDescription()
    {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription)
    {
        this.taskDescription = taskDescription;
    }

    public boolean isComplete()
    {
        return isComplete;
    }

    public void setComplete(boolean complete)
    {
        isComplete = complete;
    }
}