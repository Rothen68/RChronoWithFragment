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
 * Created by stéphane on 13/03/2015.
 */
public class Frag_Chrono_Boutons extends Fragment {

    public static final String FRAG_CHRONO_BOUTONS = "FRAG_CHRONO_BOUTONS";

    /**
     * Instance de l'interface OnClickListener
     */
    private Frag_Chrono_Boutons_Callback mCallback;
    private Button mbtnStart;
    /**
     * Objet de l'inteface, Button qui est l'instance du bouton Reset
     */
    private Button mbtnReset;

    public Frag_Chrono_Boutons() {
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
            mCallback = (Frag_Chrono_Boutons_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_Chrono_Boutons_Callback");
        }


    }

    /**
     * Modifie le texte du bouton Start/Pause
     *
     * @param texte identifiant de la chaine de caractere stockée dans les ressources à afficher dans le bouton
     */
    public void setTexteBtnStart(int texte) {
        mbtnStart.setText(texte);
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
        View rootView = inflater.inflate(R.layout.chrono_frag_boutons, container, false);

        mbtnReset = (Button) rootView.findViewById(R.id.btnReset);
        mbtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickListener(v);
            }
        });
        mbtnStart = (Button) rootView.findViewById(R.id.btnStart);
        mbtnStart.setOnClickListener(new View.OnClickListener() {
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
    public interface Frag_Chrono_Boutons_Callback {
        /**
         * Evenement OnClick sur un button
         *
         * @param v View sur laquelle l'utilisateur aa cliqué
         */
        public void onClickListener(View v);
    }
}
