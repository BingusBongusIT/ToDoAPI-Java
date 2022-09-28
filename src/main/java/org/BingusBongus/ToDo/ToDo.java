package org.BingusBongus.ToDo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ToDo
{
    private String id;
    private String createdDate;
    private String modifiedDate;
    private String taskDescription;
    private boolean isComplete;

    public ToDo(String taskDescription)
    {
        this.id = UUID.randomUUID().toString();
        this.createdDate = LocalDateTime.now().toString();
        this.modifiedDate = LocalDateTime.now().toString();
        this.taskDescription = taskDescription;
        this.isComplete = false;
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

class ToDoCreateModel
{
    private String taskDescription;

    public String getTaskDescription()
    {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription)
    {
        this.taskDescription = taskDescription;
    }
}

class ToDoUpdateModel
{
    private String taskDescription;
    private boolean isComplete;

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