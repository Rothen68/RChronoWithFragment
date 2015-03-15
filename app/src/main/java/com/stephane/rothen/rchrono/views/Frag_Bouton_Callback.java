package com.stephane.rothen.rchrono.views;

import android.view.View;

/**
 * interface OnClickListener
 * <p>Cette interface permet d'envoyer l'évenement OnClick du Button vers la classe activité qui a lancé le fragment</p>
 * <p/>
 * Created by stéphane on 15/03/2015.
 */
public interface Frag_Bouton_Callback {
    /**
     * Evenement OnClick sur un button
     *
     * @param v View sur laquelle l'utilisateur aa cliqué
     */
    public void onClickListener(View v);
}
