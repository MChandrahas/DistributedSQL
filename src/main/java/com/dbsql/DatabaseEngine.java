package com.dbsql;

import com.dbsql.query.*;
import com.dbsql.storage.Table;
import java.nio.charset.StandardCharsets;

public class DatabaseEngine {
    private final Table table;

    public DatabaseEngine(String dbFile) {
        this.table = new Table(dbFile);
    }

    public String execute(String sql) {
        Query query = SQLParser.parse(sql);

        if (query.type == Query.Type.INSERT) {
            return executeInsert((InsertQuery) query);
        } else if (query.type == Query.Type.SELECT) {
            return executeSelect((SelectQuery) query);
        }
        
        return "Error: Unknown query type";
    }

    // NEW: Handle SELECT
    private String executeSelect(SelectQuery query) {
        // In a real DB, we would check query.tableName matches our table
        // and filter columns. For now, we dump everything (SELECT *).
        
        StringBuilder result = new StringBuilder();
        // Perform the Sequential Scan
        var rows = table.scan();
        
        result.append("Query Result (").append(rows.size()).append(" rows):\n");
        for (String row : rows) {
            result.append(row).append("\n");
        }
        return result.toString();
    }

    private String executeInsert(InsertQuery query) {
        // Convert the values to a simple string format "val1|val2|val3"
        // In a real DB, we would serialize types properly.
        StringBuilder rowData = new StringBuilder();
        for (Object val : query.values) {
            rowData.append(val.toString()).append("|");
        }
        
        // Remove trailing pipe
        if (rowData.length() > 0) rowData.setLength(rowData.length() - 1);
        
        table.insert(rowData.toString().getBytes(StandardCharsets.UTF_8));
        
        return "Inserted 1 row into " + query.tableName;
    }
    
    public void close() {
        table.close();
    }

    
}