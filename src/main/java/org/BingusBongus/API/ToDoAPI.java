package org.BingusBongus.API;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableEntityUpdateMode;
import com.google.gson.Gson;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.BingusBongus.ToDo.EntityMapper;
import org.BingusBongus.ToDo.ToDo;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * API between the storage, creation and management of the todos and the
 * users interaction with them
 *
 * @author colllijo
 * @version 2.0.0
 */
public class ToDoAPI
{
    //Constant Variables
    private final String ROUTE = "todo";
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
     * @param request - request body containing the todo to create in json format
     * @return - returns a success code if the todo was successfully be added to the list
     */
    @FunctionName("CreateToDo")
    public HttpResponseMessage createToDo
    (
        @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) HttpRequestMessage<String> request,
        @QueueOutput(name = "createToDo", queueName = "todo-queue", connection = "AzureWebJobsStorage") OutputBinding<String> message,
        final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" received");

        //Get query
        final String query = request.getBody();
        if(gson.fromJson(query, ToDo.class).getTaskDescription().trim().isEmpty())
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();

        ToDo todo = new ToDo(gson.fromJson(query, ToDo.class).getTaskDescription());

        message.setValue(gson.toJson(todo));

        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" processed\nNew ToDo has been added to the List");
        return request.createResponseBuilder(HttpStatus.OK).body(todo).build();
    }

    /**
     * Function to return all ToDos which are currently stored
     * Triggers with an HttpTrigger on api/todo and returns the Todolist in the response
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @return - Returns the Todolist as well as a success status
     */
    @FunctionName("GetToDos")
    public HttpResponseMessage GetToDos
    (
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"GetToDos\" received");

        ArrayList<ToDo> toDos = new ArrayList<>();

        tableClient.listEntities(new ListEntitiesOptions().setFilter("PartitionKey eq '" + PARTITION_KEY + "'"), null, null).forEach(tableEntity -> toDos.add(EntityMapper.TableEntityToToDo(tableEntity)));

        return request.createResponseBuilder(HttpStatus.OK).body(toDos.toArray()).build();
    }

    /**
     *
     * Function to return a specific todo by its id
     * Triggers with an HttpTrigger on api/todo/{id} and returns the todo with the corresponding id if found
     * Checks all the ToDo for if the match the given id and if that one exists returns it
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param id - id of the ToDo which shall be returned
     * @return - If Found returns the ToDo an id matching the id parameter and a success code, else returns a bad request
     */
    @FunctionName("GetToDoById")
    public HttpResponseMessage GetToDoById(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE + "/{id}") HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"GetToDoById\" with id:${id} received");

        return request.createResponseBuilder(HttpStatus.OK).body(EntityMapper.TableEntityToToDo(tableClient.getEntity(PARTITION_KEY, id))).build();
    }

    /**
     * Function to update the contents of a specific todo
     * Triggers with an HttpTrigger on api/todo/{id} and updates the Todo
     * matching the id of the request ot the contents of the request body
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param request - body of the request containing the new information of the Todo
     * @param id - id of the ToDo to update
     * @return - if the ToDo with the given id was found an successfully update returns a success code and the updated to else wise returns a bad request
     */
    @FunctionName("UpdateToDo")
    public HttpResponseMessage UpdateToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.PUT, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE + "/{id}") HttpRequestMessage<Optional<String>> request,
            @QueueOutput(name = "createToDo", queueName = "todo-queue", connection = "AzureWebJobsStorage") OutputBinding<String> message,
            @BindingName("id") String id,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " received");

        //Parse query Parameters
        final String query = request.getQueryParameters().get("isComplete");
        final String body = request.getBody().orElse(query);

        TableEntity entity = tableClient.getEntity(PARTITION_KEY, id);
        entity.getProperties().put("isComplete", gson.fromJson(body, ToDo.class).isComplete());

        tableClient.updateEntity(entity , TableEntityUpdateMode.REPLACE);

        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " processed");
        return request.createResponseBuilder(HttpStatus.OK).build();
    }

    /**
     * Function to Delete a specific ToDo by its id
     * Triggers with an HttpTrigger on api/todo/{id} and deletes the todo with matches the sent id
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param id - id of the ToDo which shall be deleted
     * @return - returns a success code if the todo was found and thus delete and a bad request if it couldn't be found
     */
    @FunctionName("DeleteToDo")
    public HttpResponseMessage DeleteToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.DELETE, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE + "/{id}") HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"DeleteToDo\" with id:" + id + " received");

        tableClient.deleteEntity(PARTITION_KEY, id);
        return request.createResponseBuilder(HttpStatus.OK).build();
    }
}