package quiz.server;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;

public class DatabaseThread extends Thread {
    SharedResource sharedResource;
    Statement statement;
    String instruction;

    public DatabaseThread(SharedResource sharedResource){
        super();
        this.sharedResource = sharedResource;
    }

    public void run(){
        if(DatabaseConnection.connectToDatabase()){
            instruction = "";
            try{
                statement = DatabaseConnection.connection.createStatement();
                System.out.println("Connected to database");
            } catch (Exception e){
                System.err.println(e);
            }
            do{
                    instruction = sharedResource.readInstruction();
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
