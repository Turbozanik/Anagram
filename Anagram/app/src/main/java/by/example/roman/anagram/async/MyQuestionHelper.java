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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import by.example.roman.anagram.DAO.MyNumberDatabase;
import by.example.roman.anagram.Duel;
import by.example.roman.anagram.UtilityClass;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Roman on 25.02.2016.
 */
public class MyQuestionHelper extends AsyncTask<String ,Void,List<Duel>> {

    io.socket.client.Socket socket;
    
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    OnDuelQuestionLoaded delegate = null;
    
    private ProgressDialog dialog;
    /** application context. */
    private Activity activity;

    Context context;

    public MyQuestionHelper(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }


    @Override
    protected List<Duel> doInBackground(String... params) {
        sharedPreferences  = activity.getSharedPreferences(UtilityClass.anagramzSharedPrefKey, 0);
        editor = sharedPreferences.edit();
        
        List<Duel> list = new ArrayList<>();
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
                    obj.put("url", "/api/v1/duel/"+sharedPreferences.getString(UtilityClass.USER_ID,"0"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("get", obj);
                Log.d("connected", "connected");
            }

        }).on("message", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("message", String.valueOf(args[0]));
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("disconnected", "disconnected");
            }

        });
        socket.connect();
        return null;
    }
}
