package org.BingusBongus.TableEntities;

/**
 * Entity with which the todo objects are stored in the database
 * Extends the BaseTableEntity
 * @see org.BingusBongus.TableEntities.BaseTableEntity
 *
 * @author colllijo
 * @version 1.0.0
 */
public class ToDoTableEntity extends BaseTableEntity
{
    private String taskDescription;
    private boolean isComplete;
    private String modifiedDate;
    private String createdDate;

    public ToDoTableEntity(String partitionKey, String rowKey, String taskDescription, boolean isComplete, String modifiedDate, String createdDate)
    {
        super(partitionKey, rowKey);
        this.taskDescription = taskDescription;
        this.isComplete = isComplete;
        this.modifiedDate = modifiedDate;
        this.createdDate = createdDate;
    }

    public String getTaskDescription(){ return taskDescription; }
    public void setTaskDescription(String taskDescription){ this.taskDescription = taskDescription; }
    public boolean isComplete(){ return isComplete; }
    public void setComplete(boolean complete){ isComplete = complete; }
    public String getModifiedDate(){ return modifiedDate; }
    public void setModifiedDate(String modifiedDate){ this.modifiedDate = modifiedDate; }
    public String getCreatedDate(){ return createdDate; }
    public void setCreatedDate(String createdDate){ this.createdDate = createdDate; }
}
