package by.example.roman.anagram.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import by.example.roman.anagram.DAO.MyCharacterDatabase;
import by.example.roman.anagram.HttpHelper;
import by.example.roman.anagram.User;
import by.example.roman.anagram.UtilityClass;
import okhttp3.Response;

/**
 * Created by Roman on 23.02.2016.
 */
public class UserUpdateHelper extends AsyncTask<String,Void,List<User>> {


    private ProgressDialog dialog;
    /** application context. */
    private Activity activity;

    public UserUpdateCompleted delegate = null;

    private Cursor employees;
    private MyCharacterDatabase db;

    Context context;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public UserUpdateHelper(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<User> users) {
        super.onPostExecute(users);
        delegate.dataSent(users);
    }

    @Override
    protected List<User> doInBackground(String... params) {
        sharedpreferences = context.getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        List<User> userList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Log.d("date",currentDateandTime);
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        Log.d("seconds", String.valueOf(seconds));
        String date = "2016-02-22T10:39:11.023Z";//change
        Log.d("id",sharedpreferences.getString(UtilityClass.USER_ID,"0"));
        Log.d("data",sharedpreferences.getString(UtilityClass.BADGES_COUNT,"0")+" "+
                sharedpreferences.getString(UtilityClass.DUEL_CONT,"0")+" "+
                sharedpreferences.getString(UtilityClass.USER_TOP_DIFFCICULTY_LEVEL,"0")+" "+
                sharedpreferences.getString(UtilityClass.USER_ID,"0"));
        User user = null;
        Response response = null;
        try {
            response = new HttpHelper().doUpdatePutRequest("http://148.251.92.60:1537/api/v1/users/changeUser",
                                                                  "",
                                                                  sharedpreferences.getString(UtilityClass.BADGES_COUNT,"0"),
                                                                  sharedpreferences.getString(UtilityClass.DUEL_CONT,"0"),
                                                                  sharedpreferences.getString(UtilityClass.USER_TOP_DIFFCICULTY_LEVEL,"0"),
                                                                  sharedpreferences.getString(UtilityClass.USER_ID,"0"));
            Log.d("code", String.valueOf(response.code()));
            String strResp = response.body().string();
            Log.d("code",strResp);
            JSONObject jsonObj = new JSONObject(strResp);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }
}
