import javax.swing.*;
import java.awt.*;

/**
 * class to show GUI for chat application
 * show server and client component
 * display server log like connection and disconnection and chat msg from user
 */

public class ChatUI {
    static JFrame frame; // main window of  application
    static JTextArea chatArea; // text area to display chat and server msg
    static JTextField inputField; // text field to write msg
    static JButton sendButton; //button to send msg
    static ChatServer chatServer; // server instance
    static ChatClient chatClient; // client instance

    // constructor to set UI and start server and client
    public ChatUI() {
        setupUI(); // set GUI
        chatClient = new ChatClient(chatArea); // initialize client and pass in chat area
        chatServer = new ChatServer(chatArea); // start  server and pass in chat area
    }

    // method to set  user interface with components
    public static void setupUI() {
        frame = new JFrame("Chat Application"); // create  new frame for app
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close app when window is closed
        frame.setSize(600, 500); // set  size of  window
        frame.setLayout(new BorderLayout()); // use border layout for frame

        // chat areato display server logs and chat msg
        chatArea = new JTextArea();
        chatArea.setEditable(false); // chat area cannot edit by  user
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14)); // set for chat area
        chatArea.setBorder(BorderFactory.createTitledBorder("Chat and Server Messages")); // title for chat area
        JScrollPane chatScrollPane = new JScrollPane(chatArea); // make chat area scrollable

        // panel for input and sending msg
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField(); // input field for msg
        inputField.setFont(new Font("Arial", Font.BOLD, 15)); // set font for input field
        inputField.setBackground(Color.lightGray);
        inputField.setForeground(Color.darkGray);
        sendButton = new JButton("Send"); // button to send msg
        sendButton.setFont(new Font("Arial", Font.BOLD, 15)); //set font for send button
        sendButton.setForeground(Color.white);
        sendButton.setBackground(Color.black);

        //action listeners for input field and send button
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // add chat area and input panel in main window
        frame.add(chatScrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // window visible
        frame.setVisible(true);
    }

    // method to send  msg write in input field
    public static void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            chatClient.sendMessage(msg); // send msg to server
            inputField.setText(""); // clearinput field
        }
    }

    //main method to start the chat application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatUI::new); // start UI
    }
}

