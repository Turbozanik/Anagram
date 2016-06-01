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
import android.widget.ImageView;

/**
 * Created by Roman on 05.02.2016.
 */
public class PreWordsGameFragment extends Fragment implements View.OnClickListener{
    Button playButton;
    ImageView back;
    ImageView settings;

    FragmentTransaction transaction;

    private String anagramzSharedPrefKey = "anagramz";
    private SharedPreferences sharedpreferences;
    private String numbersOrWords = null;

    SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pre_word_game, container, false);
        playButton = (Button) view.findViewById(R.id.preWordGameButton);
        back = (ImageView)view.findViewById(R.id.preWordGameBackArrowIv);
        settings = (ImageView)view.findViewById(R.id.preWordGameSettingsIv);

        transaction  = getActivity().getFragmentManager().beginTransaction();

        sharedpreferences = getActivity().getSharedPreferences(anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        numbersOrWords = "numbers";

        back.setOnClickListener(this);
        settings.setOnClickListener(this);
        playButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.preWordGameButton:{
                if(sharedpreferences.getString(UtilityClass.CURRENT_MODE,getString(R.string.offline)).equals(getString(R.string.offline))) {
                    transaction.replace(R.id.frame_layout, new GameWordsFragment()).addToBackStack("");
                    transaction.commit();
                }else{
                    Bundle args = new Bundle();
                    args.putString("wordsOrNumbers", numbersOrWords);
                    DuelGameFragment duelGameFragment = new DuelGameFragment();
                    duelGameFragment.setArguments(args);
                    transaction.replace(R.id.frame_layout, duelGameFragment).addToBackStack("");
                    transaction.commit();
                }
                break;
            }
            case  R.id.preWordGameBackArrowIv:{
                ((MainActivity)getActivity()).onBackPressed();
                break;
            }
            case R.id.preWordGameSettingsIv:{
                transaction.replace(R.id.frame_layout,new SettingsFragment()).addToBackStack("");
                transaction.commit();
                break;
            }
        }

    }
    public void refresh(){
        transaction.detach(this).attach(this).commit();
    }
}
