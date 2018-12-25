package quiz.server;

import java.sql.*;

public class DatabaseConnection {

    public static Connection connection;

    static boolean connectToDatabase(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.err.println(e);
        }

        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost/quiz", "root", "");
            return true;
        } catch (Exception e){
            System.err.println(e);
            return false;
        }
    }
}
