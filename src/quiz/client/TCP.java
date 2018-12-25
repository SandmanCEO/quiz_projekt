package quiz.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TCP {

    static PrintWriter out;
    static BufferedReader in;

    public static void connect(int port){
        try{
            Socket socket = new Socket("localhost", port);

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socket.setTcpNoDelay(true);

        } catch (Exception e){
            System.err.println(e);
        }
    }

    public static void send(String value){
        try {
            out.println(value);
            out.flush();
        } catch (Exception e){
            System.err.println(e);
        }
    }

    public static String recive(){
        String value = "elo wale wiadro";
        try{
            value = in.readLine();
        } catch (Exception e){
            System.err.println(e);
        }
        return value;
    }
}
