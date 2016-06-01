package by.example.roman.anagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by Roman on 09.02.2016.
 */
public class DuelGameFragment extends Fragment implements View.OnClickListener{

    io.socket.client.Socket socket;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    ImageView gameBackArrowIv;
    ImageView settingsIv;

    String finalRespLogin = null;
    String winner;
    String result;
    String myPoints;
    String opponentPoints;

    int opponentWinCount;
    int myWinCount;

    long timeLesft;

    long timeEstimated;

    int questionCount = 1;

    TextView timerTv;
    TextView gameAnswerTv;
    TextView gameQuestionTv;

    Button button00;
    Button button01;
    Button button02;
    Button button10;
    Button button11;
    Button button12;
    Button button20;
    Button button21;
    Button button22;

    TextView gameHintTv;

    TextView myLoginTv;
    TextView myScoreTv;
    TextView opponentLoginTv;
    TextView opponentScoreTv;

    Button [] buttons;

    String question = null;
    String answer = null;
    String parsedDots[];

    int rightAnswersCount = 0;
    int falseAnswerCount = 0;
    int completedQuestions = 0;

    FragmentTransaction transaction;

    private CountDownTimer countDownTimer;
    private final long startTime = 10 * 1000;
    private final long interval = 1 * 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.duel_mode_game_fragment, container, false);

        sharedpreferences = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        gameBackArrowIv = (ImageView)view.findViewById(R.id.duelModeGameBackArrowIv);
        settingsIv = (ImageView)view.findViewById(R.id.duelModeGameSettingsIv);

        timerTv = (TextView)view.findViewById(R.id.duelModeTimerIv);
        gameAnswerTv = (TextView)view.findViewById(R.id.duelModeGameAnswerTv);
        gameQuestionTv = (TextView)view.findViewById(R.id.duelModeGameQuestionTv);

        button00 = (Button)view.findViewById(R.id.duelCubeButton1);
        button01 = (Button)view.findViewById(R.id.duelCubeButton2);
        button02 = (Button)view.findViewById(R.id.duelCubeButton3);
        button10 = (Button)view.findViewById(R.id.duelCubeButton4);
        button11 = (Button)view.findViewById(R.id.duelCubeButton5);
        button12 = (Button)view.findViewById(R.id.duelCubeButton6);
        button20 = (Button)view.findViewById(R.id.duelCubeButton7);
        button21 = (Button)view.findViewById(R.id.duelCubeButton8);
        button22 = (Button)view.findViewById(R.id.duelCubeButton9);

        buttons = new Button[]{button00,button01,button02,button10,button11,button12,button20,button21,button22};

        myLoginTv = (TextView)view.findViewById(R.id.duelModeGameOwnerLogin);
        myScoreTv = (TextView)view.findViewById(R.id.duelModeGameScoreOwner);

        opponentLoginTv = (TextView)view.findViewById(R.id.duelModeGameGuestLogin);
        opponentScoreTv = (TextView)view.findViewById(R.id.duelModeGameScoreGuest);

        gameHintTv = (TextView)view.findViewById(R.id.duelModeGameHint);

        transaction = getActivity().getFragmentManager().beginTransaction();

        countDownTimer = new DuelCountDownTimer(startTime,interval);

        View.OnClickListener duelCubeButtonsOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.duelCubeButton1:{
                        onCubeItemClick(button00);
                        break;
                    }
                    case R.id.duelCubeButton2:{
                        onCubeItemClick(button01);
                        break;
                    }
                    case R.id.duelCubeButton3:{
                        onCubeItemClick(button02);
                        break;
                    }
                    case R.id.duelCubeButton4:{
                        onCubeItemClick(button10);
                        break;
                    }
                    case R.id.duelCubeButton5:{
                        onCubeItemClick(button11);
                        break;
                    }
                    case R.id.duelCubeButton6:{
                        onCubeItemClick(button12);
                        break;
                    }
                    case R.id.duelCubeButton7:{
                        onCubeItemClick(button20);
                        break;
                    }
                    case R.id.duelCubeButton8:{
                        onCubeItemClick(button21);
                        break;
                    }
                    case R.id.duelCubeButton9:{
                        onCubeItemClick(button22);
                        break;
                    }
                }
            }
        };
        for(int i =0;i<buttons.length;i++){
            buttons[i].setOnClickListener(duelCubeButtonsOnClick);
            buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
        }
        List<QuestionAnswerPair> list = new ArrayList<>();

        createDuel();

        view.setOnClickListener(this);
        return view;
    }

    public void createDuel(){
        try {
            socket = IO.socket("http://148.251.92.60:1537?__sails_io_sdk_version=0.13.5");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("url", "/api/v1/duel/"+sharedpreferences.getString(UtilityClass.USER_ID,"0"));
                    Log.d("url",obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("get", obj);
                Log.d("connected","connected");
            }

        }).on("message", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("message", String.valueOf(args[0]));
                JSONObject jsonObj = null;
                String respAnswer = null;
                String respQuestion = null;
                String respIsChar = null;
                String respLogin = null;

                try {
                    jsonObj = new JSONObject(String.valueOf(args[0]));
                    JSONObject question = jsonObj.getJSONObject("question");

                    respAnswer = question.getString("answer");
                    respQuestion = question.getString("question");
                    respIsChar = question.getString("type");
                    respLogin = question.getString("opponent");

                    if (respQuestion != null) {
                        final String finalRespQuestion = respQuestion;
                        final String finalRespAnswer = respAnswer;
                        finalRespLogin = respLogin;
                        final String finalRespIsChar = respIsChar;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                questionInit(finalRespQuestion, finalRespAnswer, finalRespLogin, finalRespIsChar);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("disconnected", "disconnected");
            }

        }).on("question_result", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("question_result", String.valueOf(args[0]));
                JSONObject jsonObj = null;
                String winner = null;
                String questionNumber = null;
                String respAnswer = null;
                String respQuestion = null;
                String respIsChar = null;
                String respLogin = null;
                JSONObject nextQuestion = null;
                try {
                    jsonObj = new JSONObject(String.valueOf(args[0]));
                    winner = jsonObj.getString("message");
                    questionNumber = jsonObj.getString("questionNumber");
                    nextQuestion = jsonObj.getJSONObject("question");

                    respAnswer = nextQuestion.getString("answer");
                    respQuestion = nextQuestion.getString("question");
                    respIsChar = nextQuestion.getString("type");
                    //respLogin = nextQuestion.getString("opponent");

                    if (respQuestion != null) {
                        Log.d("question",respQuestion);
                        final String finalRespQuestion = respQuestion;
                        final String finalRespAnswer = respAnswer;
                        final String RespLogin = respLogin;
                        final String finalRespIsChar = respIsChar;
                        final String finalWinner = winner;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                questionInit(finalRespQuestion, finalRespAnswer, finalRespLogin, finalRespIsChar);
                                        if(finalWinner.equals("YOU WIN!!!")){
                                            myWinCount++;
                                            myScoreTv.setText(String.valueOf(myWinCount*4));
                                        }else if(finalWinner.equals("DRAW!!!")){
                                            myWinCount++;
                                            opponentWinCount++;
                                            myScoreTv.setText(myWinCount*4);
                                            opponentScoreTv.setText(opponentWinCount*4);
                                        }else {
                                            opponentWinCount++;
                                            opponentScoreTv.setText(opponentWinCount*4);
                                        }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }).on("end_game", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("end_game", String.valueOf(args[0]));
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(String.valueOf(args[0]));
                    winner = jsonObj.getString("winner");
                    result = jsonObj.getString("message");
                    myPoints = jsonObj.getString("yourPoints");
                    opponentPoints = jsonObj.getString("otherPoints");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.connect();
    }

    public void nextQuestion(long time){
        questionCount++;

        JSONObject obj = new JSONObject();
        try {
            obj.put("url", "/api/v1/answer/"+questionCount+"?"+"time="+time+"&user_id="+sharedpreferences.getString(UtilityClass.USER_ID,"0"));
            Log.d("url", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("get",obj);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){

        }
    }

    public void setQuestionAndAnswer(String questionLoad, String answerLoad) {
        for(int i = 0;i<buttons.length;i++){
                buttons[i].setBackgroundResource(0);
                buttons[i].setBackgroundResource(R.drawable.game_cube_item_shape);
                buttons[i].setEnabled(true);
                buttons[i].setTag("0");
        }
        gameAnswerTv.setText("");
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
                nextQuestion(timeEstimated);
                countDownTimer.cancel();
                button.setEnabled(false);
                Toast.makeText(getActivity(), "Good go to next", Toast.LENGTH_LONG).show();
                rightAnswersCount = 0;
                falseAnswerCount = 0;

//                should be replaced with data to server method invoke


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
                nextQuestion(10);
                countDownTimer.cancel();
            }
        }
    }
    public void questionInit(String question,String answer,String opponentLogin,String numbersOrCharacters) {
        Log.d("invoke","invoke");
        if(numbersOrCharacters.equals("Number")) {
            gameHintTv.setText(R.string.numbersAndDatesHint);
            Log.d("in number", numbersOrCharacters);
            setQuestionAndAnswer(question, answer);
            numbersRandomise(answer);
        }else if(numbersOrCharacters.equals("String")){
            gameHintTv.setText(R.string.namesAndWordsHint);
            Log.d("in String", numbersOrCharacters);
            setQuestionAndAnswer(question, answer);
            charactersRandomise(answer);
        }
        timerTv.setBackgroundResource(R.drawable.duel_timer);
        gameQuestionTv.setText(question);
        myLoginTv.setText(sharedpreferences.getString(UtilityClass.USER_LOGIN,"0"));
        opponentLoginTv.setText(opponentLogin);
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        socket.disconnect();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        socket.disconnect();
        super.onDetach();
    }

    public class DuelCountDownTimer extends CountDownTimer
    {

        public DuelCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            nextQuestion(10);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            timeLesft = millisUntilFinished/1000;
            timeEstimated = 10 - timeLesft;
            timerTv.setText("Time remain:" + millisUntilFinished/1000);
            if(timeLesft == 2){
                timerTv.setBackgroundResource(R.drawable.duel_red_timer);
            }
        }
    }
}
