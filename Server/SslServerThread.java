package Server;

import Other.MyStreamSocket;
import Other.Stream;

import java.io.IOException;
import java.util.*;

/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * 
 * @author M. L. Liu
 */

public class SslServerThread implements Runnable {
    private String usernameString;
    private String passwordString;
    Stream responseStreamMessage;

    // Create a hashmap to store the username and password
    private final static HashMap<String, String> allUsersList = new HashMap<>();

    // Create a hashmap to store all the messages for the user
    private final static HashMap<String, List<String>> allMessagesList = new HashMap<>();
    private List<String> msgListOfUser;
    private final MyStreamSocket myDataSocket;

    SslServerThread(MyStreamSocket myDataSocket) {
        this.myDataSocket = myDataSocket;
        allUsersList.put("admin", "admin");
        allUsersList.put("kyle", "password01");
        allUsersList.put("guest", "guestuser1");
        allUsersList.put("john", "john1234");
        msgListOfUser = new ArrayList<String>();
    }

    public void run() {
        Stream responseStreamMessage;
        boolean done = false;
        Stream incomingStreamMessage = null;
        try {

            while (!done) {
                incomingStreamMessage = myDataSocket.receiveMessage();

                if (incomingStreamMessage.getMsgCode() == 100) {
                    handleLogin(incomingStreamMessage.getMessage());
                }

                else if (incomingStreamMessage.getMsgCode() == 200) {
                    // Now send the echo to the requestor
                    handleUpload(incomingStreamMessage.getMessage());
                }

                else if (incomingStreamMessage.getMsgCode() == 300) {
                    handleDownload();
                }

                else if (incomingStreamMessage.getMsgCode() == 400) {
                    myDataSocket.close();
                    System.out.println("Session terminated.");
                    responseStreamMessage = new Stream(401, "You have been successfully logged out of the server");
                    done = true;
                }

                else {
                    responseStreamMessage = new Stream(402, "Error! The message code is not recognised!");
                    myDataSocket.sendMessage(responseStreamMessage);
                }
            }

        } catch (Exception ex) {
            System.out.println("Exception caught in thread: " + ex);
        }
    }

    private boolean storeMessage(String userName, String msg) {
        // Determine if a message is stored based on the arrayList increasing in size
        List<String> messages = allMessagesList.get(userName);
        if (messages == null) {
            messages = new ArrayList<>();
            allMessagesList.put(userName, messages);
        }
        int originalSize = messages.size();
        messages.add(msg);
        int newSize = messages.size();
        return newSize > originalSize;
    }

    private void handleLogin(String message) {
        int index = message.indexOf(' ');
        String[] temp = new String[2];
        if (index != -1) {
            temp[0] = message.substring(0, index);
            temp[1] = message.substring(index + 1);
        } else {
            temp[0] = message;
            temp[1] = "";
        }

        if (temp[0] == "" | temp[0] == null) {
            responseStreamMessage = new Stream(102, "Error! A username has not been provided!");
            try {
                myDataSocket.sendMessage(responseStreamMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        else if (temp[1] == "" | temp[1] == null) {
            responseStreamMessage = new Stream(103, "Error! A password has not been provided!");
            try {
                myDataSocket.sendMessage(responseStreamMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {

            usernameString = temp[0];
            passwordString = allUsersList.get(temp[0]);

            if (usernameString.equals(temp[0]) && passwordString.equals(temp[1])) {
                responseStreamMessage = new Stream(101, "Login Successful! Welcome to the system!");
            } else
                responseStreamMessage = new Stream(104,
                        "The username or password entered is not recognised!");

            try {
                myDataSocket.sendMessage(responseStreamMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUpload(String message) {
        boolean isStored = storeMessage(usernameString, message);
        if (isStored)
            responseStreamMessage = new Stream(201, "Upload Successful!\n");
        else
            responseStreamMessage = new Stream(202, "Upload Unsuccessful!\n");

        try {
            myDataSocket.sendMessage(responseStreamMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDownload() throws IOException {
        responseStreamMessage = new Stream(301, "Download Started!");
        myDataSocket.sendMessage(responseStreamMessage);

        msgListOfUser = allMessagesList.get(usernameString);
        if (msgListOfUser == null || msgListOfUser.isEmpty()) {
            responseStreamMessage = new Stream(302, "There are no messages to download!");
        } else {
            for (String msg : msgListOfUser) {
                responseStreamMessage = new Stream(301, msg);
                myDataSocket.sendMessage(responseStreamMessage);
            }
            responseStreamMessage = new Stream(303, "Download Completed!");
        }

        myDataSocket.sendMessage(responseStreamMessage);
    }
} // end class
