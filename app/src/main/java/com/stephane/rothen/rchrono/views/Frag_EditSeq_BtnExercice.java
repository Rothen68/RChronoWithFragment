package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.Exercice;

/**
 * Created by stéphane on 15/03/2015.
 */
public class Frag_EditSeq_BtnExercice extends Fragment {
    public static final String FRAG_EDITSEQ_BTNEXERCICE_CALLBACK = "FRAG_EDITSEQ_BTNEXERCICE_CALLBACK";


    private EditText mEtxtNom;
    private Exercice mEtxtRepetitions;
    private ToggleButton mTbNom;
    private ToggleButton mTbDuree;

    /**
     * Instance de l'interface OnClickListener
     */
    private Frag_Bouton_Callback mCallback;


    public Frag_EditSeq_BtnExercice() {
    }

    /**
     * Fonction appelée quand le fragment est attaché à son Activity
     *
     * @param activity
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Frag_Bouton_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_Bouton_Callback");
        }


    }

    /**
     * Initialisation de l'interface du fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.editionseq_frag_btnexercice, container, false);
        ((Button) rootView.findViewById(R.id.editionseq_frag_btnexercice_ajouter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickListener(v);
            }
        });
        ((Button) rootView.findViewById(R.id.editionseq_frag_btnexercice_creer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickListener(v);
            }
        });

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
