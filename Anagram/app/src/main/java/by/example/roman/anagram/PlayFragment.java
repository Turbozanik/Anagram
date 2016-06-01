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
public class PlayFragment extends Fragment implements View.OnClickListener{

    Button playButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play, container, false);
        playButton = (Button) view.findViewById(R.id.playButton);

        playButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.playButton:{
                transaction.replace(R.id.frame_layout,new MenuFragment(),"menu_fragment");
                transaction.commit();
                break;
            }
        }

    }
}
