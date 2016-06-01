package by.example.roman.anagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import by.example.roman.anagram.async.UserUpdateCompleted;
import by.example.roman.anagram.async.UserUpdateHelper;

/**
 * Created by Roman on 05.02.2016.
 */
public class RightAnswerSplashFragment extends Fragment implements View.OnClickListener,UserUpdateCompleted{
    String wordsOrNumbers;
    String count;

    FragmentTransaction transaction;

    String basicDifficultyQuestionsCompleted;
    String championDifficultyQuestionsCompleted;
    String superstarDifficultyQuestionsCompleted;
    String difficulty;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    UserUpdateHelper helper;

    ConnectivityManager connManager;
    NetworkInfo mWifi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        View view = inflater.inflate(R.layout.question_compete_splash, container, false);

        helper = new UserUpdateHelper(getActivity());
        helper.delegate = this;

        if (mWifi.isConnected()) {
            helper.execute();
        }

        sharedpreferences = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        transaction = getActivity().getFragmentManager().beginTransaction();

        difficulty = sharedpreferences.getString(UtilityClass.DIFICULTY_LEVEL, getString(R.string.basic));
        basicDifficultyQuestionsCompleted = sharedpreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
        championDifficultyQuestionsCompleted = sharedpreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
        superstarDifficultyQuestionsCompleted = sharedpreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");

        Bundle bundle = getArguments();
        wordsOrNumbers = bundle.getString("wordsOrNumbers");
        count = bundle.getString("score");

        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.questionCompleteRl:{//delete
                onClickAction();
                break;
            }
        }
    }
    public void goBackToGame(){
        if(wordsOrNumbers == "numbers"){
            transaction.replace(R.id.frame_layout, new GameNumbersFragment());
            transaction.commit();
        }else {
            transaction.replace(R.id.frame_layout, new GameWordsFragment());
            transaction.commit();
        }
    }
    public void badgeAchieveSplash(){
            Bundle args = new Bundle();
            args.putString("wordsOrNumbers", wordsOrNumbers);
            BadgeAchieveFragment badgeFragment = new BadgeAchieveFragment();
            badgeFragment.setArguments(args);
            transaction.replace(R.id.frame_layout, badgeFragment).addToBackStack("");
            transaction.commit();
    }

    public void levelOpened(){
        if (mWifi.isConnected()) {
            helper.execute();
        }
        transaction.replace(R.id.frame_layout, new CurrentProgressBadgeFragment()).addToBackStack("");//with basic arguments
        transaction.commit();
    }
    public void onClickAction(){

    }

    @Override
    public void dataSent(List<User> values) {

        if(difficulty.equals(getString(R.string.basic)) && Integer.parseInt(basicDifficultyQuestionsCompleted)<80){
            if(Integer.parseInt(basicDifficultyQuestionsCompleted)%10 == 0){
                badgeAchieveSplash();
            }else {
                goBackToGame();
            }
        }else if(difficulty.equals(getString(R.string.basic)) && Integer.parseInt(basicDifficultyQuestionsCompleted)==80){
            editor.putString(UtilityClass.IS_CHAMPION_ACTIVE, "true");
            editor.putString(UtilityClass.USER_TOP_DIFFCICULTY_LEVEL,getString(R.string.champion));
            editor.putString(UtilityClass.IS_BASIC_COMPLETED,"true");
            editor.commit();
            levelOpened();
        }else if(difficulty.equals(getString(R.string.champion)) && Integer.parseInt(championDifficultyQuestionsCompleted)<105){
            if(Integer.parseInt(championDifficultyQuestionsCompleted)%10 == 0){
                badgeAchieveSplash();
            }else {
                goBackToGame();
            }
        }else if(difficulty.equals(getString(R.string.champion)) && Integer.parseInt(championDifficultyQuestionsCompleted)==105){
            editor.putString(UtilityClass.IS_SUPERSTAR_ACTIVE, "true");
            editor.putString(UtilityClass.USER_TOP_DIFFCICULTY_LEVEL,getString(R.string.superstar));
            editor.putString(UtilityClass.IS_CHAMPION_COMPLETED,"true");
            editor.commit();
            levelOpened();
        }else if(difficulty.equals(getString(R.string.superstar)) && Integer.parseInt(superstarDifficultyQuestionsCompleted)<105){
            if(Integer.parseInt(superstarDifficultyQuestionsCompleted)%10 == 0){
                badgeAchieveSplash();
            }else {
                goBackToGame();
            }
        }else if(difficulty.equals(getString(R.string.superstar)) && Integer.parseInt(championDifficultyQuestionsCompleted)==105) {
            editor.putString(UtilityClass.IS_SUPERSTAR_COMPLETED,"true");
            editor.commit();
            levelOpened();
        }else {
            if(Integer.parseInt(superstarDifficultyQuestionsCompleted)%10 == 0){
                badgeAchieveSplash();
            }else {
                goBackToGame();
            }
        }
    }
}
