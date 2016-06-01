package by.example.roman.anagram;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Roman on 18.02.2016.
 */
public class HttpHelper {
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    // code request code here
    public String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // post request code here

    public Response doRegistrationPostRequest(String url, String login, String email, String password, String facebookId, String twitterId) throws IOException {
        //RequestBody body = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add("login", login)
                .add("password", email)
                .add("email", password)
                .add("facebookId",facebookId)
                .add("twitterId",twitterId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        Log.d("response", String.valueOf(response));
        return response;
    }

    public String doSignInPostRequest(String url, String identifier, String password) throws IOException {
        //RequestBody body = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add("identifier", identifier)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        Log.d("response", String.valueOf(response));
        return response.body().string();
    }

    public Response doUpdatePutRequest(String url, String date, String badgesCount, String duel_count, String topDifficulty,String id) throws IOException {
        //RequestBody body = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add("lastUpdateDate",date)
                .add("badgesCount",badgesCount)
                .add("duel_count",duel_count)
                .add("topDifficultyLevel",topDifficulty)
                .add("id",id)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        Log.d("response", String.valueOf(response));
        return response;
    }
}
