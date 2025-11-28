package com.dbsql.query;

import java.util.ArrayList;
import java.util.List;

public class SQLParser {

    public static Query parse(String sql) {
        String normalized = sql.trim();
        String[] tokens = normalized.split("\\s+");

        if (tokens.length == 0) throw new IllegalArgumentException("Empty query");

        String command = tokens[0].toUpperCase();
        
        if ("INSERT".equals(command)) {
            return parseInsert(normalized);
        } else if ("SELECT".equals(command)) {
            return parseSelect(normalized);
        }
        
        throw new UnsupportedOperationException("Unsupported command: " + command);
    }

    private static InsertQuery parseInsert(String sql) {
        // (Same code as before - keeping it brief here, but ensure you keep your previous logic)
        try {
            int intoIndex = sql.toUpperCase().indexOf("INTO");
            int valuesIndex = sql.toUpperCase().indexOf("VALUES");
            if (intoIndex == -1 || valuesIndex == -1) throw new IllegalArgumentException("Syntax Error");
            
            String tableName = sql.substring(intoIndex + 4, valuesIndex).trim();
            int openParen = sql.indexOf("(");
            int closeParen = sql.lastIndexOf(")");
            String rawValues = sql.substring(openParen + 1, closeParen);
            String[] valueParts = rawValues.split(",");
            
            List<Object> parsedValues = new ArrayList<>();
            for (String part : valueParts) {
                String val = part.trim();
                if (val.startsWith("\"") && val.endsWith("\"")) parsedValues.add(val.substring(1, val.length() - 1));
                else parsedValues.add(Integer.parseInt(val));
            }
            return new InsertQuery(tableName, parsedValues.toArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse INSERT: " + e.getMessage());
        }
    }

    // NEW: Logic to parse "SELECT * FROM users"
    private static SelectQuery parseSelect(String sql) {
        try {
            int selectIndex = sql.toUpperCase().indexOf("SELECT");
            int fromIndex = sql.toUpperCase().indexOf("FROM");
            
            if (selectIndex == -1 || fromIndex == -1) {
                throw new IllegalArgumentException("Syntax Error: Missing SELECT or FROM");
            }

            // Extract columns (e.g., "*")
            String colsRaw = sql.substring(selectIndex + 6, fromIndex).trim();
            String[] columns = colsRaw.split(",");

            // Extract table name
            String tableName = sql.substring(fromIndex + 4).trim();
            // Remove semicolon if present
            if (tableName.endsWith(";")) tableName = tableName.substring(0, tableName.length() - 1);

            return new SelectQuery(tableName, columns);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse SELECT: " + e.getMessage());
        }
    }
}