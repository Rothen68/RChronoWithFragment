package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.stephane.rothen.rchrono.R;

/**
 * Fragment affichant les détails de la séquence dans la fenetre Edition de la séquence
 */
public class Frag_EditSeq_Detail extends android.support.v4.app.Fragment {
    public static final String FRAG_EDITSEQ_DEATIL_CALLBACK = "FRAG_EDITSEQ_DEATIL_CALLBACK";


    private EditText mEtxtNom;
    private EditText mEtxtRepetitions;
    private ToggleButton mTbNom;
    private boolean mEtatTbNom = false;
    private ToggleButton mTbDuree;
    private boolean mEtatTbDuree = false;
    /**
     * Instance de l'interface OnClickListener
     */
    private Frag_EditSeq_Detail_Callback mCallback;

    public Frag_EditSeq_Detail() {
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
            mCallback = (Frag_EditSeq_Detail_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_EditSeq_Detail_Callback");
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
        View rootView = inflater.inflate(R.layout.editionseq_frag_detail, container, false);
        mEtxtNom = (EditText) rootView.findViewById(R.id.editionseq_frag_detail_etxtNom);
        mEtxtRepetitions = (EditText) rootView.findViewById(R.id.editionseq_frag_detail_etxtRepetitions);
        mTbNom = (ToggleButton) rootView.findViewById(R.id.editionseq_frag_detail_tbNom);
        mTbNom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbNom = isChecked;
            }
        });
        mTbDuree = (ToggleButton) rootView.findViewById(R.id.editionseq_frag_detail_tbDuree);
        mTbDuree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbDuree = isChecked;
            }
        });

        return rootView;
    }

    public String getTxtNom() {
        return mEtxtNom.getText().toString();
    }

    public void setTxtNom(String nom) {
        mEtxtNom.setText(nom);
    }

    public int getTxtRepetition() {
        return Integer.valueOf(mEtxtRepetitions.getText().toString());
    }

    public void setTxtRepetition(int repetition) {
        mEtxtRepetitions.setText(String.valueOf(repetition));
    }

    public boolean getTbNom() {
        return mEtatTbNom;
    }

    public void setTbNom(boolean etat) {
        mTbNom.setChecked(etat);
        mEtatTbNom = etat;
    }

    public boolean getTbDuree() {
        return mEtatTbDuree;
    }

    public void setTbDuree(boolean etat) {
        mTbDuree.setChecked(etat);
        mEtatTbDuree = etat;
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

    /**
     * interface OnClickListener
     * <p>Cette interface permet d'envoyer l'évenement OnClick d'un Button vers la classe activité qui a lancé le fragment</p>
     */
    public interface Frag_EditSeq_Detail_Callback {
        /**
         * Evenement OnClick sur un button
         *
         * @param v View sur laquelle l'utilisateur aa cliqué
         */
        public void onClickListener(View v);

        public void onTextChange(View v);
    }


}
