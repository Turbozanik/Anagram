package by.example.roman.anagram.async;

import java.util.List;

import by.example.roman.anagram.User;

/**
 * Created by Roman on 23.02.2016.
 */
public interface UserUpdateCompleted {
    void dataSent(List<User> values);
}
