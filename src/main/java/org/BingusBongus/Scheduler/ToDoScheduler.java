package org.BingusBongus.Scheduler;

import com.azure.data.tables.models.ListEntitiesOptions;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import org.BingusBongus.Table.Table;
import org.BingusBongus.ToDo.EntityMapper;
import org.BingusBongus.ToDo.ToDo;

import java.time.LocalDateTime;

/**
 * This class contains Scheduled Functions
 * which trigger on a TimerTrigger
 *
 * @author colllijo
 * @version 1.0.0
 */
public class ToDoScheduler
{
    /**
     * This Function Triggers with a TimerTrigger each day at Midnight
     * It then queries the database for completed ToDos and checks their age
     * if it is over a week old it gets deleted
     */
    @FunctionName("deleteCompleteToDos")
    public void deleteCompleteToDos(
        @TimerTrigger(name = "deleteOldToDos", schedule = "0 0 * * *") String timerInfo,
        ExecutionContext context
    )
    {
        ToDo[] toDos = EntityMapper.TableEntitiesToToDos(Table.client.listEntities(new ListEntitiesOptions().setFilter("PartitionKey eq 'TODO' and isComplete eq true"), null, null));

        for(ToDo todo:toDos)
        {
            if(LocalDateTime.now().minusDays(7).isAfter(todo.getModifiedDate()))
            {
                Table.client.deleteEntity(Table.PARTITION_KEY, todo.getId());
                context.getLogger().info("Deleted the todo with the id:" + todo.getId() + "\nAs it has been complete for a week");
            }
        }
    }
}
