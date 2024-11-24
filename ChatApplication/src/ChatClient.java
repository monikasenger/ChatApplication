import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * class to implement client side of chat application.
 * it connect to chat server via socket
 * send msg to server
 * receive and display msg from server
 */
public class ChatClient {
    static final String SERVER_ADDRESS = "localhost"; //server localhost address for testing
    static final int SERVER_PORT = 12345; // port for client to connect
    static Socket socket; // socket connect with server
    static PrintWriter out; // output stream send msg to server
    static JTextArea chatArea; // text area to displayed msg in chat

    // constructor to initializes with client and passes chat area UI component
    public ChatClient(JTextArea chatArea) {
        this.chatArea = chatArea;
        connectToServer(); // call method to connect with server
    }

    // method to connect with server and start listening for incoming msg
    public static void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT); // connect with server
                out = new PrintWriter(socket.getOutputStream(), true); // get output stream to send msg to server

                // start new thread to handle incoming msg from server
                new Thread(new IncomingMessageHandler(socket)).start();
                displayMessage("Client Connected to server");
            } catch (IOException e) {
                displayMessage("Error!!! Cannot connected to server: " + e.getMessage()); // display error msg when connection fails
            }
        }).start();
    }

    // method to display msg in the chat area
   public static void displayMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n"); // append  msg in chat area
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // scroll the latest msg

        });
    }

    // method to send messages toserver
    public void sendMessage(String msg) {
        if (!msg.isEmpty()) {
            out.println(msg); // send msg to server
        }
    }

    // class to handles receiving msg from  server
    public static class IncomingMessageHandler implements Runnable {
        static BufferedReader in; // input stream to read msg from  server

        // constructor to initialize input stream
        public IncomingMessageHandler(Socket socket) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // setup input stream to receive data
            } catch (IOException e) {
                displayMessage("Error!! Cannot reading from server: " + e.getMessage()); // display error if input stream setup fails
            }
        }

        @Override
        public void run() {
            String msg;
            try {
                // condition to read msg from  server and display in  chat area coutinue
                while ((msg = in.readLine()) != null) {
                    displayMessage("You: " + msg); // add Chat in chat msg to show

                }
            } catch (IOException e) {
                displayMessage("Client disconnected from server."); // user notify when user disconnect from server
            }
        }
    }
}
