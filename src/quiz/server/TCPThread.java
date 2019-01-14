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


            sharedResource.setInstruction("INSERT INTO user(login, password ) VALUES( \"" + login + "\", \"" + password +
                    "\");");

            sleep(100);
            sharedResource.setLock();

        } catch (Exception e){
            System.err.println(e);
        }


    }

    void login(){
        String login, password;
        try{
            login = in.readLine();
            password = in.readLine();

            sharedResource.setInstruction("SELECT COUNT(*) AS no FROM user WHERE login = \"" + login + "\" AND password = \"" +
                    password + "\";");

            sleep(100);

            ResultSet result = sharedResource.readResultSet();
            result.next();

            if (result.getInt("no") == 1){
                outStream.println("logged");
            } else{
                outStream.println("invalidLogin");
            }

            outStream.flush();

        } catch (Exception e){
            System.err.println(e);
        }
    }

    public void getQueries(){
        try{
            String login = in.readLine();
            sharedResource.setInstruction("SELECT * FROM query WHERE id NOT IN (SELECT query_id FROM user_query " +
                    "WHERE user_login = \"" + login +"\");");
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

    public void getStatistics(){
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

    void getNoOfQuestionsAndQueryId(){
        String tittle = "";
        try{
            tittle = in.readLine();
            sharedResource.setInstruction("SELECT * from query where tittle = \"" + tittle + "\";");
            sleep(100);
            ResultSet result = sharedResource.readResultSet();
            result.next();
            outStream.println(result.getInt("id"));
            outStream.println(result.getInt("no_questions"));
            outStream.flush();
        } catch (Exception e){
            System.err.println(e);
        }
    }

    void getQuestionAndAnswers(){
        String  tittle = "";
        String question;
        int questionNo;
        try{
            tittle = in.readLine();
            questionNo = Integer.parseInt(in.readLine());
            sharedResource.setInstruction("SELECT * from question where query_id in (select id from query where " +
                    "tittle = \"" + tittle + "\") and question_no = " + questionNo + ";");
            sleep(500);
            ResultSet questionResult = sharedResource.readResultSet();
            questionResult.next();
            question = questionResult.getString("question_text");
            sleep(600);
            outStream.println(question);
            outStream.flush();
            sharedResource.setInstruction("SELECT * from answers where query_id in (select id from query where " +
                    "tittle = \"" + tittle + "\") and question_no = " + questionNo + " order by answer_no;");
            sleep(100);
            ResultSet answerResult = sharedResource.readResultSet();
            while(answerResult.next()){
                outStream.println(answerResult.getString("answer_text"));
            }
            outStream.println("end");
            outStream.flush();
        } catch (Exception e){
            System.err.println(e);
        }
    }

    public void saveAnswer(){
        String answerText = "";
        String userLogin = "";
        int queryId, questionNumber;
        queryId = questionNumber = 0;
        try{
            userLogin = in.readLine();
            queryId = Integer.parseInt(in.readLine());
            questionNumber = Integer.parseInt(in.readLine());
            answerText  = in.readLine();

            sharedResource.setInstruction("INSERT INTO user_answer(user_login, query_id, question_no, answer_text) " +
                    "values(\"" + userLogin + "\", " + queryId + ", " + questionNumber + ", \"" + answerText + "\");");
            sleep(100);
            sharedResource.setLock();
        } catch (Exception e){

        }
    }

    public void setQueryAsDone(){
        String userLogin = "";
        int queryId = 0;
        try{

            userLogin = in.readLine();
            queryId = Integer.parseInt(in.readLine());
            sleep(500);
            sharedResource.setInstruction("INSERT INTO user_query(user_login, query_id) values(\"" + userLogin +
                    "\", " + queryId + ");");
            sleep(500);
            sharedResource.setLock();
        }catch (Exception e){
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
                System.out.println(instruction);

                if ("register".equals(instruction)) {
                    register();
                } else if ("login".equals(instruction)) {
                    login();
                } else if("getQueries".equals(instruction)){
                    getQueries();
                } else if("getStatistics".equals(instruction)){
                    getStatistics();
                } else if("getNoOfQuestionsAndQueryId".equals(instruction)){
                    getNoOfQuestionsAndQueryId();
                } else if("getQuestionAndAnswers".equals(instruction)){
                    getQuestionAndAnswers();
                } else if("saveAnswer".equals(instruction)){
                    saveAnswer();
                } else if("setQueryAsDone".equals(instruction)){
                    setQueryAsDone();
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
