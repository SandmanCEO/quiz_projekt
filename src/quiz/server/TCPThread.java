package quiz.server;

import javafx.scene.control.ChoiceBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.security.MessageDigest;
import java.util.concurrent.locks.Lock;

public class TCPThread extends Thread {
    Socket socket;
    String instruction;
    SharedResource sharedResource;
    BufferedReader in;
    PrintWriter outStream;

    public TCPThread(Socket socket, SharedResource sharedResource){
        super();
        this.socket = socket;
        this.sharedResource = sharedResource;
    }

    void register(){
        String login, password, type;

        try {
            login = in.readLine();
            password = in.readLine();
            type = in.readLine();

            sharedResource.setInstruction("INSERT INTO user(login, password , type) VALUES( \"" + login + "\", \"" + password +
                    "\", \"" + type + "\");");

            sleep(100);
            sharedResource.setLock();

        } catch (Exception e){
            System.err.println(e);
        }


    }

    boolean login(){
        String login, password;
        try{
            login = in.readLine();
            password = in.readLine();

            sharedResource.setInstruction("SELECT COUNT(*) AS no FROM user WHERE login = \"" + login + "\" AND password = \"" +
                    password + "\";");

            sleep(100);

                ResultSet result = sharedResource.readResultSet();
                result.next();

                if (result.getInt("no") == 1)
                    return true;
                else
                    return false;

        } catch (Exception e){
            System.err.println(e);
        }
        return false;
    }

    public void getQueries(){
        try{
            sharedResource.setInstruction("SELECT * FROM query;");
            String temporary = "";
            sleep(100);
                ResultSet result = sharedResource.readResultSet();

               while (result.next()) {
                   outStream.println(result.getString("tittle"));
                   outStream.flush();
                }
                outStream.println("end");
                outStream.flush();

        } catch (Exception e){
            System.err.println(e);
        }
    }

    public void run() {
        sharedResource.setLock();

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            do {
                instruction = in.readLine();

                if ("register".equals(instruction)) {
                    register();
                } else if ("login".equals(instruction)) {
                    if (login()) {
                        outStream.println("logged");
                        outStream.flush();
                        System.out.println("logged");
                    } else {
                        outStream.println("invalidLogin");
                        outStream.flush();
                        System.out.println("invalid");
                    }
                } else if("getQueries".equals(instruction)){
                    getQueries();
                }
            } while (!"exit".equals(instruction));

            sharedResource.setInstruction("exit");

            socket.close();

        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("Closing thread TCP");
    }
}
