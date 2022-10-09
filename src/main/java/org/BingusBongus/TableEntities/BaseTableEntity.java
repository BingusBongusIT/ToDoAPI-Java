package org.BingusBongus.TableEntities;

/**
 * Basic Table Entity to output to the tables database
 *
 * @author colllijo
 * @version 1.0.0
 */
public class BaseTableEntity
{
    private String PartitionKey;
    private String RowKey;

    public BaseTableEntity(String partitionKey, String rowKey)
    {
        this.PartitionKey = partitionKey;
        RowKey = rowKey;
    }

    public String getPartitionKey() {return this.PartitionKey;}
    public void setPartitionKey(String key) {this.PartitionKey = key; }
    public String getRowKey() {return this.RowKey;}
    public void setRowKey(String key) {this.RowKey = key; }
}