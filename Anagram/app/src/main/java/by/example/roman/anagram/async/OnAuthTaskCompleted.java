package by.example.roman.anagram.async;

import java.util.List;

import by.example.roman.anagram.QuestionAnswerPair;
import by.example.roman.anagram.User;

public interface OnAuthTaskCompleted {
    void onRegistrationCompleted(List<User> values);
    void onSignInCompleted(List<User> values);

}