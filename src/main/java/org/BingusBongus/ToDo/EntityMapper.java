package org.BingusBongus.ToDo;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.models.TableEntity;
import org.BingusBongus.Table.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class with static Methods to Map the java objects to table-entities
 * and vis-versa to post and get from the database
 *
 * @author collijo
 * @version 2.1.0
 */
public class EntityMapper
{
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
        toDoData.put("modifiedDate", todo.getModifiedDate().toString().substring(0, todo.getModifiedDate().toString().length() - 1));
        toDoData.put("createdDate", todo.getCreatedDate().toString());
        return new TableEntity(Table.PARTITION_KEY, todo.getId()).setProperties(toDoData);
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
                Objects.equals(tableEntity.getProperty("isComplete").toString(), "true"),
                tableEntity.getProperty("modifiedDate").toString(),
                tableEntity.getProperty("createdDate").toString()
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
    public static ToDo[] TableEntitiesToToDos(PagedIterable<TableEntity> tableEntities)
    {
        ToDo[] toDos = new ToDo[(int)tableEntities.stream().count()];

        for(int i = 0; i < toDos.length; i++)
            toDos[i] = EntityMapper.TableEntityToToDo((TableEntity)tableEntities.stream().toArray()[i]);

        return toDos;
    }
}
