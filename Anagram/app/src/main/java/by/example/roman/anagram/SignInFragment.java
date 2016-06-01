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

import java.util.Arrays;
import java.util.List;

import by.example.roman.anagram.async.MySignInHelper;
import by.example.roman.anagram.async.OnAuthTaskCompleted;

/**
 * Created by Roman on 08.02.2016.
 */
public class SignInFragment extends Fragment implements View.OnClickListener,OnAuthTaskCompleted {

    static int flag = 0;

    static boolean isSignIn = false;

    Button signInButton;
    TextView registrationTv;

    FragmentTransaction transaction;

    ImageView facebookSignInIv;
    ImageView twitterSignInIv;

    EditText login;
    EditText password;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;

    CallbackManager callbackManager;

    TwitterAuthClient mTwitterAuthClient;

    MySignInHelper helper;

    ConnectivityManager connManager;
    NetworkInfo mWifi;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_fragment, container, false);

        connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        helper = new MySignInHelper(getActivity());

        helper.delegate = this;

        sharedpreferences = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        login = (EditText)view.findViewById(R.id.signInEmailEt);
        password = (EditText)view.findViewById(R.id.signInPasswordEt);
        facebookSignInIv = (ImageView)view.findViewById(R.id.facebookSignInIv);
        twitterSignInIv = (ImageView)view.findViewById(R.id.twitterSignInIv);
        signInButton = (Button)view.findViewById(R.id.signInButton);
        registrationTv = (TextView)view.findViewById(R.id.signInRegisterTv);

        mTwitterAuthClient = new TwitterAuthClient();

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

        transaction = getActivity().getFragmentManager().beginTransaction();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        helper = new MySignInHelper(getActivity());
                        helper.delegate = SignInFragment.this;//may be error
                        isSignIn = true;
                        Log.d("Success", "Logged in1");
                        accessToken = loginResult.getAccessToken();
                        //save
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
                        Log.d("LoginCanceled", "Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("LoginError", "error");
                    }
                });

        SpannableString content = new SpannableString(getString(R.string.register));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        registrationTv.setText(content);

        facebookSignInIv.setOnClickListener(this);
        twitterSignInIv.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        registrationTv.setOnClickListener(this);

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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signInButton:{
                MySignInHelper helper = new MySignInHelper(getActivity());
                helper.delegate = this;
//                if(password.getText().toString().equals(password.getText().toString())
//                        && sharedpreferences.getString(UtilityClass.USER_ID,"0").equals("0")) {
                    String[] params = new String[5];
                    params[0]=login.getText()+"";
                    params[1]=password.getText()+"";
                    params[2]="default";
                    params[3]="default";
                    params[4]="default";
                    Log.d("i am here", "i am here");


                if (mWifi.isConnected()) {
                    helper.execute(params);
                }else{
                    Toast.makeText(getActivity(),"You must be connected to internet on the first launch.",Toast.LENGTH_SHORT).show();
                }
//                    transaction.replace(R.id.frame_layout,new PlayFragment()).addToBackStack("");
//                    transaction.commit();
                //}
                break;
            }
            case R.id.signInRegisterTv:{
                transaction.replace(R.id.frame_layout,new RegistrationFragment()).addToBackStack("");
                transaction.commit();
                break;
            }
            case R.id.facebookSignInIv:{
                MySignInHelper helper = new MySignInHelper(getActivity());
                helper.delegate = this;
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            }
            case R.id.twitterSignInIv:{

                flag=1;
                isSignIn = true;
                final MySignInHelper helper = new MySignInHelper(getActivity());
                helper.delegate = SignInFragment.this;
                RegistrationFragment.mTwitterAuthClient.authorize(getActivity(), new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
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
                        //transaction.replace(R.id.frame_layout, new PlayFragment()).addToBackStack("");
                        // transaction.commit();
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
                break;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("fragment", "aasdaFromSignIn");
        if(flag==0) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }else {
            Log.d("fragemtn","goingtotwitter");
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
        flag=0;
    }

    @Override
    public void onRegistrationCompleted(List<User> values) {

    }

    @Override
    public void onSignInCompleted(List<User> values) {
        if(values.isEmpty()) {
            Toast.makeText(getActivity(), "Such user does not exist.Please register.", Toast.LENGTH_SHORT).show();
        }else{
            transaction.replace(R.id.frame_layout, new PlayFragment()).addToBackStack("");
            transaction.commit();
        }
    }
}
