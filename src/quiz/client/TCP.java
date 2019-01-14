package quiz.client;

import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

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

    public static void getAvailableQueries(ChoiceBox<String> list, String sessionLogin){
        String temporary = "";
        list.getItems().clear();
        try{
            out.println("getQueries");
            out.println(sessionLogin);
            out.flush();

            while(!("end".equals(temporary))){
                temporary = in.readLine();
                if(!temporary.equals("end")) {
                    list.getItems().add(temporary);
                    list.setValue(temporary);
                }
            }

        } catch (Exception e){
            System.err.println(e);
        }
    }

    public static QueryHandler handler(String userLogin, String tittle){
        int queryId, noOfQuestions;
        queryId = noOfQuestions = 0;
        try {
            out.println("getNoOfQuestionsAndQueryId");
            out.println(tittle);
            out.flush();

            queryId = Integer.parseInt(in.readLine());
            noOfQuestions = Integer.parseInt(in.readLine());


        } catch (Exception e){
            System.err.println(e);
        }
        return new QueryHandler(userLogin, queryId, tittle, noOfQuestions);
    }

    public static void prepareQuestion(Text questionText, ChoiceBox<String> answers, String tittle, int questionNo){
        String temporary = "";
        answers.getItems().clear();
        try {
            out.println("getQuestionAndAnswers");
            out.println(tittle);
            out.println(questionNo);
            out.flush();
            //System.out.println(temporary);
            questionText.setText(in.readLine());

            while(!("end".equals(temporary))){
                temporary = in.readLine();
                System.out.println(temporary);
                if(!temporary.equals("end")){
                    answers.getItems().add(temporary);
                }
            }
        } catch (Exception e){
            System.err.println(e);
        }
    }

    public static void sendAnswer(String userLogin, int queryId, int questionNumber, String answerText){
        out.println("saveAnswer");
        out.println(userLogin);
        out.println(queryId);
        out.println(questionNumber);
        out.println(answerText);
        out.flush();
    }

    public static void setQueryAsDone(String userLogin, int queryId){
        out.println("setQueryAsDone");
        out.println(userLogin);
        out.println(queryId);
    }
}
