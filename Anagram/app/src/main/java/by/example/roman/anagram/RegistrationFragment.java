package by.example.roman.anagram;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import by.example.roman.anagram.async.MyRegisterHelper;
import by.example.roman.anagram.async.OnAuthTaskCompleted;

/**
 * Created by Roman on 03.02.2016.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener,OnAuthTaskCompleted {

    FragmentTransaction transaction;
    FragmentTransaction afterSignInTransaction;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    int flag = 0;

    public CallbackManager callbackManager;

    List<User> userList;

    Button registrationButton;
    ImageView twitterSingInButton;
    ImageView facebookSingInButton;
    TextView signInTv;

    EditText login;
    EditText email;
    EditText password;
    EditText repeatPassword;

    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;

    Random randomGenerator;
    static TwitterAuthClient mTwitterAuthClient;

    Context context;

    MyRegisterHelper helper;

    AlertDialog.Builder builder;

    ConnectivityManager connManager;
    NetworkInfo mWifi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration, container, false);

        connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        context = this.getActivity();

        helper = new MyRegisterHelper(getActivity());

        helper.delegate = this;

        sharedpreferences = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        mTwitterAuthClient = new TwitterAuthClient();
        transaction = getActivity().getFragmentManager().beginTransaction();
        afterSignInTransaction = getActivity().getFragmentManager().beginTransaction();

        randomGenerator = new Random();

        login = (EditText) view.findViewById(R.id.registrationNameSurname);
        email = (EditText) view.findViewById(R.id.registrationEmail);
        password = (EditText) view.findViewById(R.id.registrationPassword);
        repeatPassword = (EditText) view.findViewById(R.id.registrationRepeatPassword);

        registrationButton = (Button) view.findViewById(R.id.registrationButton);
        twitterSingInButton = (ImageView) view.findViewById(R.id.twitterRegisterIv);
        facebookSingInButton = (ImageView) view.findViewById(R.id.facebookRegisterIv);
        signInTv = (TextView)view.findViewById(R.id.signInTv);


        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You must be connected to internet on the first launch.");
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });
        if (!mWifi.isConnected()) {
            builder.show();
        }

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Logged in");
                        accessToken = loginResult.getAccessToken();
                        final String[] id = new String[1];
                        final String[] login = new String[1];
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            id[0] = object.getString("id");
                                            login[0] = object.getString("name");
                                            Log.d("facebook_user_data", id[0]+" "+login[0]);
                                            int password = 100000 + (int)(Math.random() * 1000000);
                                            final String[] params = new String[5];
                                            params[0] = login[0];
                                            params[1] = password+"";
                                            params[2] = "default";
                                            params[3] = id[0];
                                            params[4] = "default";
                                            Log.d("params",params[0]+params[1]+params[2]+params[3]+params[4]);
                                            helper.execute(params);
//                                            afterSignInTransaction.replace(R.id.frame_layout, new PlayFragment()).addToBackStack("");
//                                            afterSignInTransaction.commit();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
//                        Toast.makeText(getActivity(),"You should be connected to internet.",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getActivity(),"You should be connected to internet.",Toast.LENGTH_SHORT).show();
                    }
                });

        SpannableString content = new SpannableString(getString(R.string.signIn));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        signInTv.setText(content);

        signInTv.setOnClickListener(this);
        registrationButton.setOnClickListener(this);
        twitterSingInButton.setOnClickListener(this);
        facebookSingInButton.setOnClickListener(this);


        view.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {

        int networkId = 0;
        switch (v.getId()) {
            case R.id.registrationButton:{
                List<User> response =  new ArrayList<>();

                MyRegisterHelper helper = new MyRegisterHelper(getActivity());
                helper.delegate = this;
                if(password.getText().toString().equals(repeatPassword.getText().toString())
                   && sharedpreferences.getString(UtilityClass.USER_ID,"0").equals("0")) {
                    String[] params = new String[5];
                    params[0]=login.getText()+"";
                    params[1]=password.getText()+"";
                    params[2]=email.getText()+"";
                    params[3]="default";
                    params[4]="default";
                    Log.d("i am here", "i am here");
                    if(mWifi.isConnected()) {
                        helper.execute(params);
                        Log.d("respInFragment", response.toString());
                    }else{
                        Toast.makeText(getActivity(),"You must be connected to internet.",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.twitterRegisterIv:{

                flag=1;
                mTwitterAuthClient.authorize(getActivity(), new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        Log.d("twitterSuccess","LoggedIn");
                        //invoke helperLoginMethod
                        //save to sharedPref and first_run set to false
                        long id = twitterSessionResult.data.getUserId();
                        String name = twitterSessionResult.data.getUserName();
                        int password = 100000 + (int)(Math.random() * 1000000);

                        final String[] params = new String[5];
                        params[0] = name;
                        params[1] = password+"";
                        params[2] = "default";
                        params[3] = "default";
                        params[4] = id+"";
                        helper.execute(params);
                        Log.d("params",params[0]+params[1]+params[2]+params[3]+params[4]);

//                        transaction.replace(R.id.frame_layout, new PlayFragment()).addToBackStack("");
//                        transaction.commit();
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(getActivity(),"You should be connected to internet.",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
                break;
            }
            case R.id.signInTv:{
                transaction.replace(R.id.frame_layout,new SignInFragment());
                transaction.commit();
                break;
            }
            case R.id.facebookRegisterIv:{
                MyRegisterHelper helper = new MyRegisterHelper(getActivity());
                helper.delegate = this;
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","email"));
                break;
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("fragment", "aasda");
        if(flag==0 && SignInFragment.flag == 0) {
            Log.d("flag", String.valueOf(SignInFragment.flag));
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }else {
            Log.d("flag", String.valueOf(SignInFragment.flag));
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
        SignInFragment.flag = 0;
        flag=0;
    }

    @Override
    public void onRegistrationCompleted(List<User> values) {
        userList = values;
        if(userList.isEmpty()) {
            Toast.makeText(getActivity(),"Such a login already exists please sing in.",Toast.LENGTH_SHORT).show();
        }else{
            transaction.replace(R.id.frame_layout, new PlayFragment()).addToBackStack("");
            transaction.commit();
        }
        Log.d("asynk", String.valueOf(values));
    }

    @Override
    public void onSignInCompleted(List<User> values) {

    }
}
