package quiz.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class ServerSocketThread extends Thread {

    Connection connection;

    public ServerSocketThread(Connection connection) {
        this.connection = connection;
    }

    public void run(){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(4943);


            while(true){
                if(connection != null) {
                    Socket socket = serverSocket.accept();

                    SharedResource sharedResource = new SharedResource();

                    new TCPThread(socket, sharedResource).start();
                    new DatabaseThread(sharedResource, connection).start();
                }
            }
        } catch (Exception e){
            System.err.println(e);
        } finally {
            if(serverSocket != null)
                try{
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
