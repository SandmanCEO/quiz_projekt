package quiz.server;

import java.sql.ResultSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedResource {
    Lock lock;
    String instruction;
    ResultSet resultSet;

    public SharedResource(){
        lock = new ReentrantLock();
        instruction = null;
        resultSet = null;

    }

    public String readInstruction(){
        String temporary = null;
        lock.lock();
        try {
            temporary = instruction;
        } catch (Exception e){
            System.err.println(e);
        }

        return temporary;
    }

    public void setInstruction(String instruction){

        try{
            this.instruction = instruction;
        } catch (Exception e){
            System.err.println(e);
        } finally {
            lock.unlock();
        }
    }

    public ResultSet readResultSet(){
        ResultSet temporary = null;
        lock.lock();
        try{
            temporary = resultSet;
        } catch (Exception e){
            System.err.println(e);
        }
        return temporary;
    }

    public void setResultSet(ResultSet resultSet){
        try{
            this.resultSet = resultSet;
        } catch (Exception e){
            System.err.println(e);
        } finally {
            lock.unlock();
        }
    }


    public void setLock(){ lock.lock(); }

    public void setUnlock(){
        lock.unlock();
    }
}
