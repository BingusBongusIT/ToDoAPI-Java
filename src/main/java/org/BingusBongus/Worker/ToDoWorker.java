package org.BingusBongus.Worker;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableEntityUpdateMode;
import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import org.BingusBongus.ToDo.EntityMapper;
import org.BingusBongus.ToDo.ToDo;

public class ToDoWorker
{
    //Constant Variables
    private  static final String TABLE_NAME = "todos";
    private final String PARTITION_KEY = "TODO";
    final public Gson gson = new Gson();

    //Connect to the Database
    public static final TableClient tableClient = new TableClientBuilder()
            .connectionString("DefaultEndpointsProtocol=https;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;")
            .tableName(TABLE_NAME)
            .buildClient();


    @FunctionName("toDoQueue")
    public void doRequest(
        @QueueTrigger(name = "ToDo", queueName = "todo-queue", connection = "AzureWebJobsStorage") String msg,
        ExecutionContext context
    )
    {
        switch (msg.split("\n")[0])
        {
            case "createToDo":
                tableClient.upsertEntity(EntityMapper.ToDoToTableEntity(gson.fromJson(msg.split("\n")[1], ToDo.class)));
                context.getLogger().info("Added a ToDo to the table\n" + msg);
                break;
            case "updateToDo":
                tableClient.updateEntity(EntityMapper.ToDoToTableEntity(gson.fromJson(msg.split("\n")[1], ToDo.class)), TableEntityUpdateMode.REPLACE);
                context.getLogger().info("Updated the ToDo with the id: " + gson.fromJson(msg.split("\n")[1], TableEntity.class).getRowKey() + "\n" + msg);
                break;
            case "deleteToDo":
                tableClient.deleteEntity(PARTITION_KEY, msg.split("\n")[1]);
                context.getLogger().info("Delete the ToDo with the id: " + msg.split("\n")[1] + "\n" + msg);
                break;
        }
    }
}
