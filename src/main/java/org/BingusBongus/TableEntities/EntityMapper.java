package org.BingusBongus.TableEntities;

import org.BingusBongus.ToDo.ToDo;

/**
 * Class with static Methods to Map the java objects to table-entities
 * and vis-versa to post and get from the database
 *
 * @author collijo
 * @version 0.2.2
 */
public class EntityMapper
{
    /**
     * Takes in a ToDo abject and maps its values
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

    /**
     * Takes in a ToDoTableEntity and maps its values
     * to a ToDo object and returns this object
     * @see org.BingusBongus.ToDo.ToDo
     * @see org.BingusBongus.TableEntities.ToDoTableEntity
     *
     * @param toDoTableEntity - ToDoTableEntity to map to a ToDo object
     * @return - the remapped ToDoTableEntity
     */
    public static ToDo ToDoTableEntityToToDo(ToDoTableEntity toDoTableEntity)
    {
        return new ToDo
        (
                toDoTableEntity.getRowKey(),
                toDoTableEntity.getTaskDescription(),
                toDoTableEntity.isComplete(),
                toDoTableEntity.getModifiedDate(),
                toDoTableEntity.getCreatedDate()
        );
    }

    /**
     * Takes in a ToDoTableEntity array and maps all the ToDoTableEntity values
     * to a ToDos in a ToDo array and returns this array
     * @see org.BingusBongus.ToDo.ToDo
     * @see org.BingusBongus.TableEntities.ToDoTableEntity
     *
     * @param toDoTableEntities - array of ToDoTableEntity objects which will get mapped to ToDos
     * @return - returns an array of ToDos with the remapped values
     */
    public static ToDo[] ToDoTableEntitiesToToDos(ToDoTableEntity[] toDoTableEntities)
    {
        ToDo[] toDos = new ToDo[toDoTableEntities.length];

        for(int i = 0; i < toDoTableEntities.length; i++)
        {
            toDos[i] = new ToDo(toDoTableEntities[i].getRowKey(), toDoTableEntities[i].getTaskDescription(), toDoTableEntities[i].isComplete(), toDoTableEntities[i].getModifiedDate(), toDoTableEntities[i].getCreatedDate());
        }

        return toDos;
    }
}
