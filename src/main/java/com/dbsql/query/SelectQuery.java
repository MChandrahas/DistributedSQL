package com.dbsql.query;

public class SelectQuery extends Query {
    public final String tableName;
    public final String[] columns; // e.g., ["*"] or ["id", "name"]

    public SelectQuery(String tableName, String[] columns) {
        super(Type.SELECT);
        this.tableName = tableName;
        this.columns = columns;
    }
}