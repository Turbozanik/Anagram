package by.example.roman.anagram;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Roman on 04.02.2016.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{
    Button wordGame;
    Button numberGame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_choice, container, false);

        wordGame = (Button)view.findViewById(R.id.namesAndWordsButton);
        numberGame = (Button)view.findViewById(R.id.numberAndDatesButton);

        wordGame.setOnClickListener(this);
        numberGame.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.namesAndWordsButton:{
                transaction.replace(R.id.frame_layout,new PreWordsGameFragment(),"wordGame").addToBackStack("");
                transaction.commit();
                break;
            }
            case R.id.numberAndDatesButton:{
                transaction.replace(R.id.frame_layout,new PreNumberGameFragment(),"numberGame").addToBackStack("");
                transaction.commit();
            }
        }
    }
}
