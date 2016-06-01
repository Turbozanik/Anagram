package by.example.roman.anagram;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import by.example.roman.anagram.async.MyLettersTableHelper;
import by.example.roman.anagram.async.MyNumberTableHelper;
import by.example.roman.anagram.async.OnDatabaseQueryCompleted;

/**
 * Created by Roman on 05.02.2016.
 */
public class GameWordsFragment extends Fragment implements View.OnClickListener,IGameFlowController,OnDatabaseQueryCompleted{//need to add check for difficulty lvl and correct points

    TextView gameHintTv;
    TextView gameQuestionTv;
    TextView gameAnswerTv;

    ImageView gameBackArrowIv;
    ImageView gameSettingIv;
    ImageView questionRefreshIv;
    
    StringBuilder stringBuilder;

    Button [] buttons;

    String question = null;
    String answer = null;
    String parsedDots[];

    int rightAnswersCount = 0;
    int falseAnswerCount = 0;
    int completedQuestions = 0;

    FragmentTransaction transaction;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button button00;
    Button button01;
    Button button02;
    Button button10;
    Button button11;
    Button button12;
    Button button20;
    Button button21;
    Button button22;

    Button changeQuestionType;

    String wordsOrNumbers;

    AlertDialog.Builder builder;
    private MyLettersTableHelper lettersBasicHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        lettersBasicHelper = new MyLettersTableHelper(getActivity());
        lettersBasicHelper.delegate = this;


        transaction = getActivity().getFragmentManager().beginTransaction();
        sharedPreferences  = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, 0);
        editor = sharedPreferences.edit();

        gameBackArrowIv = (ImageView)view.findViewById(R.id.gameBackArrowIv);
        gameSettingIv = (ImageView)view.findViewById(R.id.gameSettingsIv);

        button00 = (Button)view.findViewById(R.id.cubeButton1);
        button01 = (Button)view.findViewById(R.id.cubeButton2);
        button02 = (Button)view.findViewById(R.id.cubeButton3);
        button10 = (Button)view.findViewById(R.id.cubeButton4);
        button11 = (Button)view.findViewById(R.id.cubeButton5);
        button12 = (Button)view.findViewById(R.id.cubeButton6);
        button20 = (Button)view.findViewById(R.id.cubeButton7);
        button21 = (Button)view.findViewById(R.id.cubeButton8);
        button22 = (Button)view.findViewById(R.id.cubeButton9);

        buttons = new Button[]{button00,button01,button02,button10,button11,button12,button20,button21,button22};

        changeQuestionType = (Button)view.findViewById(R.id.gameTypeChangeButton);
        questionRefreshIv = (ImageView)view.findViewById(R.id.questionRefreshIv);

        gameQuestionTv = (TextView)view.findViewById(R.id.gameQuestionTv);
        gameAnswerTv = (TextView)view.findViewById(R.id.gameAnswerTv);

        gameHintTv = (TextView)view.findViewById(R.id.gameHint);

        lettersBasicHelper.execute();
