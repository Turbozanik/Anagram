package by.example.roman.anagram;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Roman on 05.02.2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {


    private SharedPreferences sharedpreferences;
    
    SharedPreferences.Editor editor;
    RelativeLayout           choseDifficultyRl;
    RelativeLayout           chronometerRl;
    RelativeLayout           choseModeRl;
    RelativeLayout           inviteFriendRl;
    RelativeLayout           badgesCountRl;
    String                   currentDifficulty;
    String                   timerTime;
    String                   currentMode;
    String                   currentBadgesCount;
    TextView                 difficultyTv;
    TextView                 chronometerTimeChoseTv;
    TextView                 modeChoseTv;
    TextView                 settingsBadgesTv;
    ImageView                settignsBackArrowIv;
    ImageView                settingsIv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        choseDifficultyRl       = (RelativeLayout) view.findViewById(R.id.choseDifficultyRl);
        chronometerRl           = (RelativeLayout) view.findViewById(R.id.chronometerRl);
        choseModeRl             = (RelativeLayout) view.findViewById(R.id.modeRl);
        inviteFriendRl          = (RelativeLayout) view.findViewById(R.id.inviteFriendRl);
        badgesCountRl           = (RelativeLayout) view.findViewById(R.id.settingsBadgesCountRl);
        difficultyTv            = (TextView) view.findViewById(R.id.levelDifficultyTv);
        chronometerTimeChoseTv  = (TextView)view.findViewById(R.id.chronometerTimeChoseTv);
        modeChoseTv             = (TextView)view.findViewById(R.id.modeChoseTv);
        settingsBadgesTv        = (TextView)view.findViewById(R.id.settingsBadgesTv);
        settignsBackArrowIv     = (ImageView)view.findViewById(R.id.settingsBackArrowIv);
        settingsIv              = (ImageView)view.findViewById(R.id.settingsIv);

        sharedpreferences = getActivity().getSharedPreferences(UtilityClass.anagramzSharedPrefKey, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        currentBadgesCount = sharedpreferences.getString(UtilityClass.BADGES_COUNT,"0");
        if(!currentBadgesCount.equals("0")){
            badgesCountRl.setBackgroundResource(R.drawable.settings_some_badges_shape);
            settingsBadgesTv.setText(currentBadgesCount + " " + getString(R.string.badges));
            badgesCountRl.setAlpha(1);
        }

        difficultyTv.setText(sharedpreferences.getString(UtilityClass.DIFICULTY_LEVEL, getString(R.string.basic)));
        modeChoseTv.setText(sharedpreferences.getString(UtilityClass.CURRENT_MODE, getString(R.string.offline)));
        chronometerTimeChoseTv.setText(sharedpreferences.getString(UtilityClass.TIMER_ON_OFF_TIME, getString(R.string.off)));


        inviteFriendRl.setOnClickListener(this);
        choseModeRl.setOnClickListener(this);
        chronometerRl.setOnClickListener(this);
        choseDifficultyRl.setOnClickListener(this);
        settignsBackArrowIv.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.choseDifficultyRl: {
                showDifficultyAlert();
                break;
            }
            case R.id.chronometerRl:{
                showChronometerAlert();
             break;
            }
            case R.id.modeRl:{
                showModeAlert();
                break;
            }
            case R.id.inviteFriendRl:{
                showInviteAlert();
                break;
            }
            case R.id.settingsBackArrowIv:{
                ((MainActivity)getActivity()).onBackPressed();
                break;
            }
        }
    }

    private void showDifficultyAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout       = new LinearLayout(getActivity());


        LayoutInflater inflater   = getActivity().getLayoutInflater();
        View view                 = inflater.inflate(R.layout.chose_difficulty_dialog_title, null);
        View view1                = inflater.inflate(R.layout.difficulty_dialog_content, null);

        final CheckBox basic      = (CheckBox)view1.findViewById(R.id.basic);
        final CheckBox champion   = (CheckBox)view1.findViewById(R.id.champion);
        final CheckBox superstar  = (CheckBox)view1.findViewById(R.id.superstar);

        if(!sharedpreferences.getString(UtilityClass.USER_TOP_DIFFCICULTY_LEVEL, "Basic").equals(getString(R.string.champion))){
            champion.setEnabled(false);
        }else{
            champion.setEnabled(true);
        }
        if(!sharedpreferences.getString(UtilityClass.USER_TOP_DIFFCICULTY_LEVEL, "Basic").equals(getString(R.string.superstar))){
            superstar.setEnabled(false);
        }else {
            superstar.setEnabled(true);
        }

        basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                champion.setChecked(false);
                superstar.setChecked(false);
                champion.setTextColor(getResources().getColor(R.color.editTextColor));
                superstar.setTextColor(getResources().getColor(R.color.editTextColor));

                editor.putString(UtilityClass.DIFICULTY_LEVEL, getString(R.string.basic));
                editor.commit();
                if (basic.isChecked()) {
                    basic.setTextColor(getResources().getColor(R.color.playButtonColor));
                    currentDifficulty = getString(R.string.basic);
                } else {
                    basic.setTextColor(getResources().getColor(R.color.editTextColor));
                }
            }
        });
        champion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                basic.setChecked(false);
                superstar.setChecked(false);
                basic.setTextColor(getResources().getColor(R.color.editTextColor));
                superstar.setTextColor(getResources().getColor(R.color.editTextColor));
                editor.putString(UtilityClass.DIFICULTY_LEVEL, getString(R.string.basic));

                if (champion.isChecked()) {
                    champion.setTextColor(getResources().getColor(R.color.playButtonColor));
                    currentDifficulty = getString(R.string.champion);
                } else {
                    champion.setTextColor(getResources().getColor(R.color.editTextColor));
                }
            }
        });
        superstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                champion.setChecked(false);
                basic.setChecked(false);
                basic.setTextColor(getResources().getColor(R.color.editTextColor));
                champion.setTextColor(getResources().getColor(R.color.editTextColor));
                editor.putString(UtilityClass.DIFICULTY_LEVEL, getString(R.string.basic));
                if (superstar.isChecked()) {
                    superstar.setTextColor(getResources().getColor(R.color.playButtonColor));
                    currentDifficulty = getString(R.string.superstar);
                } else {
                    superstar.setTextColor(getResources().getColor(R.color.editTextColor));
                }
            }
        });

        if(!basic.isChecked() && !champion.isChecked() && !superstar.isChecked()){
            difficultyTv.setText(getString(R.string.basic));
            editor.putString(UtilityClass.DIFICULTY_LEVEL,getString(R.string.basic));
            editor.commit();
        }

        alert.setCustomTitle(view);

        layout.setOrientation(LinearLayout.VERTICAL);
        alert.setView(view1);
        alert.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!basic.isChecked() && !champion.isChecked() && !superstar.isChecked()){
                    difficultyTv.setText(getString(R.string.basic));
                    editor.putString(UtilityClass.DIFICULTY_LEVEL,getString(R.string.basic));
                    editor.commit();
                }else {
                    editor.putString(UtilityClass.DIFICULTY_LEVEL,currentDifficulty);
                    editor.commit();
                    difficultyTv.setText(currentDifficulty);
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();

    }

    private void showChronometerAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout       = new LinearLayout(getActivity());


        LayoutInflater inflater   = getActivity().getLayoutInflater();
        View titleView            = inflater.inflate(R.layout.chronometer_on_off_dialog_title, null);
        View contentView          = inflater.inflate(R.layout.chronometer_on_off_dialog_content, null);

        final RelativeLayout turnOnLayout = (RelativeLayout)contentView.findViewById(R.id.contentChronometerDialogRl);
        final CheckBox off              = (CheckBox)contentView.findViewById(R.id.off);
        final TextView on               = (TextView)contentView.findViewById(R.id.on);
        final ImageView threeDotsIv     = (ImageView)contentView.findViewById(R.id.threeDotsIv);
        final View firstSeparator       = contentView.findViewById(R.id.onOffAndTimeSeparator);
        final View timeSeparator        = contentView.findViewById(R.id.timeSeparator);
        final View endSeparator         = contentView.findViewById(R.id.endSeparator);
        final CheckBox tenSeconds       = (CheckBox)contentView.findViewById(R.id.tenSecondsTimerCheckBox);
        final CheckBox twentyFiveSeconds= (CheckBox)contentView.findViewById(R.id.twenty_fiveSecondsTimerCheckBox);

        turnOnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                off.setChecked(false);
                off.setTextColor(getResources().getColor(R.color.editTextColor));
                if(threeDotsIv.getVisibility()!=View.GONE){
                    threeDotsIv.setVisibility(View.GONE);

                    on.setTextColor(getResources().getColor(R.color.playButtonColor));

                    firstSeparator.setVisibility(View.VISIBLE);
                    timeSeparator.setVisibility(View.VISIBLE);
                    endSeparator.setVisibility(View.VISIBLE);
                    tenSeconds.setVisibility(View.VISIBLE);
                    twentyFiveSeconds.setVisibility(View.VISIBLE);
                }else{
                    on.setTextColor(getResources().getColor(R.color.editTextColor));

                    threeDotsIv.setVisibility(View.VISIBLE);
                    firstSeparator.setVisibility(View.GONE);
                    timeSeparator.setVisibility(View.GONE);
                    endSeparator.setVisibility(View.GONE);
                    tenSeconds.setVisibility(View.GONE);
                    twentyFiveSeconds.setVisibility(View.GONE);

                }
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerTime = "off";
                off.setTextColor(getResources().getColor(R.color.playButtonColor));
                on.setTextColor(getResources().getColor(R.color.editTextColor));

                threeDotsIv.setVisibility(View.VISIBLE);
                firstSeparator.setVisibility(View.GONE);
                timeSeparator.setVisibility(View.GONE);
                endSeparator.setVisibility(View.GONE);
                tenSeconds.setVisibility(View.GONE);
                twentyFiveSeconds.setVisibility(View.GONE);

                if (off.isChecked()) {
                    off.setTextColor(getResources().getColor(R.color.playButtonColor));
                    currentDifficulty = getString(R.string.champion);
                } else {
                    off.setTextColor(getResources().getColor(R.color.editTextColor));
                }
                editor.putString(UtilityClass.TIMER_ON_OFF_TIME, getString(R.string.off));
                editor.commit();
            }
        });

        tenSeconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twentyFiveSeconds.setChecked(false);
                twentyFiveSeconds.setTextColor(getResources().getColor(R.color.editTextColor));
                if (tenSeconds.isChecked()) {
                    tenSeconds.setTextColor(getResources().getColor(R.color.playButtonColor));
                    timerTime = "10";
                } else {
                    tenSeconds.setTextColor(getResources().getColor(R.color.editTextColor));
                }
                editor.putString(UtilityClass.TIMER_ON_OFF_TIME, "10s");
                editor.commit();
            }
        });

        twentyFiveSeconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenSeconds.setChecked(false);
                tenSeconds.setTextColor(getResources().getColor(R.color.editTextColor));
                if (twentyFiveSeconds.isChecked()) {
                    twentyFiveSeconds.setTextColor(getResources().getColor(R.color.playButtonColor));
                    timerTime = "25";
                } else {
                    twentyFiveSeconds.setTextColor(getResources().getColor(R.color.editTextColor));
                }
                editor.putString(UtilityClass.TIMER_ON_OFF_TIME, "25s");
                editor.commit();
            }
        });

        firstSeparator.setVisibility(View.GONE);
        timeSeparator.setVisibility(View.GONE);
        endSeparator.setVisibility(View.GONE);
        tenSeconds.setVisibility(View.GONE);
        twentyFiveSeconds.setVisibility(View.GONE);

        alert.setCustomTitle(titleView);

        layout.setOrientation(LinearLayout.VERTICAL);
        alert.setView(contentView);
        alert.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!off.isChecked() && !twentyFiveSeconds.isChecked() && !tenSeconds.isChecked()){
                    chronometerTimeChoseTv.setText(getString(R.string.off));
                    editor.putString(UtilityClass.TIMER_ON_OFF_TIME,getString(R.string.off));
                    editor.commit();
                }else {
                    chronometerTimeChoseTv.setText(timerTime);
                    //chronometerTimeChoseTv.setText(sharedpreferences.getString(UtilityClass.TIMER_ON_OFF_TIME,getString(R.string.off)));
                }
            }
        });

        alert.show();

    }


    private void showModeAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout       = new LinearLayout(getActivity());


        LayoutInflater inflater   = getActivity().getLayoutInflater();
        View titleView            = inflater.inflate(R.layout.mode_chose_dialog_title, null);
        View contentView          = inflater.inflate(R.layout.mode_chose_dialog_content, null);

        final CheckBox offlineMode      = (CheckBox)contentView.findViewById(R.id.modeDialogOfflineMode);
        final TextView duelMode         = (TextView)contentView.findViewById(R.id.modeDialogDuelMode);

        offlineMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMode = "Offline";
                modeChoseTv.setText(currentMode);

                editor.putString(UtilityClass.CURRENT_MODE, currentMode);
                editor.commit();
                duelMode.setTextColor(getResources().getColor(R.color.editTextColor));
                if (offlineMode.isChecked()) {
                    offlineMode.setTextColor(getResources().getColor(R.color.playButtonColor));
                    currentMode = getString(R.string.offline);
                } else {
                    offlineMode.setTextColor(getResources().getColor(R.color.editTextColor));
                }
            }
        });

        duelMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                duelMode.setTextColor(getResources().getColor(R.color.playButtonColor));
                offlineMode.setTextColor(getResources().getColor(R.color.editTextColor));
                showPaymentAlert();
            }
        });

        alert.setCustomTitle(titleView);

        layout.setOrientation(LinearLayout.VERTICAL);
        alert.setView(contentView);
        alert.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }

    private void showPaymentAlert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout       = new LinearLayout(getActivity());

        LayoutInflater inflater   = getActivity().getLayoutInflater();
        View titleView            = inflater.inflate(R.layout.mode_chose_dialog_title, null);
        View contentView          = inflater.inflate(R.layout.payment_dialg_content, null);

        alert.setCustomTitle(titleView);

        layout.setOrientation(LinearLayout.VERTICAL);
        alert.setView(contentView);

        alert.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMode = "Offline";
                modeChoseTv.setText(currentMode);

                editor.putString(UtilityClass.CURRENT_MODE, currentMode);
                editor.commit();
                dialog.dismiss();
            }
        });
        alert.setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMode = "Duel (2 players)";
                modeChoseTv.setText(currentMode);

                editor.putString(UtilityClass.CURRENT_MODE, currentMode);
                editor.commit();
            }
        });

        alert.show();

    }

    private void showInviteAlert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout       = new LinearLayout(getActivity());


        LayoutInflater inflater   = getActivity().getLayoutInflater();
        View titleView            = inflater.inflate(R.layout.invite_friend_dialog_title, null);
        View contentView          = inflater.inflate(R.layout.invite_friend_dialog_content, null);

        alert.setCustomTitle(titleView);

        layout.setOrientation(LinearLayout.VERTICAL);
        alert.setView(contentView);

        alert.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }

}
