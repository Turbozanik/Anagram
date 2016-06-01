package by.example.roman.anagram;


/**
 * Created by Roman on 11.02.2016.
 */
public class QuestionAnswerPair{
    String question;
    String answer;

    public QuestionAnswerPair(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

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
}
