package com.stephane.rothen.rchrono.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.stephane.rothen.rchrono.R;

/**
 * Created by stéphane on 31/03/2015.
 */
public class Frag_EditEx_Playlist extends Fragment {
    public static final String FRAG_EDITEX_PLAYLIST_CALLBACK = "FRAG_EDITEX_PLAYLIST_CALLBACK";


    /**
     * Bouton Retour du fragment
     */
    private ToggleButton mTBPlaylist;
    private Boolean mEtatTBPlaylist = false;


    public Frag_EditEx_Playlist() {
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
        View rootView = inflater.inflate(R.layout.editionex_frag_playlist, container, false);
        mTBPlaylist = (ToggleButton) rootView.findViewById(R.id.editionex_frag_detail_tbJouerPlaylist);
        mTBPlaylist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTBPlaylist = isChecked;
            }
        });
        return rootView;
    }

    public Boolean getJouerPlaylist() {
        return mEtatTBPlaylist;
    }

    public void setJouerPlaylist(Boolean jouerPlaylist) {
        mTBPlaylist.setChecked(jouerPlaylist);
        mEtatTBPlaylist = jouerPlaylist;
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
