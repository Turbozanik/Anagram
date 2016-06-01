package by.example.roman.anagram;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.Card;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Roman on 06.02.2016.
 */
public class CurrentProgressBadgeFragment extends Fragment implements View.OnClickListener {

    Button continuePlaying;

    ShareDialog shareDialog;

    ImageView twitterSendData;
    ImageView facebookSendData;
    ImageView trophyIv;

    TextView scenarioCompleteTv;

    String championIsAvailable;
    String superstarAvailable;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.completed_levels_badge, container, false);

        trophyIv = (ImageView)view.findViewById(R.id.scenarioCompleteBadgeIv);

        sharedpreferences = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        championIsAvailable = sharedpreferences.getString(UtilityClass.IS_CHAMPION_ACTIVE, "false");
        superstarAvailable = sharedpreferences.getString(UtilityClass.IS_SUPERSTAR_ACTIVE, "false");
        if(sharedpreferences.getString(UtilityClass.IS_SUPERSTAR_COMPLETED,"false").equals("true")){
            trophyIv.setImageResource(R.drawable.superrstar_complete);
            scenarioCompleteTv.setText(R.string.champion);
        }else if(sharedpreferences.getString(UtilityClass.IS_CHAMPION_COMPLETED,"false").equals("true")){
            trophyIv.setImageResource(R.drawable.champion_cup);
            scenarioCompleteTv.setText(R.string.basicLevelComplete);
        }else if(sharedpreferences.getString(UtilityClass.IS_BASIC_COMPLETED,"false").equals("true")){
            trophyIv.setImageResource(R.drawable.book_basic);
        }

        twitterSendData = (ImageView)view.findViewById(R.id.sendDataTwitterIv);
        facebookSendData = (ImageView)view.findViewById(R.id.sendDataFacebookIv);
        continuePlaying = (Button)view.findViewById(R.id.continuePlayingButton);

        shareDialog = new ShareDialog(getActivity());

        twitterSendData.setOnClickListener(this);
        facebookSendData.setOnClickListener(this);
        continuePlaying.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.continuePlayingButton:{
                transaction.replace(R.id.frame_layout,new MenuFragment()).addToBackStack("");
                transaction.commit();
                break;
            }
            case R.id.sendDataFacebookIv:{
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("AnagramZ")
                            .setContentDescription("I got :" +sharedpreferences.getString(UtilityClass.BADGES_COUNT,"0")+" "+getString(R.string.badges))
                            .setImageUrl(Uri.parse(getString(R.string.linkToShareImage)))
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .build();

                    shareDialog.show(linkContent);
                }
                break;
            }case  R.id.sendDataTwitterIv:{
                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.superrstar_complete)
                        + '/' + getResources().getResourceTypeName(R.drawable.superrstar_complete)
                        + '/' + getResources().getResourceEntryName(R.drawable.superrstar_complete));
                TweetComposer.Builder builder = null;
                try {
                    builder = new TweetComposer.Builder(getActivity())
                            .text("AnagramZ twitt")
                            .url(new URL("http://www.ebay.com"))
                            .image(imageUri);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if(builder!=null) {
                    builder.show();
                }
                break;
            }
        }
    }
}
