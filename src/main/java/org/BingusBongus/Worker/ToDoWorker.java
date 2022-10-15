package org.BingusBongus.Worker;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueOutput;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import org.BingusBongus.ToDo.EntityMapper;
import org.BingusBongus.ToDo.ToDo;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ToDoWorker
{
    //Constant Variables
    private final String TABLENAME = "todos";
    private final String PARTITION_KEY = "TODO";
    final public Gson gson = new Gson();

    //Connect to the Database
    private final TableClient tableClient = new TableClientBuilder()
            .connectionString("DefaultEndpointsProtocol=https;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;")
            .tableName(TABLENAME)
            .buildClient();

    /**
     * Function to crate a new todo.
     * Triggers with an HttpTrigger on api/todo and creates a new ToDo from the request-body
     * which is then added to the Database after being converted
     * @see org.BingusBongus.ToDo.ToDo
     * @see com.azure.data.tables.models.TableEntity
     *
     * To create a ToDo from the request google's Gson library is used to create a object from the json
     * This object is then mapped to a tableentity which gets outputed to the Table
     * @see com.google.gson.Gson#fromJson(Reader, Type)
     * @see EntityMapper#ToDoToTableEntity(ToDo)
     *
     * @param message - request body containing the todo to create in json format
     * @return - returns a success code if the todo was successfully be added to the list
     */
    @FunctionName("createToDoQueue")
    public void createToDo(
            @QueueTrigger(name = "createToDo", queueName = "todo-queue", connection = "AzureWebJobsStorage") String message,
            final ExecutionContext context
            )
    {
        try
        {
            context.getLogger().info("Worker is createing the Todo with the id: " + gson.fromJson(message, ToDo.class).getId());
            //Create a ToDo from the query using gson, map it to a TableEntity and insert it into the table
            tableClient.upsertEntity(EntityMapper.ToDoToTableEntity(gson.fromJson(message, ToDo.class)));
        }
        catch(Exception exception)
        {
            context.getLogger().warning("Something went wrong while creating a todo\nRequest body: " + message + "\nException: " + Arrays.toString(exception.getStackTrace()));
        }


    }
}
