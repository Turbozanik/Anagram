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
public class TwitterSignInFragment extends Fragment implements View.OnClickListener{

    Button okButton;
    Button cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.twitter_sign_in, container, false);

        okButton = (Button)view.findViewById(R.id.twitterRegWindowButton);
        cancelButton = (Button)view.findViewById(R.id.twitterRegCancel);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();



        switch (v.getId()){
            case R.id.twitterRegWindowButton:{
                transaction.replace(R.id.frame_layout,new PlayFragment()).addToBackStack("");
                transaction.commit();
                break;
            }
            case R.id.twitterRegCancel:{
                transaction.replace(R.id.frame_layout, new RegistrationFragment());
                transaction.commit();
                break;
            }
        }
    }
}
