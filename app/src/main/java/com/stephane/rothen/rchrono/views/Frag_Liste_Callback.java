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
     * Evenement OnItemClickListener
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id);

    /**
     * Evenement OnItemLongClickListener
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id);

}

