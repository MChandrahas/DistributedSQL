package com.dbsql.server;

import com.dbsql.DatabaseEngine;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class DatabaseServer {
    private static final int PORT = 4000;

    public static void main(String[] args) {
        // Open the database engine
        DatabaseEngine engine = new DatabaseEngine("mydb.db");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("--- DistributedSQL Server is running on port " + PORT + " ---");

            while (true) {
                // Wait for a client to connect (this blocks until someone connects)
                Socket clientSocket = serverSocket.accept();
                
                // Handle the client in a new thread (so we can handle multiple users)
                new Thread(() -> handleClient(clientSocket, engine)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, DatabaseEngine engine) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String inputLine;
            // Read messages from the client until they disconnect
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                
                String response;
                try {
                    // Send the SQL to our Engine
                    // Synchronized to prevent two users writing to the file at the exact same time
                    synchronized (engine) {
                        response = engine.execute(inputLine);
                    }
                } catch (Exception e) {
                    response = "Error: " + e.getMessage();
                }
                
                // Send answer back to client (replace newlines with a token so it fits on one line)
                out.println(response.replace("\n", "<NL>")); 
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }
}