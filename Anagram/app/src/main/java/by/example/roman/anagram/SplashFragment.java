package by.example.roman.anagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class SplashFragment extends Fragment implements View.OnClickListener{

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private long Delay = 1000;

    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash, container, false);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.splashRl);

        sharedpreferences = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        Log.d("login",sharedpreferences.getString(UtilityClass.USER_LOGIN, "0"));
        Log.d("login",sharedpreferences.getString(UtilityClass.USER_ID, "0"));

        Timer RunSplash = new Timer();

        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                //if(sharedpreferences.getString(UtilityClass.USER_ID,"0").equals("0")) {
                    RegistrationFragment registrationFragment = new RegistrationFragment();
                    transaction.replace(R.id.frame_layout, registrationFragment, "registration");
                //}else {
                //    PlayFragment play =  new PlayFragment();
                //    transaction.replace(R.id.frame_layout, play,"play");
                //}
                transaction.commit();
            }
        };

        RunSplash.schedule(ShowSplash, Delay);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        RegistrationFragment registrationFragment =  new RegistrationFragment();
        transaction.replace(R.id.frame_layout,registrationFragment,"registration");

        transaction.commit();
    }
}
