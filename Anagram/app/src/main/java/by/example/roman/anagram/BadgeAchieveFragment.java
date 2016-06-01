package by.example.roman.anagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import by.example.roman.anagram.async.UserUpdateCompleted;
import by.example.roman.anagram.async.UserUpdateHelper;

/**
 * Created by Roman on 05.02.2016.
 */
public class BadgeAchieveFragment extends Fragment implements View.OnClickListener,UserUpdateCompleted {

    String wordsOrNumbers;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    UserUpdateHelper helper;

    ConnectivityManager connManager;
    NetworkInfo mWifi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.badge_achieved, container, false);

        connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Bundle bundle = getArguments();
        wordsOrNumbers = bundle.getString("wordsOrNumbers", "0");

        sharedPreferences  = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, 0);
        editor = sharedPreferences.edit();

        helper = new UserUpdateHelper(getActivity());
        helper.delegate = this;

        if (mWifi.isConnected()) {
            String badgesCount = sharedPreferences.getString(UtilityClass.BADGES_COUNT, "0");
            int count = Integer.parseInt(badgesCount);
            count++;
            editor.putString(UtilityClass.BADGES_COUNT, String.valueOf(count));
            editor.commit();
            helper.execute();
        }else {
            String badgesCount = sharedPreferences.getString(UtilityClass.BADGES_COUNT, "0");
            int count = Integer.parseInt(badgesCount);
            count++;
            editor.putString(UtilityClass.BADGES_COUNT, String.valueOf(count));
            editor.commit();
        }
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.scenarioCOmpleteRl:{//delete
                if(wordsOrNumbers.equals("numbers")){
                    transaction.replace(R.id.frame_layout, new GameNumbersFragment());
                    transaction.commit();
                }else {
                    transaction.replace(R.id.frame_layout, new GameWordsFragment());
                    transaction.commit();
                }
                break;
            }
        }
}

    @Override
    public void dataSent(List<User> values) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        if(wordsOrNumbers.equals("numbers")){
            transaction.replace(R.id.frame_layout, new GameNumbersFragment());
            transaction.commit();
        }else {
            transaction.replace(R.id.frame_layout, new GameWordsFragment());
            transaction.commit();
        }
    }
}