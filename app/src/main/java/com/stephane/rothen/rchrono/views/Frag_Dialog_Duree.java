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
public class Frag_Dialog_Duree extends DialogFragment

{

    private int mNum;

    private Frag_Dialog_Duree_Callback mCallback;

    static public Frag_Dialog_Duree newInstance(int num) {
        Frag_Dialog_Duree f = new Frag_Dialog_Duree();


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
            mCallback = (Frag_Dialog_Duree_Callback) activity;
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
        View v = inflater.inflate(R.layout.dialog_frag_duree, container, false);
        final NumberPicker tpMinute = (NumberPicker) v.findViewById(R.id.dialFragDureePickMinute);
        final NumberPicker tpSecondes = (NumberPicker) v.findViewById(R.id.dialFragDureePickSeconde);

        int minutes = mNum / 60;
        int secondes = mNum - (60 * minutes);

        tpMinute.setMinValue(0);
        tpMinute.setMaxValue(59);
        tpMinute.setValue(minutes);

        tpSecondes.setMinValue(0);
        tpSecondes.setMaxValue(59);
        tpSecondes.setValue(secondes);

        getDialog().setTitle(R.string.dialogDuree_duree);
        // Watch for button clicks.
        Button button = (Button) v.findViewById(R.id.dialFragDureebtnOk);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                int res = tpMinute.getValue() * 60 + tpSecondes.getValue();
                mCallback.onRetourDialogDuree(res);
                getDialog().dismiss();

            }
        });

        return v;
    }

    /**
     * Frag_Dialog_Repetition_Callback
     * <p>Cette interface permet de gerer les callback du fragment vers l'activity</p>
     */
    public interface Frag_Dialog_Duree_Callback {
        public void onRetourDialogDuree(int valeur);
    }
}
