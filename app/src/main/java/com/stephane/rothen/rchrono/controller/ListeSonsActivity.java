package com.stephane.rothen.rchrono.controller;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.Morceau;
import com.stephane.rothen.rchrono.model.Playlist;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;

/**
 * Created by stéphane on 31/03/2015.
 */
public class ListeSonsActivity extends ActionBarActivity implements Frag_Bouton_Callback, Frag_Liste_Callback {

    public static final int RESULT_OK = 10;

    private Frag_ListeItems mLstMorceaux;
    private Frag_BoutonRetour mBtnRetour;

    private Playlist mPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listesons);
        getSupportFragmentManager().executePendingTransactions();
        mLstMorceaux = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSons_Liste);
        mBtnRetour = (Frag_BoutonRetour) getSupportFragmentManager().findFragmentById(R.id.ListeSons_BtnRetour);
        mLstMorceaux.setAfficheBtnSuppr(false);
        getListeSons();
        mLstMorceaux.afficheListView(mPlaylist);


    }

    /**
     * Evenement OnClick sur un button
     *
     * @param v View sur laquelle l'utilisateur aa cliqué
     */
    @Override
    public void onClickListener(View v) {
        finish();
    }

    /**
     * Evenement long click sur un element
     *
     * @param v View qui a ete cliquee
     * @return true si l'evenement a ete gerer
     */
    @Override
    public boolean onLongClickListener(View v) {
        return false;
    }

    /**
     * Evenement OnItemClickListener
     * <p>A utiliser si le bouton de l'item n'est pas actif</p>
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    @Override
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        Intent returnIntent = new Intent();
        Morceau m = mPlaylist.getMorceauAt(position);

        returnIntent.putExtra("ID", m.getIdMorceau());
        returnIntent.putExtra("TITRE", m.getTitre());
        returnIntent.putExtra("ARTISTE", m.getArtiste());
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    /**
     * Evenement OnItemLongClickListener
     * <p>A utiliser si le bouton de l'item n'est pas actif</p>
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    @Override
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }


    private void getListeSons() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        mPlaylist = new Playlist();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                mPlaylist.ajouterMorceau(new Morceau(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }


}
