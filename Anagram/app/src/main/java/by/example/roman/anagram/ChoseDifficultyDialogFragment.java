package by.example.roman.anagram;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Roman on 08.02.2016.
 */
public class ChoseDifficultyDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final boolean[] mCheckedItems = { false, true, false };
        final String[] checkCatsName = { "Васька", "Рыжик", "Мурзик" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        return null;
    }
}
