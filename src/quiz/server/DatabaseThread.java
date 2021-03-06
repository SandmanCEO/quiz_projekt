package quiz.server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;

public class DatabaseThread extends Thread {
    Connection connection;
    SharedResource sharedResource;
    Statement statement;
    String instruction;

    public DatabaseThread(SharedResource sharedResource, Connection connection){
        super();
        this.sharedResource = sharedResource;
        this.connection = connection;
    }

    public void run(){
        if(DatabaseConnection.connectToDatabase()){
            instruction = "";
            try{
                statement = connection.createStatement();
                System.out.println("Connected to database");
            } catch (Exception e){
                System.err.println(e);
            }
            do{
                    instruction = sharedResource.readInstruction();
                    System.out.println(instruction);
                    try {
                        if (instruction.startsWith("INSERT")) {
                            statement.executeUpdate(instruction);
                            sharedResource.setInstruction("");
                        } else if (instruction.startsWith("SELECT")) {
                            sharedResource.setResultSet(statement.executeQuery(instruction));
                        }
                        sleep(500);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
            }while(!instruction.equals("exit"));
        }
        System.out.println("Closing thread Database");
    }

}
