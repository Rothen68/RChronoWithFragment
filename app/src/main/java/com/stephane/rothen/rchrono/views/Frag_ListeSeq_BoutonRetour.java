package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stephane.rothen.rchrono.R;

/**
 * Created by stéphane on 14/03/2015.
 */
public class Frag_ListeSeq_BoutonRetour extends Fragment {

    public static final String FRAG_LISTESEQ_BOUTONAJOUT_CALLBACK = "FRAG_LISTESEQ_BOUTONAJOUT_CALLBACK";

    /**
     * Instance de l'interface OnClickListener
     */
    private Frag_ListeSeq_BoutonRetour_Callback mCallback;

    /**
     * Bouton Retour du fragment
     */
    private Button mBtnRetour;


    public Frag_ListeSeq_BoutonRetour() {
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
            mCallback = (Frag_ListeSeq_BoutonRetour_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements *");
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
        View rootView = inflater.inflate(R.layout.listeseq_frag_bouton_retour, container, false);
        mBtnRetour = (Button) rootView.findViewById(R.id.btnRetour);
        mBtnRetour.setOnClickListener(new View.OnClickListener() {
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

    /**
     * interface OnClickListener
     * <p>Cette interface permet d'envoyer l'évenement OnClick d'un Button vers la classe activité qui a lancé le fragment</p>
     */
    public interface Frag_ListeSeq_BoutonRetour_Callback {
        /**
         * Evenement OnClick sur un button
         *
         * @param v View sur laquelle l'utilisateur aa cliqué
         */
        public void onClickListener(View v);
    }
}
