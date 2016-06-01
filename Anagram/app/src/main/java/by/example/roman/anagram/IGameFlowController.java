package by.example.roman.anagram;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by Roman on 11.02.2016.
 */
public interface IGameFlowController {
    void setQuestionAndAnswer(String question,String answer);

    void numbersRandomise(String answer);

    void charactersRandomise(String answer);

    void onClickAction(View v);

    int getRandom(ArrayList<Integer> List);
}
