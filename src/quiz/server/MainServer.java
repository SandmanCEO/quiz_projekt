package quiz.server;

import java.io.IOException;
import java.net.*;

public class MainServer {
    public static void main(String[] args){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(4943);


            while(true){
                Socket socket = serverSocket.accept();

                new TCPThread(socket).start();
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
