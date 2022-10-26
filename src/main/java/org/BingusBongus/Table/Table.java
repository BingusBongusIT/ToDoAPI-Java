package org.BingusBongus.Table;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import org.BingusBongus.ToDo.EntityMapper;
import org.BingusBongus.ToDo.ToDo;

/**
 * Class which creates a connection to the Table Database
 * This connection can then be used by the other classes
 *
 * @author colllijo
 * @version 1.0.0
 */
public class Table
{
    private static CosmosDatabase cosmosDatabase;
    private static CosmosContainer cosmosContainer;

    //Connect to the Database
    private final static CosmosClient cosmosClient = new CosmosClientBuilder()
            .endpoint(TableConnectionInfo.HOST)
            .key(TableConnectionInfo.MASTER_KEY)
            .consistencyLevel(ConsistencyLevel.EVENTUAL)
            .buildClient();

    /**
     * Creates a new Entry in the CosmosDB
     * after Parsing the ToDo to a JsonObject which can be added to the Table
     * @see org.BingusBongus.ToDo.EntityMapper
     *
     * @param todo - Todo to add to the CosmosDB
     */
    public static void createToDo(ToDo todo)
    {
        try
        {
            getContainerCreateResourcesIfNotExist().createItem(EntityMapper.ToDoToTableEntity(todo));
        }
        catch (CosmosException exception)
        {
            System.out.println("Error creating TODO item.\n");
            exception.printStackTrace();
        }
    }

    /**
     * Queries the cosmos db for all the ToDos
     * These are then parsed with the EntityMapper
     * before being returned
     * @see org.BingusBongus.ToDo.ToDo
     * @see org.BingusBongus.ToDo.EntityMapper
     *
     * @return - returns an Array of all fetched ToDos | On error returns null
     */
    public static ToDo[] getToDos()
    {
        String sql = "SELECT t.id, t.taskDescription, t.isComplete, t.modifiedDate, t.createdDate FROM ToDos t";

        try
        {
            return EntityMapper.TableEntitiesToToDos(
                    getContainerCreateResourcesIfNotExist()
                            .queryItems(sql, null, JsonNode.class)
            );
        }
        catch (Exception exception)
        {
            System.out.println("Error fetching ToDos\n" + exception);

            return null;
        }
    }

    /**
     * Queries the cosmos db for all the ToDos
     * These are then parsed with the EntityMapper
     * before being returned
     * @see org.BingusBongus.ToDo.ToDo
     * @see org.BingusBongus.ToDo.EntityMapper
     *
     * @return - returns an Array of all fetched ToDos | On error returns null
     */
    public static ToDo getToDoById(String id)
    {
        String sql = "SELECT t.id, t.taskDescription, t.isComplete, t.modifiedDate, t.createdDate FROM ToDos t WHERE t.id = '" + id + "'";

        try
        {
            CosmosPagedIterable<JsonNode> fetch = getContainerCreateResourcesIfNotExist()
                .queryItems(sql, null, JsonNode.class);

            if(fetch.stream().count() == 1)
            {
                return EntityMapper.TableEntityToToDo((JsonNode) fetch.stream().toArray()[0]);
            }
            else if(!fetch.stream().findAny().isPresent())
            {
                System.out.println("ToDo with the id: " + id + " couldn't be found");
            }
            else
            {
                System.out.println("Major error multiple ToDos with the same id found");
            }
            return null;
        }
        catch (Exception exception)
        {
            System.out.println("Error fetching ToDo\n" + exception);

            return null;
        }
    }

    /**
     * Updates a Table Entry with the id of the given todo
     *
     * @param todo - ToDo with the update values
     */
    public static void updateToDo(ToDo todo)
    {
        try
        {
            getContainerCreateResourcesIfNotExist().replaceItem(EntityMapper.ToDoToTableEntity(todo), todo.getId(), new PartitionKey(todo.getId()), new CosmosItemRequestOptions());
        }
        catch (CosmosException exception)
        {
            System.out.println("Error updating TODO item.\n");
            exception.printStackTrace();
        }
    }

    /**
     * Creates an Entry from the Database
     * with a given id
     *
     * @param id - id of the Entry to delete
     */
    public static void deleteToDo(String id)
    {
        try {
            getContainerCreateResourcesIfNotExist().deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
        }
        catch (CosmosException exception)
        {
            System.out.println("Error deleting TODO item.\n");
            exception.printStackTrace();
        }
    }

    /**
     * Uses the Conection to the Database and creates a connection to the ToDoDB Container
     * @see com.azure.cosmos
     *
     * @return - returns a CosmosContainer connected to the Database
     */
    private static CosmosContainer getContainerCreateResourcesIfNotExist()
    {
        try
        {
            if (cosmosDatabase == null)
            {
                CosmosDatabaseResponse cosmosDatabaseResponse = cosmosClient.createDatabaseIfNotExists(TableConnectionInfo.DATABASE_ID);
                cosmosDatabase = cosmosClient.getDatabase(cosmosDatabaseResponse.getProperties().getId());
            }
        }
        catch (CosmosException e)
        {
            System.out.println("Something has gone terribly wrong - the app wasn't able to create the Database.\n");
            e.printStackTrace();
        }

        try
        {
            if (cosmosContainer == null)
            {
                CosmosContainerProperties properties = new CosmosContainerProperties(TableConnectionInfo.CONTAINER_ID, TableConnectionInfo.PARTITION_KEY_PATH);
                CosmosContainerResponse cosmosContainerResponse = cosmosDatabase.createContainerIfNotExists(properties);
                cosmosContainer = cosmosDatabase.getContainer(cosmosContainerResponse.getProperties().getId());
            }
        }
        catch (CosmosException e)
        {
            System.out.println("Something has gone terribly wrong - the app wasn't able to create the Container.\n");
            e.printStackTrace();
        }

        return cosmosContainer;
    }
}