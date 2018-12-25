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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quiz.client.TCP;

import java.util.Stack;

public class Main extends Application {

    Stage window;
    Scene getPort, login, queryResult, registration;
    Button connectButton, loginButton;
    String result;
    Text tittle;

    void login(){
        TextField inputLogin = new TextField();
        TextField inputPassword = new TextField();
        loginButton = new Button("Zaloguj");
        loginButton.setOnAction( e -> {
            TCP.send(inputLogin.getText());
            TCP.send(inputPassword.getText());
            result = TCP.recive();
            tittle.setText(result);
            window.setScene(queryResult);
        });
        Button goToRegistration = new Button("Zarejestruj się!");
        goToRegistration.setOnAction( e -> window.setScene(registration));

        VBox getLoginLayout = new VBox(10);
        getLoginLayout.setPadding(new Insets(20, 20, 20, 20));
        getLoginLayout.getChildren().addAll(inputLogin, inputPassword, loginButton, goToRegistration);

        login = new Scene(getLoginLayout, 300, 250);

    }

    void queryResult(){
        tittle = new Text();


        VBox getQueryResultLayout = new VBox(10);
        getQueryResultLayout.setPadding(new Insets(20, 20, 20, 20));
        getQueryResultLayout.getChildren().add(tittle);

        queryResult = new Scene(getQueryResultLayout, 300, 250);

    }

    void getPort(){
        TextField inputPort = new TextField();
        connectButton = new Button("Połącz");
        connectButton.setOnAction( e -> {
            TCP.connect(Integer.parseInt(inputPort.getText()));
            window.setScene(login);
        });

        VBox getPortLayout = new VBox(10);
        getPortLayout.setPadding(new Insets(20, 20, 20, 20));
        getPortLayout.getChildren().addAll(inputPort, connectButton);


        getPort = new Scene(getPortLayout, 300, 250);

    }

    void register(){
        Text login =  new Text("Login");
        Text password = new Text("Hasło");
        Text type = new Text("Typ konta");

        TextField loginInput = new TextField();
        TextField passwordInput = new TextField();

        ChoiceBox<String> chooseType = new ChoiceBox<>();
        chooseType.getItems().addAll("Ankieter", "Ankietowany");

        Button registerButton = new Button("Zarejestruj");
        registerButton.setOnAction( e -> {
            TCP.send("register");
            TCP.send(loginInput.getText());
            TCP.send(passwordInput.getText());
            TCP.send(chooseType.getValue());
        });

        VBox getRegisterLayout = new VBox(10);
        getRegisterLayout.setPadding(new Insets(20, 20, 20, 20));
        getRegisterLayout.getChildren().addAll(login, loginInput, password, passwordInput, type, chooseType, registerButton);

        registration = new Scene(getRegisterLayout, 300, 400);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("QuizApp");

        getPort();
        login();
        queryResult();
        register();

        window.setScene(getPort);
        window.show();
    }


    public static void main(String[] args){
        launch(args);
    }
}
