package org.BingusBongus.ToDo;

import com.google.gson.Gson;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.BingusBongus.TableEntities.EntityMapper;
import org.BingusBongus.TableEntities.ToDoTableEntity;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * API between the storage, creation and management of the todos and the
 * users interaction with them
 *
 * @author colllijo
 * @version 1.1.0
 */
public class ToDoAPI
{
    //Constant Variables
    private final String ROUTE = "todo";
    private final String DBNAME = "todoDB";
    private final String TABLENAME = "todos";
    private final String PARTITIONKEY = "TODO";

    //Local List of all ToDo's
    static List<ToDo> ToDoList = new ArrayList<>();
    final public Gson gson = new Gson();

    /**
     * Function to crate a new todo.
     * Triggers with an HttpTrigger on api/todo and creates a new ToDo from the request-body
     * which is then added to the Database after being converted
     * @see org.BingusBongus.ToDo.ToDo
     * @see org.BingusBongus.TableEntities.ToDoTableEntity
     *
     * To create a ToDo from the request google's Gson library is used to create a object from the json
     * This object is then mapped to a tableentity which gets outputed to the Table
     * @see com.google.gson.Gson#fromJson(Reader, Type)
     * @see org.BingusBongus.TableEntities.EntityMapper#ToDoToTableEntity(ToDo)
     *
     * @param request - request body containing the todo to create in json format
     * @param todo - OutputBinding to the Database
     * @return - returns a success code if the todo was successfully be added to the list
     */
    @FunctionName("CreateToDo")
    public HttpResponseMessage createToDo
    (
        @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) HttpRequestMessage<String> request,
        @TableOutput(name=DBNAME, tableName = TABLENAME, connection="AzureWebJobsStorage") OutputBinding<ToDoTableEntity> todo,
        final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" received");

        //Get query
        final String query = request.getBody();

        //Create a Todo from the query using gson to extract the taskDescription from the query and map it to a table-entity
        ToDoTableEntity entity = EntityMapper.ToDoToTableEntity(new ToDo(gson.fromJson(query, ToDo.class).getTaskDescription()));
        todo.setValue(entity);


        context.getLogger().info("New Todo JSON: " + gson.toJson(entity));
        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" processed\nNew ToDo has been added to the List");

        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(entity).build();
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
            @TableInput(name = DBNAME, partitionKey = PARTITIONKEY, tableName = TABLENAME, connection = "AzureWebJobsStorage") ToDoTableEntity[] toDoTableEntities,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"GetToDos\" received");

        return request.createResponseBuilder(HttpStatus.OK).body(EntityMapper.ToDoTableEntitiesToToDos(toDoTableEntities)).build();
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
            @TableInput(name = DBNAME, partitionKey = PARTITIONKEY, rowKey = "{id}" ,tableName = TABLENAME, connection = "AzureWebJobsStorage") ToDoTableEntity toDoTableEntity,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"GetToDoById\" with id:${id} received");

        return request.createResponseBuilder(HttpStatus.OK).body(EntityMapper.ToDoTableEntityToToDo(toDoTableEntity)).build();
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
            @BindingName("id") String id,
//            @TableOutput(name = DBNAME, partitionKey = PARTITIONKEY, rowKey = "{id}" ,tableName = TABLENAME, connection = "AzureWebJobsStorage") OutputBinding<ToDoTableEntity> toDoTableEntity,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " received");

        //Parse query Parameters
        final String query = request.getQueryParameters().get("isComplete");
        final String body = request.getBody().orElse(query);

//        todo.setComplete(gson.fromJson(body, ToDo.class).isComplete());
//        toDoTableEntity.setValue();

        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " processed");
        return request.createResponseBuilder(HttpStatus.I_AM_A_TEAPOT).build();
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
            final ExecutionContext context,
            @BindingName("id") String id
    )
    {
        context.getLogger().info("Java HTTP GET Request \"DeleteToDo\" with id:" + id + " received");

        //Check all Todos for a matching id
        for(ToDo todo: ToDoList)
        {
            if(todo.getId().equals(id))
            {
                context.getLogger().info("Java HTTP GET Request \"DeleteToDo\" with id:" + id + " processed");
                return request.createResponseBuilder(HttpStatus.OK).build();
            }
        }

        context.getLogger().info("Couldn't find " + id + " in ToDoList");
        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
    }
}