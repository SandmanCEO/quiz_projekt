package quiz.client;

public class QueryHandler {
    String userLogin;
    int queryId;
    String queryTittle;
    int noOfQuestions;
    int actualQuestion;

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public QueryHandler(String userLogin, int queryId, String queryTittle, int noOfQuestions) {
        this.queryId = queryId;
        this.queryTittle = queryTittle;
        this.noOfQuestions = noOfQuestions;
        this.actualQuestion = 1;
        this.userLogin = userLogin;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public String getQueryTittle() {
        return queryTittle;
    }

    public void setQueryTittle(String queryTittle) {
        this.queryTittle = queryTittle;
    }

    public int getNoOfQuestions() {
        return noOfQuestions;
    }

    public int getActualQuestion() {
        return actualQuestion;
    }

    public void setActualQuestion(int actualQuestion) {
        this.actualQuestion = actualQuestion;
    }

    public void setNoOfQuestions(int actualQuestion) {
        this.noOfQuestions = noOfQuestions;
    }
}
