package by.example.roman.anagram.async;

import android.app.Activity;
import android.app.FragmentTransaction;
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
import okhttp3.Response;

/**
 * Created by Roman on 18.02.2016.
 */
public class MyRegisterHelper extends AsyncTask<String, Void, List<User>>{

    private ProgressDialog dialog;
    /** application context. */
    private Activity activity;

    Context context;

    public OnAuthTaskCompleted delegate = null;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    FragmentTransaction transaction;

    String clientEmails[];
    String clientPasswords[];

    public final int SPLASH_DISPLAY_LENGTH = 1000;

    public MyRegisterHelper(Activity activity) {
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
            editor.commit();
            delegate.onRegistrationCompleted(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }else {
            delegate.onRegistrationCompleted(s);
            //Toast.makeText(context, "Already exist", Toast.LENGTH_SHORT).show();
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
        String login = params[0];
        String password = params[1];
        String email = params[2];
        if(email == null) {
            email = "default";
        }
        String facebookId = params[3];
        String twitterId = params[4];
        Response response = null;
        try {
            response = new HttpHelper().doRegistrationPostRequest("http://148.251.92.60:1537/auth/local/register", login, password, email, facebookId, twitterId);
            Log.d("code", String.valueOf(response.code()));
                String strResp = response.body().string();
            Log.d("code",strResp);
            JSONObject jsonObj = new JSONObject(strResp);

            editor.putString(UtilityClass.FIRST_RUN, "true");
            String respLogin = jsonObj.getString(UtilityClass.TAG_LOGIN);
            String respEmail = jsonObj.getString(UtilityClass.TAG_EMAIL);
            String id = jsonObj.getString(UtilityClass.TAG_ID);
            String lastUpdate = jsonObj.getString(UtilityClass.TAG_LAST_UPDATE);
            String badgesCount = jsonObj.getString(UtilityClass.TAG_BADGES_COUNT);
            String duel_count = jsonObj.getString(UtilityClass.TAG_DUEL_COUNT);
            String topDifficultyLevel = jsonObj.getString(UtilityClass.TAG_TOP_DIFFCICULTY_LEVEL);

            // adding each child node to HashMap key => value
            user = new User(respLogin, duel_count, id, topDifficultyLevel, respEmail, badgesCount, lastUpdate, facebookId, twitterId);
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
