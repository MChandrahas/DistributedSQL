package com.dbsql.query;

public class InsertQuery extends Query {
    public final String tableName;
    public final Object[] values;

    public InsertQuery(String tableName, Object[] values) {
        super(Type.INSERT);
        this.tableName = tableName;
        this.values = values;
    }
}