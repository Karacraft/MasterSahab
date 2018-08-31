package com.karacraft.mastersahab;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 05 2015
 * File Name        CustomAlertDialogFragment.java
 * Comments
 */
public class CustomAlertDialogFragment extends DialogFragment {

    public static CustomAlertDialogFragment newInstance(String message){
        CustomAlertDialogFragment frag = new CustomAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.MESSAGE,message);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Get the Title from the Bundle
        //int title = getArguments().getInt("title");
        String msg = getArguments().getString(Constants.MESSAGE);
        return new AlertDialog.Builder(getActivity())
                //Set Dialog Icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                        //.setTitle(title)
                .setTitle("Alert").setMessage(msg)

                        //Positive Button
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent i = getActivity().getIntent();
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                        /** ONe Possibliity
                         * ((SingleFragmentActivity)getActivity()).setPositiveButton()
                         */
                    }
                })

                        //Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent i = getActivity().getIntent();
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, i);
                    }
                }).create();
        }
}
