package by.example.roman.anagram;

/**
 * Created by Roman on 25.02.2016.
 */
public class Duel {
    String question;
    String answer;
    String opponentLogin;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOpponentLogin() {
        return opponentLogin;
    }

    public void setOpponentLogin(String opponentLogin) {
        this.opponentLogin = opponentLogin;
    }

    public Duel(String question, String answer, String opponentLogin) {

        this.question = question;
        this.answer = answer;
        this.opponentLogin = opponentLogin;
    }
}
