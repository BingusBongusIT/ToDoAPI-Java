package org.BingusBongus.TableEntities;

import org.BingusBongus.TableEntities.ToDoTableEntity;
import org.BingusBongus.ToDo.ToDo;

/**
 * Class with static Methods to Map the java objects to table-entities
 * and vis-versa to post and get from the database
 *
 * @author collijo
 * @version 0.1.0
 */
public class EntityMapper
{
    /**
     * Takes in a ToDo abject and maps it's values
     * to a ToDoTableEntity and returns this object
     * @see org.BingusBongus.ToDo.ToDo
     * @see org.BingusBongus.TableEntities.ToDoTableEntity
     *
     * @param todo - ToDo object to map to a ToDoTableEntity
     * @return - the remapped ToDo object
     */
    public static ToDoTableEntity ToDoToTableEntity(ToDo todo)
    {
        return new ToDoTableEntity
        (
            "TODO",
                todo.getId(),
                todo.getTaskDescription(),
                todo.isComplete(),
                todo.getModifiedDate().toString(),
                todo.getCreatedDate().toString()
        );
    }
}
