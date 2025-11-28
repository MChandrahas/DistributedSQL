# Use Eclipse Temurin (The standard OpenJDK build)
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy source code
COPY src /app/src
COPY pom.xml /app/

# Compile the code manually inside the container
RUN mkdir bin
RUN javac -d bin src/main/java/com/dbsql/storage/*.java \
    src/main/java/com/dbsql/query/*.java \
    src/main/java/com/dbsql/server/*.java \
    src/main/java/com/dbsql/client/*.java \
    src/main/java/com/dbsql/*.java

# Expose the database port
EXPOSE 4000

# Run the server by default
CMD ["java", "-cp", "bin", "com.dbsql.server.DatabaseServer"]