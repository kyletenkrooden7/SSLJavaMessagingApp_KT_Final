package Server;

import Other.MyStreamSocket;

import javax.net.ssl.*;

/**
 * This module contains the application logic of an echo server
 * which uses a stream-mode socket for interprocess communication.
 * Unlike EchoServer2, this server services clients concurrently.
 * A command-line argument is required to specify the server port.
 * 
 * @author M. L. Liu
 */

public class SslConcurrentServer {

    public static void main(String[] args) {
        final int serverPort = 7;
        // keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -keystore
        // server.jks
        System.setProperty("javax.net.ssl.keyStore", "server.jks");
        // specifing the password of the keystore file
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        System.setProperty("javax.net.debug", "all");
        try {

            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
                    .getDefault();

            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(serverPort);

            System.out.println("The server has successfully been launched!");

            /* Infinite Loop Waiting For Connections */
            /* Concurrent Server */
            while (true) {

                System.out.println("Waiting for a connection from the ClientGUI");

                MyStreamSocket myDataSocket = new MyStreamSocket((SSLSocket) sslServerSocket.accept());
                System.out.println(
                        "The connection has successfully been accepted from :"
                                + sslServerSocket.getLocalSocketAddress());

                // Start a thread to handle this client's sesson
                /* Separation Of Logic */
                Thread thread = new Thread(new SslServerThread(myDataSocket));
                thread.start();
                // navigate to next client
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
