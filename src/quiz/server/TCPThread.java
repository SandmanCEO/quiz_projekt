package quiz.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.security.MessageDigest;

public class TCPThread extends Thread {
    Socket socket;
    String login, password;
    BufferedReader in;
    PrintWriter outStream;
    Statement statement;

    public TCPThread(Socket socket){
        super();
        this.socket = socket;
    }

    void register(){
        String login, password, type, query, md5Password;
        query = "INSERT INTO user(login, password , type) VALUES( ?, ?, ?)";

        try {
            PreparedStatement insertQuery = DatabaseConnection.connection.prepareStatement(query);

            login = in.readLine();
            password = in.readLine();
            type = in.readLine();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5Password = md.digest(password.getBytes()).toString();

            insertQuery.setString(1, login);
            insertQuery.setString(2, md5Password);
            insertQuery.setString(3, type);

            insertQuery.execute();
        } catch (Exception e){
            System.err.println(e);
        }


    }

    public void run(){
        if(DatabaseConnection.connectToDatabase())
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                statement = DatabaseConnection.connection.createStatement();

                do{
                    if("register".equals(in.readLine())){
                        register();
                    }
                } while("exit".equals(in.readLine()));

                socket.close();

            } catch (Exception e){
                System.err.println(e);
            }
    }
}
