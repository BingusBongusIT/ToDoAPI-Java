package org.BingusBongus.ToDo;

import com.azure.data.tables.models.TableEntity;
import org.BingusBongus.ToDo.ToDo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class with static Methods to Map the java objects to table-entities
 * and vis-versa to post and get from the database
 *
 * @author collijo
 * @version 0.2.2
 */
public class EntityMapper
{
    private final static String PATITIONKEY = "TODO";

    /**
     * Takes in a ToDo abject and maps its values
     * to a TableEntity and returns this object
     * @see org.BingusBongus.ToDo.ToDo
     * @see com.azure.data.tables.models.TableEntity
     *
     * @param todo - ToDo object to map to a ToDoTableEntity
     * @return - the remapped ToDo object
     */
    public static TableEntity ToDoToTableEntity(ToDo todo)
    {
        Map<String, Object> toDoData = new HashMap<>();
        toDoData.put("taskDescription", todo.getTaskDescription());
        toDoData.put("isComplete", todo.isComplete());
        toDoData.put("modifiedDate", todo.getModifiedDate());
        toDoData.put("createdDate", todo.getCreatedDate());
        return new TableEntity(PATITIONKEY, todo.getId()).setProperties(toDoData);
    }

    /**
     * Takes in a TableEntity and maps its values
     * to a ToDo object which then is returned
     * @see com.azure.data.tables.models.TableEntity
     * @see org.BingusBongus.ToDo.ToDo
     *
     * @param tableEntity - TableEntity to map to a ToDo object
     * @return - the remapped TableEntity
     */
    public static ToDo TableEntityToToDo(TableEntity tableEntity)
    {
        return new ToDo(
                tableEntity.getRowKey(),
                tableEntity.getProperty("taskDescription").toString(),
                tableEntity.getProperty("isComplete").toString() == "true" ? true : false,
                tableEntity.getProperty("modifiedDate").toString(),
                tableEntity.getProperty("createdDate").toString()
        );
    }
}
