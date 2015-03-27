package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.stephane.rothen.rchrono.Fonctions;
import com.stephane.rothen.rchrono.R;

/**
 * Created by stéphane on 27/03/2015.
 */
public class Frag_EditEx_Detail extends Fragment {
    public static final String FRAG_EDITEX_DETAIL_CALLBACK = "FRAG_EDITEX_DETAIL_CALLBACK";


    private EditText mEtxtNom;
    private EditText mEtxtDescription;
    private EditText mEtxtDuree;
    private int mDuree;

    private ToggleButton mTbNom;
    private boolean mEtatTbNom = false;
    private ToggleButton mTbDuree;
    private boolean mEtatTbDuree = false;

    private ToggleButton mTbVibreur;
    private boolean mEtatTbVibreur = false;
    private ToggleButton mTbPopup;
    private boolean mEtatTbPopup = false;
    private ToggleButton mTbSonnerie;
    private boolean mEtatTbSonnerie = false;

    private EditText mEtxtSonnerie;


    /**
     * Instance de l'interface OnClickListener
     */
    private Frag_EditEx_Detail_Callback mCallback;

    public Frag_EditEx_Detail() {
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
            mCallback = (Frag_EditEx_Detail_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_EditEx_Detail_Callback");
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
        View rootView = inflater.inflate(R.layout.editionex_frag_detail, container, false);
        mEtxtNom = (EditText) rootView.findViewById(R.id.editionex_frag_detail_etxtNom);
        mEtxtDescription = (EditText) rootView.findViewById(R.id.editionex_frag_detail_etxtDescription);
        mEtxtDuree = (EditText) rootView.findViewById(R.id.editionex_frag_detail_etxtDuree);
        mDuree = 0;

        mTbNom = (ToggleButton) rootView.findViewById(R.id.editionex_frag_detail_tbNom);
        mTbNom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbNom = isChecked;
            }
        });
        mTbDuree = (ToggleButton) rootView.findViewById(R.id.editionex_frag_detail_tbDuree);
        mTbDuree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbDuree = isChecked;
            }
        });

        mTbVibreur = (ToggleButton) rootView.findViewById(R.id.editionex_frag_detail_tbVibreur);
        mTbVibreur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbVibreur = isChecked;
            }
        });
        mTbPopup = (ToggleButton) rootView.findViewById(R.id.editionex_frag_detail_tbPopup);
        mTbPopup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbPopup = isChecked;
            }
        });
        mTbSonnerie = (ToggleButton) rootView.findViewById(R.id.editionex_frag_detail_tbSonnerie);
        mTbSonnerie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbSonnerie = isChecked;
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

    public String getTxtDescription() {
        return mEtxtDescription.getText().toString();
    }

    public void setTxtDescription(String description) {
        mEtxtDescription.setText(description);
    }

    public int getTxtDuree() {
        return mDuree;
    }

    public void setTxtDuree(int duree) {
        mDuree = duree;
        mEtxtDuree.setText(Fonctions.convertSversHMSSansZeros(duree));
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

    public boolean getTbVibreur() {
        return mEtatTbVibreur;
    }

    public void setTbVibreur(boolean etat) {
        mTbVibreur.setChecked(etat);
        mEtatTbVibreur = etat;
    }

    public boolean getTbPopup() {
        return mEtatTbPopup;
    }

    public void setTbPopup(boolean etat) {
        mTbPopup.setChecked(etat);
        mEtatTbPopup = etat;
    }

    public boolean getTbSonnerie() {
        return mEtatTbSonnerie;
    }

    public void setTbSonnerie(boolean etat) {
        mTbSonnerie.setChecked(etat);
        mEtatTbSonnerie = etat;
    }

    public String getTxtSonnerie() {
        return mEtxtSonnerie.getText().toString();
    }

    public void setTxtSonnerie(String texte) {
        mEtxtSonnerie.setText(texte);
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
    public interface Frag_EditEx_Detail_Callback {
        /**
         * Evenement OnClick sur un button
         *
         * @param v View sur laquelle l'utilisateur aa cliqué
         */
        public void onClickListener(View v);

        public void onTextChange(View v);
    }


}
