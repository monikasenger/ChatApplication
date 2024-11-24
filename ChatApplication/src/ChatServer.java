import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * class that implement server side of chat application
 * maintain list of connected client
 * handle incoming msg from client
 * log server event like connected and disconnected and broadcast the msg
 */

public class ChatServer {
    static final int PORT = 12345; // port where server connect with client
    static Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet(); // store output to server
    static JTextArea chatArea; // UI component where both chat and server msg display

    //constructor to initialize the server and pass the UI component for logs
    public ChatServer(JTextArea chatArea) {
        this.chatArea = chatArea;
        startServer(); // Call method to start the server
    }

    // method to start the server for client connections
   public static void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) { // server listens on defined port
                displayMessage("Server started on port " + PORT); // log message define server has started
                while (true) {
                    // wait for client conn. and start new handler for every client
                    new ClientHandler(serverSocket.accept()).start(); // accept and handle each client
                }
            } catch (IOException e) {
                displayMessage("Server error: " + e.getMessage()); // log any server error
            }
        }).start();
    }

    // method to update the chat area for server and chat msg
    public static void displayMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg+ "\n"); // add msg in chat area
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // scroll for latest msg
        });
    }

    // class to handle every client connection
    public static class ClientHandler extends Thread {
        static Socket socket; // client socket for communication
        static PrintWriter out; // output stream for sending msg to client
        static BufferedReader in; //  input stream to receive msg from  client

        // constructor to initializes  client socket
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        // method handle client connects start and communication
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // receive data from  client
                out = new PrintWriter(socket.getOutputStream(), true); // send data to client
                clientWriters.add(out); // add client output stream to list of active clients

                displayMessage("Client connected: " + socket.getInetAddress()); // log with client connects

                String msg;
                while ((msg = in.readLine()) != null) { // read msg from client contiues
                    displayMessage("\nServer: " + msg); // add  Chat to different from chat msg

                    // display msg to all connected clients
                    for (PrintWriter writer : clientWriters) {
                        writer.println(msg); // send msg to every client
                    }
                }
            } catch (IOException e) {
                displayMessage("Client disconnected: " + socket.getInetAddress()); // log when a client disconnects
            } finally {
                // cleanup when client disconnects
                if (out != null) {
                    clientWriters.remove(out); // remove client from active list
                }
                try {
                    socket.close(); // close client socket
                } catch (IOException e) {
                    displayMessage("Error closing socket: " + e.getMessage()); //log any errors while closing socket
                }
            }
        }
    }
}
