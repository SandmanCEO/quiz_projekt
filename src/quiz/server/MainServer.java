package quiz.server;

import java.io.IOException;
import java.net.*;
import java.sql.ResultSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainServer {
    public static void main(String[] args){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(4943);


            while(true){
                Socket socket = serverSocket.accept();

                SharedResource sharedResource = new SharedResource();

                new TCPThread(socket, sharedResource).start();
                new DatabaseThread(sharedResource).start();
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
