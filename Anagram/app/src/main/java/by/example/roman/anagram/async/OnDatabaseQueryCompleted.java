package by.example.roman.anagram.async;

import java.util.List;

import by.example.roman.anagram.QuestionAnswerPair;

/**
 * Created by Roman on 23.02.2016.
 */
public interface OnDatabaseQueryCompleted {

    void numericBasicCompleted(List<QuestionAnswerPair> value);
    void numericChampionCompleted(List<QuestionAnswerPair> value);
    void numericSuperstarCompleted(List<QuestionAnswerPair> value);

    void letterBasicCompleted(List<QuestionAnswerPair> value);
    void letterChampionCompleted(List<QuestionAnswerPair> value);
    void letterSuperstarCompleted(List<QuestionAnswerPair> value);
}
