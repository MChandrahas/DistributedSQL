package com.dbsql.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class DatabaseClient {
    private static final String HOST = "localhost";
    private static final int PORT = 4000;

    public static void main(String[] args) {
        System.out.println("--- Connected to DistributedSQL Node ---");
        System.out.println("Type your SQL command (or 'exit'):");

        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.print("SQL> "); // The prompt
                String userInput = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }

                // Send to server
                out.println(userInput);

                // Read response
                String response = in.readLine();
                if (response != null) {
                    // Put the newlines back
                    System.out.println(response.replace("<NL>", "\n"));
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Is the server running? " + e.getMessage());
        }
    }
}