package by.example.roman.anagram.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import by.example.roman.anagram.DAO.MyNumberDatabase;
import by.example.roman.anagram.QuestionAnswerPair;
import by.example.roman.anagram.R;
import by.example.roman.anagram.UtilityClass;

/**
 * Created by Roman on 23.02.2016.
 */



public class MyNumberTableHelper extends AsyncTask<String,Void,List<QuestionAnswerPair>> {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private ProgressDialog dialog;
    /** application context. */
    private Activity activity;

    public OnDatabaseQueryCompleted delegate = null;

    private Cursor employees;
    private MyNumberDatabase db;

    Context context;

    public MyNumberTableHelper(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        db = new MyNumberDatabase(context);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    @Override
    protected void onPostExecute(List<QuestionAnswerPair> questionAnswerPairs) {
        super.onPostExecute(questionAnswerPairs);
        if(!questionAnswerPairs.isEmpty()) {
            delegate.numericBasicCompleted(questionAnswerPairs);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        db.closeCursor();
    }

    @Override
    protected List<QuestionAnswerPair> doInBackground(String... params) {
        sharedPreferences  = activity.getSharedPreferences(UtilityClass.anagramzSharedPrefKey, 0);
        editor = sharedPreferences.edit();
        QuestionAnswerPair question = null;
        List<QuestionAnswerPair> questionList = new ArrayList<>();
        if(sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,context.getString(R.string.basic)).equals(context.getString(R.string.basic))) {
            int max = db.getBasicMaxId();
            int id = 1 + (int) (Math.random() * max);
            String[] data = db.getBasicQuestionById(id);
            question = new QuestionAnswerPair(data[0], data[1]);
        }else if(sharedPreferences.getString(UtilityClass.DIFICULTY_LEVEL,context.getString(R.string.basic)).equals(context.getString(R.string.champion))){
            int max = db.getChampionMaxId();
            int id = 1 + (int) (Math.random() * max);
            String [] data = db.getChampionQuestionById(id);
            question = new QuestionAnswerPair(data[0], data[1]);
        }else {
            int max = db.getSuperstarMaxId();
            int id = 1 + (int) (Math.random() * max);
            String [] data = db.getSuperstarQuestionById(id);
            question = new QuestionAnswerPair(data[0], data[1]);
        }
        questionList.add(question);
        return questionList;
    }
}
