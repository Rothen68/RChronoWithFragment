package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stephane.rothen.rchrono.R;

/**
 * Fenetre popup Enregistrement sequence
 * Created by stéphane on 15/03/2015.
 */
public class Frag_Dialog_EnregistrementSeq extends DialogFragment

{


    private Frag_Dialog_EnregistrementSeq_Callback mCallback;

    static public Frag_Dialog_EnregistrementSeq newInstance() {
        Frag_Dialog_EnregistrementSeq f = new Frag_Dialog_EnregistrementSeq();


        // Supply num input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Frag_Dialog_EnregistrementSeq_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_Dialog_EnregistrementSeq_Callback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_frag_enregistrerseq, container, false);
        getDialog().setTitle(R.string.dialogEnrSeq_Modifications);
        ((Button) v.findViewById(R.id.dialFragEnrSeqNouvelle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRetourDialogEnrSeq(v);
                getDialog().dismiss();
            }
        });
        ((Button) v.findViewById(R.id.dialFragEnrSeqEcraser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRetourDialogEnrSeq(v);
                getDialog().dismiss();
            }
        });
        ((Button) v.findViewById(R.id.dialFragEnrSeqCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRetourDialogEnrSeq(v);
                getDialog().dismiss();
            }
        });
        return v;
    }

    /**
     * Frag_Dialog_Repetition_Callback
     * <p>Cette interface permet de gerer les callback du fragment vers l'activity</p>
     */
    public interface Frag_Dialog_EnregistrementSeq_Callback {
        public void onRetourDialogEnrSeq(View v);
    }
}
