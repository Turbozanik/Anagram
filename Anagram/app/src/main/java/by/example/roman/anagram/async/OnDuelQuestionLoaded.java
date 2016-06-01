package by.example.roman.anagram.async;

import java.util.List;

import by.example.roman.anagram.Duel;

/**
 * Created by Roman on 25.02.2016.
 */
public interface OnDuelQuestionLoaded {
    void questionLoadCompleted(List<Duel> value);
}
