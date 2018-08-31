package com.karacraft.mastersahab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 05 2015
 * File Name        CustomAlertDialogOptionsFragment.java
 * Comments
 */
public class CustomAlertDialogOptionsFragment extends DialogFragment {

    public static CustomAlertDialogOptionsFragment newInstance(String message){
        CustomAlertDialogOptionsFragment frag = new CustomAlertDialogOptionsFragment();
        Bundle args = new Bundle();
        //args.putInt("title",title);
        args.putString(Constants.MESSAGE, message);
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
                    .setIcon(android.R.drawable.ic_dialog_info)
                            //.setTitle(title)
                    .setTitle(msg)
                    .setItems(R.array.order_status, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent i = getActivity().getIntent();
                            i.putExtra(Constants.KEY, true);
                            i.putExtra("selection", which + 1);
                            getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK, i);
                        }
                    })
                    .create();
                }
}
