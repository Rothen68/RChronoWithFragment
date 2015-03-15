package com.stephane.rothen.rchrono.views;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by stéphane on 14/03/2015.
 */
public interface Frag_Liste_Callback {
    /**
     * Evenement OnClick sur un button
     *
     * @param v View sur laquelle l'utilisateur aa cliqué
     */
    public void onClickListener(View v);

    /**
     * Evenement long click sur un element
     * @param v
     *          View qui a ete cliquee
     *
     * @return
     *      true si l'evenement a ete gerer
     */
    public boolean onLongClickListener(View v);

    /**
     * Evenement OnItemClickListener
     * <p>A utiliser si le bouton de l'item n'est pas actif</p>
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id);

    /**
     * Evenement OnItemLongClickListener
     *<p>A utiliser si le bouton de l'item n'est pas actif</p>
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id);

}

