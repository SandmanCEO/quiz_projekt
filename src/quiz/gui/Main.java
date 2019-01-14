package quiz.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quiz.client.QueryHandler;
import quiz.client.TCP;

import java.util.Stack;

public class Main extends Application {

    Stage window;
    Scene getPort, login, showAvailableQueries, registration, showQuestion;
    String result, userLogin;
    ChoiceBox<String> chooseQuery, chooseStat, chooseAnswer;
    String sessionLogin, sessionPassword, sessionType;
    Text questionText;
    QueryHandler handler;

    boolean validate(String text){
        if(text == null || text.equals(""))
            return false;
        else
            return true;
    }

    void login(){
        TextField inputLogin = new TextField();
        TextField inputPassword = new TextField();
        Text inputLog = new Text("Podaj login:");
        Text inputPass = new Text("Podaj hasło:");
        Button exit = new Button("Wyjście");
        exit.setOnAction( e -> {
            TCP.send("exit");
            window.close();
        } );


        Text invalidLogin =  new Text();
        invalidLogin.setFill(Color.RED);
        Button loginButton = new Button("Zaloguj");
        loginButton.setOnAction( e -> {
            if(validate(inputLogin.getText()) && validate(inputPassword.getText())) {
                TCP.send("login");
                sessionLogin =  inputLogin.getText();
                sessionPassword = inputPassword.getText();
                TCP.send(sessionLogin);
                TCP.send(sessionPassword);
                result = TCP.recive();
                System.out.println(result);
                if(result.equals("logged")){
                    System.out.println("wchodze w widok ankietowanego");
                    userLogin = inputLogin.getText();
                    TCP.getAvailableQueries(chooseQuery, sessionLogin);
                    window.setScene(showAvailableQueries);
                }else if(result.equals("invalidLogin")){
                    invalidLogin.setText("Blędny login lub hasło");
                }
                } else
                    invalidLogin.setText("Nieprawidłowe dane");
        });
        Button goToRegistration = new Button("Zarejestruj się!");
        goToRegistration.setOnAction( e -> window.setScene(registration));

        VBox getLoginLayout = new VBox(10);
        getLoginLayout.setPadding(new Insets(20, 20, 20, 20));
        getLoginLayout.getChildren().addAll(inputLog, inputLogin, inputPass, inputPassword, invalidLogin, loginButton, goToRegistration, exit);

        login = new Scene(getLoginLayout, 600, 400);

    }


    void showAvailableQueries(){
        Button exit = new Button("Wyjście");
        Button refresh = new Button("Odśwież");
        Button pickQuery = new Button("Wybierz ankietę");
        Text queries = new Text("Dostępne ankiety:");
        chooseQuery = new ChoiceBox<>();
        exit.setOnAction( e -> {
            TCP.send("exit");
            window.close();
        } );
        refresh.setOnAction( e -> {
            TCP.getAvailableQueries(chooseQuery, sessionLogin);
            window.setScene(showAvailableQueries);
        });
        pickQuery.setOnAction( e -> {
            handler =  TCP.handler(userLogin, chooseQuery.getValue());
            TCP.prepareQuestion(questionText, chooseAnswer, handler.getQueryTittle(), handler.getActualQuestion());
            window.setScene(showQuestion);
        });

        VBox getShowAvailableQueries = new VBox(10);
        getShowAvailableQueries.setPadding(new Insets(20, 20, 20, 20));
        getShowAvailableQueries.getChildren().addAll(refresh, queries, chooseQuery, pickQuery, exit);

        showAvailableQueries = new Scene(getShowAvailableQueries, 600, 400);
    }


    void showQuestion(){

        questionText = new Text();
        chooseAnswer = new ChoiceBox<>();
        Text invalidAnswer = new Text();
        invalidAnswer.setFill(Color.RED);

        Button sendAnswer = new Button("Zatwierdź");
        sendAnswer.setOnAction( e -> {
            if(validate(chooseAnswer.getValue())) {
                sendAnswer.setDisable(false);
                invalidAnswer.setText("");
                TCP.sendAnswer(userLogin, handler.getQueryId(), handler.getActualQuestion(), chooseAnswer.getValue());

                if (handler.getActualQuestion() < handler.getNoOfQuestions()) {
                    handler.setActualQuestion(handler.getActualQuestion() + 1);
                    TCP.prepareQuestion(questionText, chooseAnswer, handler.getQueryTittle(), handler.getActualQuestion());
                    window.setScene(showQuestion);
                }else{
                    TCP.setQueryAsDone(sessionLogin, handler.getQueryId());
                    window.setScene(showAvailableQueries);
                }
            }else{
                invalidAnswer.setText("Wybierz odpowiedź!");
            }
        });

        VBox getQuestionLayout = new VBox(10);
        getQuestionLayout.setPadding(new Insets(20, 20, 20, 20));
        getQuestionLayout.getChildren().addAll(questionText, chooseAnswer, invalidAnswer, sendAnswer);

        showQuestion = new Scene(getQuestionLayout, 600, 400);
    }

    void getPort(){
        Text portText = new Text("Podaj numer portu serwera");
        TextField inputPort = new TextField();
        Button exit = new Button("Wyjście");
        exit.setOnAction( e -> {
            window.close();
        } );
        Button connectButton = new Button("Połącz");
        connectButton.setOnAction( e -> {
            TCP.connect(Integer.parseInt(inputPort.getText()));
            window.setScene(login);
        });

        VBox getPortLayout = new VBox(10);
        getPortLayout.setPadding(new Insets(20, 20, 20, 20));
        getPortLayout.getChildren().addAll(portText, inputPort, connectButton, exit);


        getPort = new Scene(getPortLayout, 600, 400);

    }

    void register(){
        Button exit = new Button("Wyjście");
        exit.setOnAction( e -> {
            TCP.send("exit");
            window.close();
        } );
        Text loginText =  new Text("Login");
        Text passwordText = new Text("Hasło");

        Text invalidRegister = new Text();
        invalidRegister.setFill(Color.RED);

        TextField loginInput = new TextField();
        TextField passwordInput = new TextField();

        Button goBack = new Button("Powrót");



        Button registerButton = new Button("Zarejestruj");
        registerButton.setOnAction( e -> {
            if((validate(loginInput.getText()) && validate(passwordInput.getText()))) {
                TCP.send("register");
                TCP.send(loginInput.getText());
                TCP.send(passwordInput.getText());
                window.setScene(login);
            } else{
                invalidRegister.setText("Nieprawidłowe dane");
            }
        });

        goBack.setOnAction( e -> window.setScene(login));

        VBox getRegisterLayout = new VBox(10);
        getRegisterLayout.setPadding(new Insets(20, 20, 20, 20));
        getRegisterLayout.getChildren().addAll(loginText, loginInput, passwordText, passwordInput,
                invalidRegister, registerButton, goBack);

        registration = new Scene(getRegisterLayout, 600, 400);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("QuizApp");




        getPort();
        login();
        showAvailableQueries();
        showQuestion();
        register();

        window.setScene(getPort);
        window.show();
    }


    public static void main(String[] args){
        launch(args);
    }
}
