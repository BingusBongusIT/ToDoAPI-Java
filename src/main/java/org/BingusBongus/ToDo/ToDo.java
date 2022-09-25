package org.BingusBongus.ToDo;

import java.time.LocalDateTime;
import java.util.UUID;

public class ToDo
{
    public ToDo(String taskTitle){this(taskTitle, null);}
    public ToDo(String taskTitle, String taskDescription)
    {
        this.id = String.format("%x", UUID.randomUUID().toString());
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        //this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.isComplete = false;
    }

    private String id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    //private String taskTitle;
    private String taskDescription;
    private boolean isComplete;

    public String getId()
    {
        return id;
    }

    public LocalDateTime getCreatedDate()
    {
        return createdDate;
    }

    public LocalDateTime getModifiedDate()
    {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

//    public String getTaskTitle()
//    {
//        return taskTitle;
//    }
//
//    public void setTaskTitle(String taskTitle)
//    {
//        this.taskTitle = taskTitle;
//    }

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