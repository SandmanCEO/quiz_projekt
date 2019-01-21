package quiz.server;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainServer extends Application {

    Stage window;
    Scene getDbParameters, serverDashboard, queryTittle, queryQuestion, queryAnswer, chartScene;
    ChoiceBox<String> chooseQuery;
    static Connection connection = null;
    Statement statement;
    ObservableList<PieChart.Data> chartData;
    PieChart chart;
    int idQuery =  0;
    int noOfQuestions = 0;
    int noOfAnswers =  0;
    int actualQuestion = 0;

    void showChart(){
        try {
            setChartData();
            ResultSet resultSet = statement.executeQuery("select question_text from question where query_id = " +
                    idQuery + " and question_no = " + actualQuestion + ";");
            resultSet.next();
            Stage stage = new Stage();
            Scene scene = new Scene(new Group());
            stage.setTitle("Wykres");
            stage.setWidth(500);
            stage.setHeight(500);

            final PieChart chart = new PieChart(chartData);
            chart.setTitle(resultSet.getString(1));

            ((Group) scene.getRoot()).getChildren().add(chart);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            System.err.println(e);
        }
    }

    public void setChartScene(){
        Button  continueQuery = new Button("Kontynuuj");

        continueQuery.setOnAction( event -> {
            if(actualQuestion < noOfQuestions) {
                actualQuestion += 1;
                showChart();
            } else {
                actualQuestion = 0;
                idQuery = 0;
                noOfQuestions = 0;
                window.setScene(serverDashboard);
            }
        });

        VBox chartSceneLayout = new VBox( 10);
        chartSceneLayout.setPadding(new Insets(20,20, 20, 20));
        chartSceneLayout.getChildren().addAll( continueQuery);
        chartScene = new Scene(chartSceneLayout, 600, 400);

    }

    public void setChartData(){
        try{
            chartData = FXCollections.observableArrayList();
            ResultSet resultSet = statement.executeQuery("select user_answer.answer_text, COUNT(user_answer.answer_text) from " +
                    "user_answer where user_answer.question_no = " + actualQuestion + " and user_answer.query_id = " +
                    idQuery + " GROUP by user_answer.answer_text");
            while (resultSet.next()){
                chartData.add(new PieChart.Data(resultSet.getString(1), resultSet.getInt(2)));
            }
            chart = new PieChart(chartData);
        } catch (Exception e){
            System.err.println(e);
        }
    }

    public void setQueryAnswer(){
        Text answerText = new Text("Podaj treść odpowiedzi: ");
        TextField answerInput = new TextField();
        Text invalidText = new Text("");
        invalidText.setFill(Color.RED);
        Button nextAnswer = new Button("Następna odpowiedź");
        Button nextQuestion = new Button("Następne pytanie");
        Button finishQuery = new Button("Zakończ ankietę");

        nextAnswer.setOnAction( answerEvent -> {
            if(answerInput.equals("")){
                invalidText.setText("Treść odpowiedzi nie może być pusta!");
            } else {
                try {
                    noOfAnswers += 1;
                    statement.executeUpdate("insert into answers(query_id, question_no, answer_text, answer_no) values(" +
                            idQuery + ", " + noOfQuestions + ", \"" + answerInput.getText() + "\", " +
                            noOfAnswers + ");");
                    invalidText.setText("");
                    answerInput.clear();
                } catch (Exception e){
                    System.err.println(e);
                }
            }
        });

        nextQuestion.setOnAction( questionEvent -> {
            if(answerInput.equals("")){
                invalidText.setText("Treść odpowiedzi nie może być pusta!");
            } else {
                try {
                    noOfAnswers += 1;
                    statement.executeUpdate("insert into answers(query_id, question_no, answer_text, answer_no) values(" +
                            idQuery + ", " + noOfQuestions + ", \"" + answerInput.getText() + "\", " +
                            noOfAnswers + ");");
                    statement.executeUpdate("update question set answers_no = " + noOfAnswers + " where query_id = " + idQuery  + ";");
                    noOfAnswers = 0;
                    invalidText.setText("");
                    answerInput.clear();
                    window.setScene(queryQuestion);
                } catch (Exception e){
                    System.err.println(e);
                }
            }
        });

        finishQuery.setOnAction( finishEvent -> {
            if(answerInput.equals("")){
                invalidText.setText("Treść odpowiedzi nie może być pusta!");
            } else {
                try {
                    noOfAnswers += 1;
                    statement.executeUpdate("insert into answers(query_id, question_no, answer_text, answer_no) values(" +
                            idQuery + ", " + noOfQuestions + ", \"" + answerInput.getText() + "\", " +
                            noOfAnswers + ");");
                    statement.executeUpdate("update question set answers_no = " + noOfAnswers + " where query_id = " + idQuery  + ";");
                    noOfAnswers = 0;
                    statement.executeUpdate("update query set no_questions = " + noOfQuestions + " where id = " + idQuery  + ";");
                    noOfQuestions = 0;
                    invalidText.setText("");
                    answerInput.clear();
                    window.setScene(serverDashboard);
                } catch (Exception e){
                    System.err.println(e);
                }
            }
        });


        VBox queryAnswerLayout = new VBox(10);
        queryAnswerLayout.setPadding(new Insets(20, 20, 20, 20));
        queryAnswerLayout.getChildren().addAll(answerText, answerInput, invalidText, nextAnswer, nextQuestion, finishQuery);

        queryAnswer = new Scene(queryAnswerLayout, 600, 400);
    }

    public void setQueryQuestion(){
        Text questionText = new Text("Podaj treść pytania: ");
        TextField questionInput = new TextField();
        Text invalidText = new Text("");
        invalidText.setFill(Color.RED);
        Button accept = new Button("Zatwierdź");

        accept.setOnAction(event ->{
            if(questionInput.equals("")){
                invalidText.setText("Treść pytania nie może być pusta!");
            } else {
                try {
                    noOfQuestions += 1;
                    statement.executeUpdate("insert into question(query_id, question_text, question_no, answers_no) " +
                            "values( "+ idQuery + ", \"" + questionInput.getText() + "\", " + noOfQuestions +
                            ", " + noOfAnswers +");");
                    invalidText.setText("");
                    questionInput.clear();
                    window.setScene(queryAnswer);
                }catch (Exception e){
                    System.err.println(e);
                }
            }
        });

        VBox queryQuestionLayout = new VBox(10);
        queryQuestionLayout.setPadding(new Insets(20, 20, 20, 20));
        queryQuestionLayout.getChildren().addAll(questionText, questionInput, invalidText, accept);

        queryQuestion = new Scene(queryQuestionLayout, 600, 400);
    }

    public  void setQueryTittle(){
        Text queryTittleText = new Text("Podaj tytuł ankiety: ");
        Text invalidTittle = new Text("");
        invalidTittle.setFill(Color.RED);
        TextField queryTittleInput = new TextField();
        Button accept = new Button("Zatwierdź");
        Button goBack = new Button("Cofnij");

        goBack.setOnAction( goBackEvent -> {
            window.setScene(serverDashboard);
        });

        accept.setOnAction( event  -> {
            if(queryTittleInput.equals("")){
                invalidTittle.setText("Podaj nazwę!");
            } else {
                try{
                    ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM query;");
                    resultSet.next();
                    idQuery = resultSet.getInt(1) +  1;
                    statement.executeUpdate("INSERT INTO query(id, tittle, no_questions) values(" + idQuery + ", \"" +
                            queryTittleInput.getText() + "\", 0);");

                    window.setScene(queryQuestion);
                } catch (Exception e){
                    System.err.println(e);
                }
            }
        });

        VBox queryTittleLayout = new VBox(10);
        queryTittleLayout.setPadding(new Insets(20, 20, 20, 20));
        queryTittleLayout.getChildren().addAll(queryTittleText, queryTittleInput, invalidTittle, accept);
        queryTittle =  new Scene(queryTittleLayout, 600, 400);
    }

    public void setServerDashboard(){
        Text chooseQueryText =  new Text("Wybierz ankietę");
        chooseQuery = new ChoiceBox<>();

        Button accept = new Button("Wybierz");
        Button create = new Button("Utwórz ankietę");
        Button exit = new Button("Wyjdź");
        Button refresh = new Button("Odśwież");

        refresh.setOnAction( refreshEvent -> {
            getQueries(chooseQuery);
            window.setScene(serverDashboard);
        });

        exit.setOnAction( e -> window.close());

        create.setOnAction( e -> {
            window.setScene(queryTittle);
        });

        accept.setOnAction( acceptEvent ->{
            try {
                ResultSet iDQueryResult = statement.executeQuery("select id, no_questions from query where tittle = \"" + chooseQuery.getValue() +
                        "\";");
                iDQueryResult.next();
                idQuery = iDQueryResult.getInt(1);
                noOfQuestions = iDQueryResult.getInt(2);
                actualQuestion += 1;
                if(actualQuestion < noOfQuestions){
                    showChart();
                    window.setScene(chartScene);
                }
            }catch (Exception e){
                System.err.println(e);
            }
        });

        VBox serverDashboardlayout =  new VBox(10);
        serverDashboardlayout.setPadding(new Insets(20, 20, 20, 20));
        serverDashboardlayout.getChildren().addAll(chooseQueryText, chooseQuery, accept, create, exit);

        serverDashboard = new Scene(serverDashboardlayout, 600, 400);
    }

    public void setGetDbParameters(){
        Text insertDbIp = new Text("Podaj adres IP serwera bazy danych");
        Text insertDbName = new Text("Podaj nazwę bazy danych");
        Text insertDbLogin = new Text("Podaj login bazy danych");
        Text insertDbPassword = new Text("Podaj hasło bazy danych");

        TextField DbIp = new TextField();
        TextField DbName = new TextField();
        TextField DbLogin = new TextField();
        TextField DbPassword = new TextField();

        Button connect = new Button("Połącz");
        connect.setOnAction( event -> {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                connection = DriverManager.getConnection("jdbc:mysql://" + DbIp.getText() + "/" + DbName.getText() ,
                        DbLogin.getText(), DbPassword.getText());
                new ServerSocketThread(connection).start();
                statement = connection.createStatement();
                getQueries(chooseQuery);
                window.setScene(serverDashboard);
            } catch (Exception e){
                System.err.println(e);
                createDbBackup();
            }
        });

        VBox getDbParametersLayout = new VBox(10);
        getDbParametersLayout.setPadding(new Insets(20, 20, 20, 20));
        getDbParametersLayout.getChildren().addAll(insertDbIp, DbIp, insertDbName, DbName, insertDbLogin, DbLogin,
                insertDbPassword, DbPassword, connect);
        getDbParameters = new Scene(getDbParametersLayout, 600, 400);
    }

    public void createDbBackup(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "");
            new ServerSocketThread(connection).start();
            statement = connection.createStatement();
            statement.executeUpdate("CREATE database quiz; ");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/quiz", "root", "");
            statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE `answers` " +
                    "  (`query_id` int(11) NOT NULL, " +
                    "  `question_no` int(11) NOT NULL," +
                    "  `answer_text` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL," +
                    "  `answer_no` int(11) NOT NULL);");
            statement.executeUpdate("CREATE TABLE `query` " +
                    "  (`id` int(11) NOT NULL," +
                    "  `tittle` varchar(30) COLLATE utf8_polish_ci NOT NULL," +
                    "  `no_questions` int(11) NOT NULL)" +
                    " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;");
            statement.executeUpdate("CREATE TABLE `question` " +
                    "  (`query_id` int(11) NOT NULL," +
                    "  `question_text` varchar(100) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL," +
                    "  `question_no` int(11) NOT NULL," +
                    "  `answers_no` int(11) NOT NULL)" +
                    " ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            statement.executeUpdate("CREATE TABLE `user` " +
                    "  (`login` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL," +
                    "  `password` varchar(30) COLLATE utf16_polish_ci NOT NULL)" +
                    " ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;");
            statement.executeUpdate("CREATE TABLE `user_answer` " +
                    "  (`user_login` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL," +
                    "  `query_id` int(11) NOT NULL," +
                    "  `question_no` int(11) NOT NULL," +
                    "  `answer_text` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL)" +
                    " ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            statement.executeUpdate("CREATE TABLE `user_query` " +
                    "  (`user_login` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL," +
                    "  `query_id` int(11) NOT NULL)" +
                    " ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            getQueries(chooseQuery);
            window.setScene(serverDashboard);
        } catch (Exception e){
            System.err.println(e);
        }
    }

    public  void getQueries(ChoiceBox<String> list){
        try{
            ResultSet resultSet = statement.executeQuery("SELECT * FROM query;");

            while (resultSet.next()){
                list.getItems().add(resultSet.getString("tittle"));
            }
        } catch (Exception e){
            System.err.println(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("QuizServer");

        setGetDbParameters();
        setServerDashboard();
        setQueryTittle();
        setQueryQuestion();
        setQueryAnswer();
        setChartScene();

        window.setScene(getDbParameters);
        window.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
