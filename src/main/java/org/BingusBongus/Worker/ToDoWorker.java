package org.BingusBongus.Worker;

import com.azure.data.tables.models.TableEntity;
import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import org.BingusBongus.Table.Table;
import org.BingusBongus.ToDo.ToDo;

/**
 * Class Containing Worker Functions to complete the tasks on the queue
 *
 * @author colllijo
 * @version 1.0.0
 */
public class ToDoWorker
{
    //Constant Variables
    final public Gson gson = new Gson();

    /**
     * This Function grabs the tasks on the queue and completes them
     * The tasks get split to three categories: create | update | delete
     * createToDo:
     * gets the ToDo body from the msg and the sends it to the table
     *
     * updateToDo:
     * gets a modified ToDo, sets it modifiedDate and updates its table entry
     *
     * deleteToDo:
     * gets a RowKey for the ToDo to delete and removes it from the table
     *
     * @param msg - message which was left on the queue for the worker, contains the type of task and the content of the task
     */
    @FunctionName("toDoQueue")
    public void doRequest(
        @QueueTrigger(name = "ToDo", queueName = "todo-queue", connection = "AzureWebJobsStorage") String msg,
        ExecutionContext context
    )
    {
        switch (msg.split("\n")[0])
        {
            case "createToDo":
                Table.createToDo(gson.fromJson(msg.split("\n")[1], ToDo.class));
                //Table.client.upsertEntity(EntityMapper.ToDoToTableEntity(gson.fromJson(msg.split("\n")[1], ToDo.class)));
                context.getLogger().info("Added a ToDo to the table\n" + msg);
                break;
            case "updateToDo":
                ToDo todo = gson.fromJson(msg.split("\n")[1], ToDo.class);
                //todo.setModifiedDate(LocalDateTime.now());
                Table.updateToDo(todo);
                context.getLogger().info("Updated the ToDo with the id: " + gson.fromJson(msg.split("\n")[1], TableEntity.class).getRowKey() + "\n" + msg);
                break;
            case "deleteToDo":
                Table.deleteToDo(msg.split("\n")[1]);
                context.getLogger().info("Delete the ToDo with the id: " + msg.split("\n")[1] + "\n" + msg);
                break;
        }
    }
}
