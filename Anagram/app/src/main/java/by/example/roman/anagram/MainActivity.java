package by.example.roman.anagram;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.FragmentTransaction;
import android.os.Bundle;


import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;



/**
 * Created by Roman on 04.02.2016.
 */

public class MainActivity extends Activity {


    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    static int questionChanged = 0;

    //hide this
    private static final String TWITTER_KEY = "4gBAR4k8l1SGjJjccAhfdK5rx";
    private static final String TWITTER_SECRET = "yQL3gJslwdzzn7I9HoP8pzjnWegEOpGr7b9JTdi1JWv26bXxf1";

    long Delay = 8000;

    CallbackManager callbackManager;
    static Context context;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        setContentView(R.layout.fragment_container);

        sharedpreferences = this.getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        editor.putString("QUESTION", "What year is it now?");
        editor.putString("ANSWER","2016");
        editor.commit();

        SplashFragment splash = new SplashFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, splash);
        transaction.commit();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        android.app.Fragment fragment = getFragmentManager().findFragmentByTag("registration");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }
}
