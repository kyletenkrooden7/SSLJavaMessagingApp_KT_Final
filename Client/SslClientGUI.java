package Client;

import Other.Stream;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * This module contains the presentaton logic of an Echo Client.
 * 
 * @author M. L. Liu
 */
public class SslClientGUI {
   private static JFrame frame;
   private static JLabel systemTitle;
   private static JPanel inputPanel;
   private static JTextField inputMessage;
   private static JPanel responsePanel;
   private static SslClientGUIHelper helper;
   static boolean done = false;
   private static boolean isValid = false;
   private static Stream msg;

   public static void main(String[] args) {

      /**
       * Generate Cert for client
       * keytool -exportcert -alias server -keystore server.jks -file server.cer
       * 
       * Import cert CLI
       * keytool -importcert -alias server -keystore TrustStore.jts -file server.cer
       **/

      System.setProperty("javax.net.ssl.trustStore", "TrustStore.jts");
      // specifing the password of the trustStore file upon creation
      System.setProperty("javax.net.ssl.trustStorePassword", "password");
      System.setProperty("javax.net.debug", "all");

      frame = new JFrame("Client Graphical User Interface");
      frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
      frame.setBackground(Color.gray);
      frame.setSize(600, 500);
      frame.setLayout(new FlowLayout());
      frame.setResizable(false);
      frame.getContentPane().setBackground(Color.gray);

      frame.setLocationRelativeTo(null);

      systemTitle = new JLabel("Welcome To The Messaging System!");
      systemTitle.setForeground(Color.white);
      systemTitle.setFont(new Font("Arial", Font.BOLD, 23));
      systemTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
      systemTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

      frame.add(systemTitle);

      JButton logOffButton = new JButton("Log Off");
      logOffButton.setPreferredSize(new Dimension(100, 30));
      logOffButton.setFont(new Font("Arial", Font.PLAIN, 8));
      logOffButton.setBackground(Color.red);
      logOffButton.setForeground(Color.white);
      logOffButton.setOpaque(true);

      frame.add(logOffButton);

      inputPanel = new JPanel();
      inputPanel.setLayout(new BorderLayout());
      inputPanel.setAutoscrolls(done);
      inputPanel.add(new JLabel("Please Input a message: "), BorderLayout.NORTH);

      inputMessage = new JTextField(62);
      inputMessage.setPreferredSize(new Dimension(500, 50));
      inputMessage.setFont(new Font("Arial", Font.PLAIN, 10));
      inputMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

      inputPanel.add(inputMessage, BorderLayout.CENTER);
      frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

      responsePanel = new JPanel();
      responsePanel.setLayout(new BorderLayout());
      responsePanel.add(new JLabel("Server Response: "), BorderLayout.NORTH);

      JTextArea outputArea = new JTextArea(10, 62);

      JScrollPane scrollPane = new JScrollPane(outputArea);

      responsePanel.add(scrollPane, BorderLayout.CENTER);

      frame.getContentPane().add(responsePanel, BorderLayout.SOUTH);

      JButton uploadButton = new JButton("Send Message");
      uploadButton.setPreferredSize(new Dimension(180, 30));
      uploadButton.setFont(new Font("Arial", Font.PLAIN, 12));
      uploadButton.setBackground(Color.GREEN);

      JPanel buttonPanel = new JPanel();
      buttonPanel.setBorder(new EmptyBorder(50, 90, 10, 90));

      JButton downloadButton = new JButton("Download all Messages");
      downloadButton.setPreferredSize(new Dimension(180, 30));
      downloadButton.setFont(new Font("Arial", Font.PLAIN, 12));
      downloadButton.setBackground(Color.magenta);

      buttonPanel.add(uploadButton);
      buttonPanel.add(downloadButton);

      frame.add(buttonPanel);

      uploadButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String message = inputMessage.getText();
            if (message.equals("")) {
               return;
            }

            try {
               Stream streamMsg = new Stream(200, message);
               Stream msgResponse = helper.getMsg(streamMsg);

               if (msgResponse.getMsgCode() == 201) {
                  outputArea.append("\nMessage Sent: " + message
                        + "\n" + msgResponse.getMessage());
                  inputMessage.setText("");
               } else {
                  outputArea.append("\nError!: " + msgResponse.getMessage());
               }
            } catch (IOException | ClassNotFoundException ex) {
               ex.printStackTrace();
            }
         }
      });

      downloadButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            try {
               Stream streamMsg = new Stream(300, "download");
               Stream msgResponse = helper.getMsg(streamMsg);

               if (msgResponse.getMsgCode() == 301) {
                  outputArea.append("All Downloaded Messages: \n");

                  boolean containsMessage = true;
                  while (containsMessage) {
                     msgResponse = helper.recieveMsg();
                     if (msgResponse.getMsgCode() == 301) {
                        outputArea.append(msgResponse.getMessage() + "\n");
                     } else {
                        containsMessage = false;
                     }
                  }
               } else if (msgResponse.getMsgCode() == 302) {
                  outputArea.append("There are no messages to download: \n");
               }
            } catch (IOException | ClassNotFoundException ex) {
               ex.printStackTrace();
            }
         }
      });

      logOffButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            try {
               helper.done();
               isValid = false;
               frame.setVisible(false);
               SslClientGUI.main(null);
            } catch (IOException ex) {
               ex.printStackTrace();
            }
         }
      });

      try {
         String hostName = "localhost";
         outputArea.append("Welcome to the " + hostName
               + " Messaging System. \n All responses from the server will be displayed here! .\n");
         String portNum = "7";
         helper = new SslClientGUIHelper(hostName, portNum);
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      while (!isValid) {
         displayLogin();
         if (isValid)
            break;
         else {
            int result = JOptionPane.showConfirmDialog(
                  null, "Would you like to try logging in again ?",
                  "Error!", JOptionPane.YES_NO_OPTION);
            if (result != 0)
               break;
         }
      }
      if (isValid)
         frame.setVisible(true);
      else
         System.exit(0);
   } // end main

   private static void displayLogin() {

      JPanel panel = new JPanel(new GridLayout(4, 2, 6, 6));
      panel.setBackground(Color.gray);
      panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      JLabel userLabel = new JLabel("Username:", SwingConstants.CENTER);
      userLabel.setForeground(new Color(255, 255, 255));
      panel.add(userLabel);
      JTextField usernameField = new JTextField(15);
      usernameField.setForeground(new Color(30, 30, 30));
      panel.add(usernameField);
      JLabel passwordLabel = new JLabel("Password:", SwingConstants.CENTER);
      passwordLabel.setForeground(new Color(255, 255, 255));
      panel.add(passwordLabel);
      JPasswordField passwordField = new JPasswordField(15);
      passwordField.setForeground(new Color(60, 60, 60));
      panel.add(passwordField);

      int option = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Log In",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

      if (option == JOptionPane.OK_OPTION) {
         String username = usernameField.getText().trim();
         String password = new String(passwordField.getPassword()).trim();

         Stream packet = new Stream(100, username + " " + password);

         try {
            Stream response = helper.getMsg(packet);
            if (response.getMsgCode() == 101) {
               JOptionPane.showMessageDialog(
                     null,
                     response.getMessage(),
                     "Login",
                     JOptionPane.PLAIN_MESSAGE);
               isValid = true;
            } else {
               JOptionPane.showMessageDialog(
                     null,
                     response.getMessage(),
                     "Login",
                     JOptionPane.ERROR_MESSAGE);
            }
         } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
         }
      }
   }

} // end class
