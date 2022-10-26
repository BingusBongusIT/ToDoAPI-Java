package org.BingusBongus.ToDo;

import com.azure.cosmos.implementation.Utils;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.gson.Gson;

/**
 * Class with static Methods to Map the java objects to table-entities
 * and vis-versa to post and get from the database
 *
 * @author collijo
 * @version 3.0.0
 */
public class EntityMapper
{
    private final static Gson gson = new Gson();

    /**
     * Takes in a ToDo abject and maps its values
     * to a TableEntity and returns this object
     * @see org.BingusBongus.ToDo.ToDo
     * @see com.azure.data.tables.models.TableEntity
     *
     * @param todo - ToDo object to map to a ToDoTableEntity
     * @return - the remapped ToDo object
     */
    public static JsonNode ToDoToTableEntity(ToDo todo)
    {
        ObjectNode todoJson = Utils.getSimpleObjectMapper().createObjectNode();

        todoJson.put("id", todo.getId());
        todoJson.put("taskDescription", todo.getTaskDescription());
        todoJson.put("isComplete", todo.isComplete());
        todoJson.put("modifiedDate", todo.getModifiedDate().toString());
        todoJson.put("createdDate", todo.getCreatedDate().toString());

        return todoJson;
    }

    /**
     * Takes in a CosmosPagedIterable<JsonNode> and maps the json
     * to a ToDo object which then is returned
     * @see org.BingusBongus.ToDo.ToDo
     * @see com.google.gson.Gson
     *
     * @param tableEntity - CosmosPagedIterable<> to map to a ToDo object
     * @return - the remapped TableEntity
     */
    public static ToDo TableEntityToToDo(JsonNode tableEntity)
    {
        return gson.fromJson(
                tableEntity.toString(),
                ToDo.class
        );
    }

    /**
     * Receives a PagedIterable and converts it to an Array
     * as well as converting the TableEntities contained in it to ToDos
     * @see org.BingusBongus.ToDo.ToDo
     * @see com.azure.data.tables.models.TableEntity
     *
     * @param tableEntities - PagedIterable<TableEntity> which has been received from a table query
     * @return - reutrns a ToDo[] with the remapped values of all TableEntities
     */
    public static ToDo[] TableEntitiesToToDos(CosmosPagedIterable<JsonNode> tableEntities)
    {
        ToDo[] toDos = new ToDo[(int)tableEntities.stream().count()];

        for(int i = 0; i < toDos.length; i++)
            toDos[i] = TableEntityToToDo((JsonNode) tableEntities.stream().toArray()[i]);

        return toDos;
    }

}
