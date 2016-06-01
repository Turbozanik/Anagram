package by.example.roman.anagram;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import by.example.roman.anagram.DAO.MyNumberDatabase;
import by.example.roman.anagram.async.MyNumberTableHelper;
import by.example.roman.anagram.async.OnDatabaseQueryCompleted;

/**
 * Created by Roman on 05.02.2016.
 */
public class GameNumbersFragment extends Fragment implements View.OnClickListener,IGameFlowController,OnDatabaseQueryCompleted{//need to add check for difficulty lvl and correct points

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

    String wordsOrNumbers = "numbers";

    AlertDialog.Builder builder;
    MyNumberTableHelper numberBasicHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        numberBasicHelper = new MyNumberTableHelper(getActivity());
        numberBasicHelper.delegate = this;

        transaction = getActivity().getFragmentManager().beginTransaction();
        sharedPreferences  = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, 0);
        editor = sharedPreferences.edit();


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

        gameBackArrowIv = (ImageView)view.findViewById(R.id.gameBackArrowIv);
        gameSettingIv = (ImageView)view.findViewById(R.id.gameSettingsIv);

        gameQuestionTv = (TextView)view.findViewById(R.id.gameQuestionTv);
        gameAnswerTv = (TextView)view.findViewById(R.id.gameAnswerTv);

        gameHintTv = (TextView)view.findViewById(R.id.gameHint);


        numberBasicHelper.execute();
        //if(wordsOrNumbers == "numbers"){

        //}else {
        //    gameHintTv.setText(R.string.namesAndWordsHint);
        //}


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
                ((MainActivity)getActivity()).onBackPressed();
                break;
            }
            case R.id.gameSettingsIv:{
                transaction.replace(R.id.frame_layout,new SettingsFragment()).addToBackStack("");
                transaction.commit();
                break;
            }
            case R.id.gameTypeChangeButton:{
                    transaction.replace(R.id.frame_layout, new GameWordsFragment());
                    transaction.commit();
                break;
            }
            case R.id.questionRefreshIv:{
                if(MainActivity.questionChanged <3) {
                    MainActivity.questionChanged++;
                    refresh();
                }else {
                    Toast.makeText(getActivity(),"You cant do it anymore",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.gameRl:{
                transaction.replace(R.id.frame_layout, new CurrentProgressBadgeFragment());
                transaction.commit();
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
//        Log.d("paresd", parsedDots[0] +","+parsedDots[1] +","+parsedDots[2] +","+parsedDots[3]);

    }

    @Override
    public void numbersRandomise(String answer) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        ArrayList<Integer> rightNumbers = new ArrayList<Integer>();
        Random randomGenerator = new Random();
        int i=0;
        while (numbers.size() < 10) {

            int random = randomGenerator .nextInt(10);
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
           numbers.set(rightNumbers.get(k), Integer.parseInt(String.valueOf(answer.charAt(k))));
           Log.d(rightNumbers.get(k) + "", String.valueOf(answer.charAt(k)));
       }
        for (int h=0;h<numbers.size()-1;h++) {
            Log.d("newNumber", String.valueOf(numbers.get(h)));
        }
        for(int m=0;m<buttons.length;m++){
            buttons[m].setText(numbers.get(m) + "");
        }
    }

    @Override
    public void charactersRandomise(String answer) {

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
            //Log.d("equality",rightAnswersCount + "??"+ answer.charAt(rightAnswersCount));
            if(rightAnswersCount == answer.length()){
                button.setEnabled(false);
                Toast.makeText(getActivity(),"Good go to next",Toast.LENGTH_LONG).show();
                rightAnswersCount = 0;
                falseAnswerCount = 0;
                String difficulty = sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,getString(R.string.basic));
                String completedBeforeQuestions;
                if(difficulty.equals(getString(R.string.basic))) {
                    completedBeforeQuestions = sharedPreferences.getString(UtilityClass.BASIC_LEVEL_COMPLETED_QUESTION,"0");
                    Integer count = Integer.parseInt(completedBeforeQuestions);
                    count++;
                    Log.d("score",completedBeforeQuestions);
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
            Log.d("equalityFalse",rightAnswersCount + "??"+ answer.charAt(rightAnswersCount));
            button.setBackgroundResource(0);
            button.setBackgroundResource(R.drawable.wrong_answer_game_cube_shape);
            button.setTag(R.drawable.wrong_answer_game_cube_shape);
            button.setEnabled(false);
            falseAnswerCount++;
            if(falseAnswerCount==4){
                rightAnswersCount = 0;
                falseAnswerCount = 0;
                builder.show();
            }
        }
    }


    public void refresh(){
        transaction.detach(this).attach(this).commit();
    }
    @Override
    public int getRandom(ArrayList<Integer> List) {
        return 0;
    }

    //BD callback here,we need numeric one

    @Override
    public void numericBasicCompleted(List<QuestionAnswerPair> value) {
        gameHintTv.setText(R.string.numbersAndDatesHint);
        Log.d("questionAnser", value.get(0).getQuestion() + " " + value.get(0).getAnswer());
        Log.d("answerLength", String.valueOf(value.get(0).getAnswer().length()));
        setQuestionAndAnswer(value.get(0).getQuestion(), value.get(0).getAnswer());
        numbersRandomise(value.get(0).getAnswer());
    }

    @Override
    public void numericChampionCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void numericSuperstarCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void letterBasicCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void letterChampionCompleted(List<QuestionAnswerPair> value) {

    }

    @Override
    public void letterSuperstarCompleted(List<QuestionAnswerPair> value) {

    }
}
