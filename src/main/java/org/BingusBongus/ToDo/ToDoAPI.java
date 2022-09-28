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
    final public Gson gson = new Gson();

    /**
     * Function to crate a new todo.
     * Triggers with an HttpTrigger on api/todo and creates a new todo from the requestbody
     * which is the added to the list of todos
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

        ToDo todo = new ToDo(gson.fromJson(query, ToDoCreateModel.class).getTaskDescription());

        ToDoList.add(todo);

        context.getLogger().info("New Todo JSON: " + gson.toJson(todo));
        context.getLogger().info("Java HTTP POST Request \"CreateToDo\" processed\nNew ToDo has been added to the List");

        return request.createResponseBuilder(HttpStatus.OK).body(todo).build();
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

        if(ToDoList.size() > 0)
            context.getLogger().info("" + ToDoList.get(0).isComplete());

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
        final String query = request.getQueryParameters().get("id");
        final String id = request.getBody().orElse(query);

        ToDo todo = null;

        for(ToDo ltodo:ToDoList)
        {
            if(ltodo.getId().equals(id))
                todo = ltodo;
        }

        context.getLogger().info("Java HTTP GET Request \"GetToDoById\" with id:${id} processed");
        if(todo == null)
        {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
        }
        else
        {
            return request.createResponseBuilder(HttpStatus.OK).body(todo).build();
        }

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
            final ExecutionContext context,
            @BindingName("id") String id
    )
    {
        context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " received");

        //Parse query Parameters
        final String query = request.getQueryParameters().get("id");
        final String body = request.getBody().orElse(query);

        ToDo cTodo = null;
        for(ToDo todo:ToDoList)
        {
            if(todo.getId().equals(id))
            {
                todo.setComplete(gson.fromJson(body, ToDoUpdateModel.class).isComplete());

                context.getLogger().info("Java HTTP GET Request \"UpdateToDo\" with id:" + id + " processed");
                return request.createResponseBuilder(HttpStatus.OK).body(todo).build();
            }
        }

        context.getLogger().info("Couldn't find " + id + " in ToDoList");
        return request.createResponseBuilder(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS).build();
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
            final ExecutionContext context,
            @BindingName("id") String id
    )
    {
        context.getLogger().info("Java HTTP GET Request \"DeleteToDo\" with id:" + id + " received");

        //Parse query Parameters

        for(ToDo todo: ToDoList)
        {
            if(todo.getId().equals(id))
            {
                context.getLogger().info("Java HTTP GET Request \"DeleteToDo\" with id:" + id + " processed");
                return request.createResponseBuilder(HttpStatus.OK).build();
            }
        }

        context.getLogger().info("Couldn't find " + id + " in ToDoList");
        return request.createResponseBuilder(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS).build();
    }
}