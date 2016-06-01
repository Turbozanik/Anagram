package by.example.roman.anagram;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Roman on 09.02.2016.
 */
public class DuelResultFragment extends Fragment implements View.OnClickListener {
    private int duelsCount = 1;




    Button continuePlaying;
    Button quitDuelMode;
    ImageView completeDuelBackground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {//add if statement to change content if you win
        View view = inflater.inflate(R.layout.duel_lost_fragment, container, false);

        completeDuelBackground = (ImageView)view.findViewById(R.id.completedDuelBackgroundIv);
        quitDuelMode    = (Button)view.findViewById(R.id.quitDuelButton);
        continuePlaying = (Button)view.findViewById(R.id.continueDuelButton);

        quitDuelMode.setOnClickListener(this);
        continuePlaying.setOnClickListener(this);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.continueDuelButton:{
                if(duelsCount == 0){
                    showBuyDuelsAlert();
                }else {
                    transaction.replace(R.id.frame_layout,new MenuFragment()).addToBackStack("");
                    transaction.commit();
                }
                break;
            }
            case R.id.completedDuelRl:{
                completeDuelBackground.setImageResource(R.drawable.duel_win_background);
                break;
            }
            case R.id.quitDuelButton:{
                transaction.replace(R.id.frame_layout,new SettingsFragment()).addToBackStack("");
                transaction.commit();
                break;
            }
        }
    }
    private void showBuyDuelsAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout       = new LinearLayout(getActivity());


        LayoutInflater inflater   = getActivity().getLayoutInflater();
        View titleView            = inflater.inflate(R.layout.mode_chose_dialog_title, null);
        View contentView          = inflater.inflate(R.layout.buy_more_duels_dialog_content, null);


        alert.setCustomTitle(titleView);

        layout.setOrientation(LinearLayout.VERTICAL);
        alert.setView(contentView);
        alert.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout,new SettingsFragment()).addToBackStack("");
                transaction.commit();
            }
        });
        alert.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //send to messages app
                dialog.dismiss();
            }
        });

        alert.show();

    }

}
