package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.stephane.rothen.rchrono.R;

/**
 * Created by stéphane on 14/03/2015.
 */
public class Frag_AlertDialog_Suppr extends DialogFragment {

    private Frag_AlertDialog_Suppr_Callback mCallback;

    public static Frag_AlertDialog_Suppr newInstance(String title) {
        Frag_AlertDialog_Suppr frag = new Frag_AlertDialog_Suppr();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Frag_AlertDialog_Suppr_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_AlertDialog_Suppr_Callback");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.suppr)
                .setTitle(title)
                .setPositiveButton(R.string.alertDialog_suppr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.doDialogFragSupprClick();
                    }
                })
                .setNegativeButton(R.string.alertDialog_annuler, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.doDialogFragCancelClick();
                    }
                })
                .create();

    }

    /**
     * Frag_AlertDialog_Suppr_Callback
     * <p>Cette interface permet de gerer les callback du fragment vers l'activity</p>
     */
    public interface Frag_AlertDialog_Suppr_Callback {
        public void doDialogFragSupprClick();

        public void doDialogFragCancelClick();
    }
}
