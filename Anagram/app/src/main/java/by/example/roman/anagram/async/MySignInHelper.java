package by.example.roman.anagram.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import by.example.roman.anagram.HttpHelper;
import by.example.roman.anagram.User;
import by.example.roman.anagram.UtilityClass;

/**
 * Created by Roman on 22.02.2016.
 */
public class MySignInHelper extends AsyncTask<String,Void,List<User>> {

    private ProgressDialog dialog;
    /** application context. */
    private Activity activity;

    Context context;

    public OnAuthTaskCompleted delegate = null;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public MySignInHelper(Activity activity) {
        this.activity = activity;
        this.context = activity;
        //this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPostExecute(List<User> s) {
        super.onPostExecute(s);
        sharedpreferences = context.getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if(!s.isEmpty()) {
            editor.putString(UtilityClass.USER_LOGIN, s.get(0).getLogin());
            editor.putString(UtilityClass.DUEL_CONT, s.get(0).getDuelCount());
            editor.putString(UtilityClass.USER_ID, s.get(0).get_id());
            editor.putString(UtilityClass.USER_TOP_DIFFCICULTY_LEVEL, s.get(0).getDuelCount());
            editor.putString(UtilityClass.USER_EMAIL, s.get(0).getEmail());
            editor.putString(UtilityClass.BADGES_COUNT, s.get(0).getBadgesCount());
            editor.putString(UtilityClass.USER_LAST_UPDATE, s.get(0).getLastUpdateDate());
            delegate.onSignInCompleted(s);
            editor.commit();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }else{
            delegate.onSignInCompleted(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    @Override
    protected List<User> doInBackground(String... params) {
        sharedpreferences = context.getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        List<User> userList = new ArrayList<>();
        User user = null;
        String identifier = null;
        String login = params[0];
        String password = params[1];
        if(password == null) {
            int intPassword = 100000 + (int)(Math.random() * 1000000);
            password = intPassword+"";
        }
        String email = params[2];
        if(email == null) {
            email = "default";
        }
        String facebookId = params[3];
        String twitterId = params[4];
        String response = null;
        if(!login.equals("default")) {
            identifier = "{"+"\"login\""+":"+"\""+ login + "\"" +"}";
            Log.d("str",identifier);
        }else if(!facebookId.equals("default")){
            identifier = "{"+"\"facebookId\""+":"+"\""+ facebookId + "\"" +"}";
            Log.d("str",identifier);
        }else {
            identifier = "{"+"\"twitterId\""+":"+"\""+ twitterId + "\"" +"}";
            Log.d("str",identifier);
        }
        try {
            response = new HttpHelper().doSignInPostRequest("http://148.251.92.60:1537/auth/local", identifier, password);

            Log.d("response",response);
            JSONObject jsonObj = new JSONObject(response);

            editor.putString(UtilityClass.FIRST_RUN,"true");
            String respLogin = jsonObj.getString(UtilityClass.TAG_LOGIN);
            String respEmail = jsonObj.getString(UtilityClass.TAG_EMAIL);
            String id = jsonObj.getString(UtilityClass.TAG_ID);
            String lastUpdate = jsonObj.getString(UtilityClass.TAG_LAST_UPDATE);
            String badgesCount = jsonObj.getString(UtilityClass.TAG_BADGES_COUNT);
            String duel_count = jsonObj.getString(UtilityClass.TAG_DUEL_COUNT);
            String topDifficultyLevel = jsonObj.getString(UtilityClass.TAG_TOP_DIFFCICULTY_LEVEL);

            // adding each child node to HashMap key => value
            user = new User(respLogin,duel_count,id,topDifficultyLevel,respEmail,badgesCount,lastUpdate,facebookId,twitterId);
            userList.add(user);
            Log.d("response", respLogin + " " + id + " " + lastUpdate + " " + badgesCount + " " + duel_count + " " + topDifficultyLevel + " " + facebookId + " " + twitterId);
            Log.d("response1", user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }
}
