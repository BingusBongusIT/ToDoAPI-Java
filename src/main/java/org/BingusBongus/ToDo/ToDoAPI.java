package org.BingusBongus.ToDo;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

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
 * @version 1.0.0
 */
public class ToDoAPI
{
    //Local List of all ToDo's
    static List<ToDo> ToDoList = new ArrayList<>();
    final public Gson gson = new Gson();

    /**
     * Function to crate a new todo.
     * Triggers with an HttpTrigger on api/todo and creates a new ToDo from the request-body
     * which is then added to the list of todos
     * @see org.BingusBongus.ToDo.ToDo
     *
     * The create a ToDo from the request google's Gson library is used to create a object from the json
     * which is then added to the Todo
     * @see com.google.gson.Gson#fromJson(Reader, Type)
     *
     * @param request - request body containing the todo to create in json format
     * @param context
     * @return - returns a sucess code if the todo was successfully be added to the list
     */
    @FunctionName("CreateToDo")
    public HttpResponseMessage createToDo
    (
        @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo")
        HttpRequestMessage<String> request,
        final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" received");

        //Get query
        final String query = request.getBody();

        //Create a Todo from the query using gson to extraxt the taskDescription from the query
        ToDo todo = new ToDo(gson.fromJson(query, ToDo.class).getTaskDescription());

        ToDoList.add(todo);

        context.getLogger().info("New Todo JSON: " + gson.toJson(todo));
        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" processed\nNew ToDo has been added to the List");

        return request.createResponseBuilder(HttpStatus.OK).body(todo).build();
    }

    /**
     * Function to return all ToDos which are currently stored
     * Triggers with an HttpTrigger on api/todo and returns the Todolsit in the response
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param request
     * @param context
     * @return - Returns the Todolist as well as a success status
     */
    @FunctionName("GetToDos")
    public HttpResponseMessage GetToDos
    (
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"GetToDos\" received");

        return request.createResponseBuilder(HttpStatus.OK).body(ToDoList).build();
    }

    /**
     *
     * Function to return a specific todo by its id
     * Triggers with an HttpTrigger on api/todo/{id} and returns the todo with the corresponding id if found
     * Checks all the ToDo for if the match the given id and if that one exists returns it
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param request
     * @param context
     * @param id - id of the ToDo which shall be returned
     * @return - If Found returns the ToDo an id matching the id parameter and a succes code, else returns a bad request
     */
    @FunctionName("GetToDoById")
    public HttpResponseMessage GetToDoById(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context,
            @BindingName("id") String id
    )
    {
        context.getLogger().info("Java HTTP GET Request \"GetToDoById\" with id:${id} received");

        ToDo todo = null;

        //Go through all ToDos of the ToDoList searching for a matching id
        for(ToDo ltodo:ToDoList)
        {
            if(ltodo.getId().equals(id))
                todo = ltodo;
        }

        context.getLogger().info("Java HTTP GET Request \"GetToDoById\" with id:${id} processed");

        if(todo == null){ return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build(); }
        else{ return request.createResponseBuilder(HttpStatus.OK).body(todo).build(); }
    }

    /**
     * Function to update the contents of a specific todo
     * Triggers with an HttpTrigger on api/todo/{id} and updates the Todo
     * matching the id of the request ot the contents of the request body
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param request - body of the request containing the new information of the Todo
     * @param context
     * @param id - id of the ToDo to update
     * @return - if the ToDo with the given id was found an successfully update returns a success code and the updated to else wise returns a bad request
     */
    @FunctionName("UpdateToDo")
    public HttpResponseMessage UpdateToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.PUT, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context,
            @BindingName("id") String id
    )
    {
        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " received");

        //Parse query Parameters
        final String query = request.getQueryParameters().get("isComplete");
        final String body = request.getBody().orElse(query);

        //Check all ToDos in the List for a matching id
        for(ToDo todo:ToDoList)
        {
            if(todo.getId().equals(id))
            {
                //As a matching id has been found update that todo
                todo.setComplete(gson.fromJson(body, ToDo.class).isComplete());

                context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " processed");
                return request.createResponseBuilder(HttpStatus.OK).body(todo).build();
            }
        }

        context.getLogger().info("Couldn't find " + id + " in ToDoList");
        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Function to Delete a specific ToDo by its id
     * Triggers with an HttpTrigger on api/todo/{id} and deletes the todo with matches the sent id
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param request
     * @param context
     * @param id - id of the ToDo which shall be deleted
     * @return - returns a success code if the todo was found and thus delete and a bad request if it couldn't be found
     */
    @FunctionName("DeleteToDo")
    public HttpResponseMessage DeleteToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.DELETE, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}")
            HttpRequestMessage<Optional<String>> request,
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