package com.stephane.rothen.rchrono.controller;

import android.content.Context;

import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;

/**
 * Created by stéphane on 14/03/2015.
 */
public class ListeSeqCustomAdapter extends CustomAdapter {
    /**
     * Constructeur
     * <p>Initialise le LayoutInflater avec le context de l'application</p>
     *
     * @param context Context de l'application
     * @see com.stephane.rothen.rchrono.controller.CustomAdapter#m_inflater
     */
    public ListeSeqCustomAdapter(Context context) {
        super(context);
    }

    @Override
    public void setCallback(Frag_Liste_Callback callback) {
        mCallback = (Frag_Liste_Callback.Frag_ListeSeq_Liste_Callback) callback;
    }
}
