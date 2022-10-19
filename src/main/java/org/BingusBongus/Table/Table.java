package org.BingusBongus.Table;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;

/**
 * Class which creates a connection to the Table Database
 * This connection can then be used by the other classes
 *
 * @author colllijo
 * @version 1.0.0
 */
public class Table
{
    private  static final String TABLE_NAME = "todos";
    public static final String PARTITION_KEY = "TODO";

    //Connect to the Database
    public final static TableClient client = new TableClientBuilder()
            .connectionString("DefaultEndpointsProtocol=https;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;")
            .tableName(TABLE_NAME)
            .buildClient();
}
