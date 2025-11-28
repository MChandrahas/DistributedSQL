package com.dbsql.query;

// The parent class for all SQL commands
public abstract class Query {
    public enum Type { INSERT, SELECT }
    public final Type type;

    public Query(Type type) {
        this.type = type;
    }
}