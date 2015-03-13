package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stephane.rothen.rchrono.Fonctions;
import com.stephane.rothen.rchrono.R;

/**
 * Created by stéphane on 13/03/2015.
 */
public class Frag_Chrono_Affichage extends Fragment {


    public static final String FRAG_CHRONO_AFFICHAGE = "FRAG_CHRONO_AFFICHAGE";
    /**
     * Instance de l'interface Frag_Chrono_Affichage_Callback
     */
    private Frag_Chrono_Affichage_Callback mCallback;
    /**
     * Objet de l'interface, TextView qui est l'instance de la zone de texte permettant d'afficer le temps restant pour l'exercice en cours
     */
    private TextView mtxtChrono;
    private TextView mtxtDescChrono;

    public void setTxtDescChrono(String texte) {
        mtxtDescChrono.setText(texte);
    }

    /**
     * Fonction permettant de controler que l'activity utilisant le fragment implémente bien ses callbacks
     *
     * @param activity
     */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Frag_Chrono_Affichage_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_Chrono_Affichage_Callback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chrono_frag_affichage, container, false);
        mtxtChrono = (TextView) view.findViewById(R.id.txtChrono);
        mtxtChrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickListener(v);
            }
        });
        mtxtDescChrono = (TextView) view.findViewById(R.id.txtDescChrono);

        return view;
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
     * Met à jour le texte dans la TextView txtChrono
     *
     * @param valeur valeur à afficher dans la TextView apres conversion en HMS
     */
    public void setTxtChrono(int valeur) {
        mtxtChrono.setText(Fonctions.convertSversHMS(valeur));
        mtxtChrono.invalidate();
    }

    /**
     * Frag_Chrono_Affichage_Callback
     * <p>Cette interface permet de gerer les callback du fragment vers l'activity</p>
     */
    public interface Frag_Chrono_Affichage_Callback {
        /**
         * Evenement OnClick sur la zone de texte
         *
         * @param v View sur laquelle l'utilisateur aa cliqué
         */
        public void onClickListener(View v);
    }

}
