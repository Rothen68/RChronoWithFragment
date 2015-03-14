package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.stephane.rothen.rchrono.R;

/**
 * Created by stéphane on 14/03/2015.
 */
public class Frag_Dialog_Repetition extends DialogFragment {

    private int mNum;

    private Frag_Dialog_Repetition_Callback mCallback;

    static public Frag_Dialog_Repetition newInstance(int num) {
        Frag_Dialog_Repetition f = new Frag_Dialog_Repetition();


        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Frag_Dialog_Repetition_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_Dialog_Repetition_Callback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_frag_repetition, container, false);
        final NumberPicker numberPicker = (NumberPicker) v.findViewById(R.id.dialFragRepNumPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(1000);
        numberPicker.setValue(mNum);
        getDialog().setTitle(R.string.dialogRep_Repetition);
        // Watch for button clicks.
        Button button = (Button) v.findViewById(R.id.dialFragRepbtnOk);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                mCallback.onClickListener(v, numberPicker.getValue());
                getDialog().dismiss();

            }
        });

        return v;
    }

    /**
     * Frag_Dialog_Repetition_Callback
     * <p>Cette interface permet de gerer les callback du fragment vers l'activity</p>
     */
    public interface Frag_Dialog_Repetition_Callback {
        public void onClickListener(View v, int valeur);
    }
}

