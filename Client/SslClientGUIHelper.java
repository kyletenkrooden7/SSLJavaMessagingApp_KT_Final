package Client;

import Other.MyStreamSocket;
import Other.Stream;

import java.net.*;
import java.io.*;

/**
 * This class is a module which provides the application logic
 * for an Echo client using stream-mode socket.
 * 
 * @author M. L. Liu
 */

public class SslClientGUIHelper {
   private MyStreamSocket mySocket;
   private InetAddress serverHost;
   private int serverPort;

   SslClientGUIHelper(String hostName,
         String portNum) throws SocketException,
         UnknownHostException, IOException {

      this.serverHost = InetAddress.getByName(hostName);
      this.serverPort = Integer.parseInt(portNum);
      try {
         this.mySocket = new MyStreamSocket(serverHost, serverPort);
         System.out.println("Connection to server successful! Please input username and password!");

      } catch (Exception ex) {
         ex.printStackTrace();
         System.out
               .println("There has been an issue connecting to the stream socket! Make sure the server is running!");
      }

   }

   public Stream getMsg(Stream message) throws SocketException,
         IOException, ClassNotFoundException {
      Stream streamMessage;
      mySocket.sendMessage(message);
      streamMessage = mySocket.receiveMessage();
      return streamMessage;
   }

   public Stream recieveMsg() throws SocketException,
         IOException, ClassNotFoundException {
      Stream streamMessage;
      streamMessage = mySocket.receiveMessage();
      return streamMessage;
   }

   public void done() throws SocketException,
         IOException {
      final Stream endMessage = new Stream(400, ".");
      mySocket.sendMessage(endMessage);
      mySocket.close();
      System.out.println("Connection terminated!");
   }
}
