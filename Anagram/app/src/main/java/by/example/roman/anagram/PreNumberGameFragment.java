package by.example.roman.anagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Roman on 04.02.2016.
 */
public class PreNumberGameFragment extends Fragment implements View.OnClickListener{

    Button playButton;
    ImageView back;
    ImageView settings;

    private String anagramzSharedPrefKey = "anagramz";
    private SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private String numbersOrWords = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pre_number_game, container, false);
        playButton = (Button) view.findViewById(R.id.preNumberGameNumbersAndDates);
        back = (ImageView)view.findViewById(R.id.preNumberGameBackArrowIv);
        settings = (ImageView)view.findViewById(R.id.preNumberGameSettingsIv);

        sharedpreferences = getActivity().getSharedPreferences(anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        numbersOrWords = "numbers";

        playButton.setOnClickListener(this);
        back.setOnClickListener(this);
        settings.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.preNumberGameNumbersAndDates:{
                if(sharedpreferences.getString(UtilityClass.CURRENT_MODE,getString(R.string.offline)).equals(getString(R.string.offline))) {
                    transaction.replace(R.id.frame_layout, new GameNumbersFragment());
                    transaction.commit();
                }else{
                    Bundle args = new Bundle();
                    args.putString("wordsOrNumbers", numbersOrWords);
                    DuelGameFragment duelGameFragment = new DuelGameFragment();
                    duelGameFragment.setArguments(args);
                    transaction.replace(R.id.frame_layout, duelGameFragment);
                    transaction.commit();
                }
                break;
            }
            case R.id.preNumberGameBackArrowIv:{
                ((MainActivity)getActivity()).onBackPressed();
                break;
            }
            case R.id.preNumberGameSettingsIv:{
                transaction.replace(R.id.frame_layout,new SettingsFragment());
                transaction.commit();
                break;
            }
        }

    }

}
