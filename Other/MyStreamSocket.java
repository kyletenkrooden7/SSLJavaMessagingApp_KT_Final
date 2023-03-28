package Other;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;

/**
 * A wrapper class of Socket which contains
 * methods for sending and receiving messages
 * 
 * @author M. L. Liu
 */
public class MyStreamSocket extends Socket {
   private SSLSocket socket;
   private ObjectInputStream input;
   private ObjectOutputStream output;

   public MyStreamSocket(InetAddress acceptorHost,
         int acceptorPort) throws SocketException,
         IOException {
      SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      socket = (SSLSocket) sslsocketfactory.createSocket(acceptorHost, acceptorPort);
      socket.startHandshake();
      setStreams();

   }

   public MyStreamSocket(SSLSocket socket) throws IOException {
      this.socket = (SSLSocket) socket;
      setStreams();
   }

   private void setStreams() throws IOException {
      output = new ObjectOutputStream(socket.getOutputStream());
      output.flush();
      input = new ObjectInputStream(socket.getInputStream());
   }

   public void sendMessage(Stream message)
         throws IOException {
      output.writeObject(message);
      output.flush();
   }

   public Stream receiveMessage()
         throws IOException, ClassNotFoundException {
      Stream message;
      Object obj = input.readObject();
      message = (Stream) obj;
      return message;

   }

   public void close()
         throws IOException {
      socket.close();
   }
}
