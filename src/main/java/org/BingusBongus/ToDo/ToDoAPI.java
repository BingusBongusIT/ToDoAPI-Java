package org.BingusBongus.ToDo;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.BingusBongus.JSON.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ToDoAPI
{
    //Local List of all ToDo's
    static List<ToDo> ToDoList = new ArrayList<>();

    /**
     * Function to crate a new todo.
     * Triggers with an HttpTrigger on api/todo and creates a new todo from the requestbody
     * which is the added to the list of todos
     *
     * @see org.BingusBongus.JSON.getParamFromJson()
     *
     * @param request
     * @param context
     * @return
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
        context.getLogger().info("Java HTTP POST query body " + query);

        ToDo todo = new ToDo(JSON.getPramFromJson(query, "taskDescription"));
        ToDoList.add(todo);

        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" processed\nNew ToDo has been added to the List");

        return request.createResponseBuilder(HttpStatus.OK).build();
    }

    /**
     *
     * @param request
     * @param context
     * @return
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

        context.getLogger().info("Java HTTP GET Request \"GetToDos\" processed");
        return request.createResponseBuilder(HttpStatus.OK).body(ToDoList).build();
    }

    /**
     *
     * @param request
     * @param context
     * @return
     */
    @FunctionName("GetToDoById")
    public HttpResponseMessage GetToDoById(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"GetToDoById\" with id:${id} received");

        //Parse query Parameters
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        ToDo todo = ToDoList.get(0);
        context.getLogger().info("Java HTTP GET Request \"GetToDoById\" with id:${id} processed");
        return request.createResponseBuilder(HttpStatus.OK).body(todo).build();
    }

    /**
     *
     * @param request
     * @param context
     * @return
     */
    @FunctionName("UpdateToDo")
    public HttpResponseMessage UpdateToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.PUT, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:${id} received");

        //Parse query Parameters
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:${id} processed");
        return request.createResponseBuilder(HttpStatus.OK).build();
    }

    /**
     *
     * @param request
     * @param context
     * @return
     */
    @FunctionName("DeleteToDo")
    public HttpResponseMessage DeleteToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.DELETE, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context
    )
    {
        context.getLogger().info("Java HTTP GET Request \"DeleteToDo\" with id:${id} received");

        //Parse query Parameters
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        context.getLogger().info("Java HTTP GET Request \"DeleteToDo\" with id:${id} processed");
        return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
    }
}