//        gameHintTv.setText(R.string.numbersAndDatesHint);
//        setQuestionAndAnswer(question,answer);
//        charactersRandomise(answer);



        View.OnClickListener cubeButtonsOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cubeButton1:{
                        onCubeItemClick(button00);
                        break;
                    }
                    case R.id.cubeButton2:{
                        onCubeItemClick(button01);
                        break;
                    }
                    case R.id.cubeButton3:{
                        onCubeItemClick(button02);
                        break;
                    }
                    case R.id.cubeButton4:{
                        onCubeItemClick(button10);
                        break;
                    }
                    case R.id.cubeButton5:{
                        onCubeItemClick(button11);
                        break;
                    }
                    case R.id.cubeButton6:{
                        onCubeItemClick(button12);
                        break;
                    }
                    case R.id.cubeButton7:{
                        onCubeItemClick(button20);
                        break;
                    }
                    case R.id.cubeButton8:{
                        onCubeItemClick(button21);
                        break;
                    }
                    case R.id.cubeButton9:{
                        onCubeItemClick(button22);
                        break;
                    }
                }
                //onClickAction(v);
            }
        };

        for(int i =0;i<buttons.length;i++){
            buttons[i].setOnClickListener(cubeButtonsOnClick);
            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
        }

        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Wrong answer");
        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refresh();
                dialog.dismiss();
            }
        });

        questionRefreshIv.setOnClickListener(this);
        changeQuestionType.setOnClickListener(this);
        gameBackArrowIv.setOnClickListener(this);
        gameSettingIv.setOnClickListener(this);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.gameBackArrowIv:{
                ((MainActivity)getActivity()).onBackPressed();//remake logic its just reversing last transaction
                break;
            }
            case R.id.gameSettingsIv:{
                transaction.replace(R.id.frame_layout,new SettingsFragment());
                transaction.commit();
                break;
            }
            case R.id.gameTypeChangeButton:{
                    transaction.replace(R.id.frame_layout, new GameNumbersFragment());
                    transaction.commit();
                break;
            }
            case R.id.questionRefreshIv:{
                if(MainActivity.questionChanged <3) {
                    MainActivity.questionChanged++;
                    refresh();
                }else {
                    Toast.makeText(getActivity(),"You can do it anymore",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void setQuestionAndAnswer(String questionLoad, String answerLoad) {
        question = questionLoad;
        answer = answerLoad;
        int answerLength = answer.length();
        gameQuestionTv.setText(question);
        for(int i = 0;i < answerLength; i++) {
            gameAnswerTv.append(". ");
        }
        gameAnswerTv.append("("+answerLength+")");
        String dots = gameAnswerTv.getText().toString();
        parsedDots = dots.split(" ");
    }

    @Override
    public void numbersRandomise(String answer) {

    }

    @Override
    public void charactersRandomise(String answer){
        final String alphabet = "ABSDEFGHMN";
        final int N = alphabet.length();
        ArrayList<Character> numbers = new ArrayList<Character>();
        ArrayList<Integer> rightNumbers = new ArrayList<Integer>();
        Random randomGenerator = new Random();

        Log.d("size",String.valueOf(N));int i=0;
        while (numbers.size() < 10) {

            char random = alphabet.charAt(randomGenerator.nextInt(N));
            if (!numbers.contains(random)) {
                numbers.add(random);
                Log.d("numbers", numbers.get(i).toString());
                i++;
            }
        }
        int j=0;
        while (rightNumbers.size() < answer.length()) {
            int random = randomGenerator .nextInt(8);
            if (!rightNumbers.contains(random)) {
                rightNumbers.add(random);
                Log.d("RightNumbers",rightNumbers.get(j).toString());
                j++;
            }
        }
        for(int k =0;k<rightNumbers.size();k++){
            numbers.set(rightNumbers.get(k),answer.charAt(k));
            Log.d(rightNumbers.get(k)+"", numbers.get(rightNumbers.get(k)).toString());
        }
        for (int h=0;h<numbers.size()-1;h++) {
            Log.d("newNumber", String.valueOf(numbers.get(h)));
        }
        for(int m=0;m<buttons.length;m++){
            buttons[m].setText(numbers.get(m) + "");
        }
    }

    @Override
    public void onClickAction(View v) {

    }

    public void onCubeItemClick(Button button){
        String buttonText = String.valueOf(button.getText());
        if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
            parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
            gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
            Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
            rightAnswersCount++;
            falseAnswerCount = 0;
            button.setBackgroundResource(0);
            Log.d("i am here", "should be changed");

            for(int i = 0;i<buttons.length;i++){
                int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
                if(resId == R.drawable.wrong_answer_game_cube_shape){
                    buttons[i].setBackgroundResource(0);
                    buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
                    buttons[i].setEnabled(true);
                    buttons[i].setTag("0");
                }
            }
            button.setEnabled(false);
            button.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
            if(rightAnswersCount == answer.length()){
                Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
                rightAnswersCount = 0;
                falseAnswerCount = 0;
                String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,getString(R.string.basic));
                String completedBeforeQuestions;
                if(difficulty.equals(getString(R.string.basic))) {
                    completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
                    Log.d("score",completedBeforeQuestions);
                    Integer count = Integer.parseInt(completedBeforeQuestions);
                    count++;
                    editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
                    editor.commit();
                }else if(difficulty.equals(getString(R.string.champion))){
                    completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
                    Integer count = Integer.parseInt(completedBeforeQuestions);
                    count++;
                    editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
                    editor.commit();
                }else {
                    completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
                    Integer count = Integer.parseInt(completedBeforeQuestions);
                    count++;
                    editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
                    editor.commit();
                }
                Bundle args = new Bundle();
                args.putString("wordsOrNumbers", wordsOrNumbers);
                args.putString("score", String.valueOf(completedQuestions));
                RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
                splashFragment.setArguments(args);
                transaction.replace(R.id.frame_layout, splashFragment);
                transaction.commit();
            }
        }else{
            button.setBackgroundResource(0);
            button.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
            button.setTag(R.drawable.wrong_answer_game_cube_shape);
            button.setEnabled(false);
            falseAnswerCount++;
            if(falseAnswerCount==4){
                //Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
                rightAnswersCount = 0;
                falseAnswerCount = 0;
                builder.show();
            }
        }
    }
////
////    @Override
////    public void onClickAction(View v) {
////        switch (v.getId()){
////            case R.id.cubeButton1:{
////                String buttonText = String.valueOf(button00.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button00.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////
////                        for(int i = 0;i<buttons.length;i++){
////                            int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                            if(resId == R.drawable.wrong_answer_game_cube_shape){
////                                buttons[i].setBackgroundResource(0);
////                                buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                                buttons[i].setEnabled(true);
////                                buttons[i].setTag("0");
////                            }
////                        }
////                    button00.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                        if(rightAnswersCount == answer.length()){
////                            Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                            rightAnswersCount = 0;
////                            falseAnswerCount = 0;
////                            String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                            String completedBeforeQuestions;
////                            if(difficulty.equals(UtilityClass.BASIC)) {
////                                completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                                Integer count = Integer.parseInt(completedBeforeQuestions);
////                                count++;
////                                editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                            }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                                completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                                Integer count = Integer.parseInt(completedBeforeQuestions);
////                                count++;
////                                editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                            }else {
////                                completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                                Integer count = Integer.parseInt(completedBeforeQuestions);
////                                count++;
////                                editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                            }
////                            Bundle args = new Bundle();
////                            args.putString("wordsOrNumbers", wordsOrNumbers);
////                            RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                            splashFragment.setArguments(args);
////                            transaction.replace(R.id.frame_layout, splashFragment);
////                            transaction.commit();
////                        }
////                }else{
////                    button00.setBackgroundResource(0);
////                    button00.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    button00.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button00.setEnabled(false);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        //Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton2:{
////                String buttonText = String.valueOf(button01.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button01.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button01.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button01.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button01.setBackgroundResource(0);
////                    button01.setEnabled(false);
////                    button01.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton3:{
////                String buttonText = String.valueOf(button02.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button02.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button02.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button02.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button02.setBackgroundResource(0);
////                    button02.setEnabled(false);
////                    button02.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton4:{
////                String buttonText = String.valueOf(button10.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button10.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button10.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button10.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button10.setBackgroundResource(0);
////                    button10.setEnabled(false);
////                    button10.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton5:{
////                String buttonText = String.valueOf(button11.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    Log.d("i am here", "should be changed");
////                    button11.setBackgroundResource(0);
////
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button11.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button11.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button11.setBackgroundResource(0);
////                    button11.setEnabled(false);
////                    button11.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton6:{
////                String buttonText = String.valueOf(button12.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button12.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button12.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button12.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button12.setBackgroundResource(0);
////                    button12.setEnabled(false);
////                    button12.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton7:{
////                String buttonText = String.valueOf(button20.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button20.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button20.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        button20.setBackgroundResource(0);
////                        button20.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button20.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button20.setBackgroundResource(0);
////                    button20.setEnabled(false);
////                    button20.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton8:{
////                String buttonText = String.valueOf(button21.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button21.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        button21.setEnabled(true);
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button21.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Log.d("score",completedBeforeQuestions);
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button21.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button21.setBackgroundResource(0);
////                    button21.setEnabled(false);
////                    button21.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////            case R.id.cubeButton9:{
////
////                String buttonText = String.valueOf(button22.getText());
////                if(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount)))) {
////                    parsedDots[rightAnswersCount] = String.valueOf(answer.charAt(rightAnswersCount));
////                    gameAnswerTv.setText(TextUtils.join(" ", Arrays.asList(parsedDots)));
////                    Log.d("0=0", String.valueOf(buttonText.equals(String.valueOf(answer.charAt(rightAnswersCount))))+rightAnswersCount);
////                    rightAnswersCount++;
////                    falseAnswerCount = 0;
////                    button22.setBackgroundResource(0);
////                    Log.d("i am here", "should be changed");
////
////                    for(int i = 0;i<buttons.length;i++){
////                        int resId = Integer.parseInt(String.valueOf(buttons[i].getTag()));
////                        if(resId == R.drawable.wrong_answer_game_cube_shape){
////                            buttons[i].setBackgroundResource(0);
////                            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
////                            buttons[i].setTag("0");
////                            buttons[i].setEnabled(true);
////                        }
////                    }
////                    button22.setBackgroundResource(R.drawable.right_answer_game_cube_shape);
////                    if(rightAnswersCount == answer.length()){
////                        Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,UtilityClass.BASIC);
////                        Log.d("difficulty",difficulty);
////                        String completedBeforeQuestions;
////                        if(difficulty.equals(UtilityClass.BASIC)) {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else if(difficulty.equals(UtilityClass.CHAMPION)){
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.CHAMPION_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }else {
////                            completedBeforeQuestions = sharedPreferences.getString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,"0");
////                            Integer count = Integer.parseInt(completedBeforeQuestions);
////                            count++;
////                            editor.putString(UtilityClass.SUPERSTAR_LEVEL_COMPLETED_QUESTION,String.valueOf(count));
////                        }
////                        Bundle args = new Bundle();
////                        args.putString("wordsOrNumbers", wordsOrNumbers);
////                        RightAnswerSplashFragment splashFragment = new RightAnswerSplashFragment();
////                        splashFragment.setArguments(args);
////                        transaction.replace(R.id.frame_layout, splashFragment);
////                        transaction.commit();
////                    }
////                }else{
////                    button22.setTag(R.drawable.wrong_answer_game_cube_shape);
////                    button22.setBackgroundResource(0);
////                    Log.d("click","is it?");
////                    button22.setEnabled(false);
////                    button22.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
////                    falseAnswerCount++;
////                    if(falseAnswerCount==4){
////                        Toast.makeText(getActivity(),"You lose",Toast.LENGTH_LONG).show();
////                        rightAnswersCount = 0;
////                        falseAnswerCount = 0;
////                        builder.show();
////                    }
////                }
////                break;
////            }
////        }
//    }

    public void refresh(){
        transaction.detach(this).attach(this).commit();
    }
    @Override
    public int getRandom(ArrayList<Integer> List) {
        return 0;
    }

    //we need letter from here,DB callbacks

    @Override
    public void numericBasicCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void numericChampionCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void numericSuperstarCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void letterBasicCompleted(List<QuestionAnswerPair> value) {
        gameHintTv.setText(R.string.numbersAndDatesHint);
        Log.d("questionAnser", value.get(0).getQuestion() + " " + value.get(0).getAnswer());
        Log.d("answerLength", String.valueOf(value.get(0).getAnswer().length()));
        setQuestionAndAnswer(value.get(0).getQuestion(), value.get(0).getAnswer());
        charactersRandomise(value.get(0).getAnswer());
    }

    @Override
    public void letterChampionCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void letterSuperstarCompleted(List<QuestionAnswerPair> value) {

    }
}
