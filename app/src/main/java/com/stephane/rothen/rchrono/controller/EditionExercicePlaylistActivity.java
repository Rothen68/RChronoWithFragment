package com.stephane.rothen.rchrono.controller;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Morceau;
import com.stephane.rothen.rchrono.model.Playlist;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by stéphane on 02/04/2015.
 */
public class EditionExercicePlaylistActivity extends ActionBarActivity implements Frag_Bouton_Callback, Frag_Liste_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback {

    private static final int LISTESONS_MORCEAU = 2;

    /**
     * Instance de la classe AtomicReference<Chronometre> pour éviter les conflits d'acces entre le ChronoService et l'activity
     *
     * @see com.stephane.rothen.rchrono.controller.Chronometre
     * @see java.util.concurrent.atomic.AtomicReference
     */
    private AtomicReference<Chronometre> mChrono;
    /**
     * Objet permettant de récupérer l'instance du service ChronoService
     *
     * @see com.stephane.rothen.rchrono.controller.ChronoService
     */
    private ChronoService chronoService;
    /**
     * Objet permettant la communication entre le service et l'activity
     *
     * @see ListeSequencesActivity.MyReceiver
     */
    private MyReceiver myReceiver;
    /**
     * Objet permettant de gérer la communication de l'interface vers le service, il initialise chronoService
     *
     * @see ListeSequencesActivity#chronoService
     */
    private ServiceConnection mConnexion;

    private Frag_ListeItems mLstMorceaux;
    private Frag_BoutonRetour mBtnRetour;
    private Button mBtnAjouterMorceau;

    private Playlist mPlaylist;

    private int mIndexMorceauASuppr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editionexplaylist_host_frag);
        getSupportFragmentManager().executePendingTransactions();
        mLstMorceaux = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.editionexplaylist_host_frag_lstMorceaux);
        mBtnRetour = (Frag_BoutonRetour) getSupportFragmentManager().findFragmentById(R.id.editionexplaylist_host_frag_btnRetour);
        mBtnAjouterMorceau = (Button) findViewById(R.id.editionexplaylist_host_frag_btnAjouterMorceau);
        mLstMorceaux.setAfficheBtnSuppr(false);
        mPlaylist = null;
        mBtnAjouterMorceau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToListeSons();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editionexerciceplaylist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_editionExercicePlaylist_supprimer) {
            if (mLstMorceaux.getAfficheBtnSuppr()) {
                mLstMorceaux.setAfficheBtnSuppr(false);
                mLstMorceaux.afficheListView(mPlaylist);
            } else {
                mLstMorceaux.setAfficheBtnSuppr(true);
                mLstMorceaux.afficheListView(mPlaylist);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Gestion de la reprise de l'activity, reconnexion au ChronoService et actualisation de la vue
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Lancement du service ChronoService
        mConnexion = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                chronoService = ((ChronoService.MonBinder) service).getService();
                if (chronoService.getAtomicChronometre() == null) {
                    //todo gérer erreur dans service
                } else {
                    mChrono = chronoService.getAtomicChronometre();
                    chronoService.setPersistance(false);
                    ElementSequence el = mChrono.get().getElementSeqTemp();
                    if (el != null) {
                        mPlaylist = el.getPlaylistExercice();
                        mLstMorceaux.afficheListView(mPlaylist);
                    } else {
                        finish();
                    }

                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                chronoService = null;
            }
        };
        Intent intent = new Intent(getApplicationContext(), ChronoService.class);
        intent.putExtra(ChronoService.SER_ACTION, 0);
        startService(intent);
        bindService(intent, mConnexion, BIND_AUTO_CREATE);
        //initialisation du receiver qui permet la communication vers l'interface depuis chronoService
        myReceiver = new MyReceiver();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(ChronoService.SER_UPDATE_LISTVIEW);
        registerReceiver(myReceiver, ifilter);
        myReceiver.isRegistered = true;
    }


    /**
     * Gestion de la mise en pause de l'activity, déconnexion du ChronoService
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (myReceiver.isRegistered)
            unregisterReceiver(myReceiver);


    }


    private void goToListeSons() {
        Intent i = new Intent(this, ListeSonsActivity.class);
        startActivityForResult(i, LISTESONS_MORCEAU);
    }

    /**
     * Evenement OnClick sur un button
     *
     * @param v View sur laquelle l'utilisateur aa cliqué
     */
    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.editionexplaylist_host_frag_btnRetour:
                finish();
                break;
            default:
                break;
        }
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
        if (mLstMorceaux.getAfficheBtnSuppr()) {
            mIndexMorceauASuppr = position;
            String nom = mPlaylist.getMorceauAt(mIndexMorceauASuppr).getTitre();
            afficheDialogSuppr(nom);
        }

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

    /**
     * Affiche la popup confirmation de suppression
     *
     * @param nom nom du fichier à confirmer
     */
    private void afficheDialogSuppr(String nom) {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("dialog") == null) {
            DialogFragment df = Frag_AlertDialog_Suppr.newInstance(getString(R.string.alertDialog_suppr) + " " + nom);
            df.show(getFragmentManager(), "dialog");
        }
    }

    /**
     * Gestion de l'appuis sur Supprimer de la popup confirmation de suppression
     *
     * @see com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr
     */
    public void doDialogFragSupprClick() {
        Toast.makeText(this, "Suppression...", Toast.LENGTH_SHORT).show();

        mPlaylist.supprimerMorceau(mIndexMorceauASuppr);
        mLstMorceaux.afficheListView(mPlaylist);

    }

    /**
     * Gestion de l'appuis sur Cancel de la popup confirmation de suppression
     *
     * @see com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr
     */
    @Override
    public void doDialogFragCancelClick() {

    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == LISTESONS_MORCEAU || requestCode == LISTESONS_MORCEAU) && resultCode == ListeSonsActivity.RESULT_OK) {
            long id = data.getLongExtra("ID", -1);
            String titre = data.getStringExtra("TITRE");
            String artiste = data.getStringExtra("ARTISTE");
            if (id != -1) {
                switch (requestCode) {

                    case LISTESONS_MORCEAU:
                        mPlaylist.ajouterMorceau(new Morceau(id, titre, artiste));

                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Classe privée MyReceiver
     * <p>Elle permet de récupérer et de traiter des broadcast venant de chronoService</p>
     *
     * @see com.stephane.rothen.rchrono.controller.ChronoService
     */
    private class MyReceiver extends BroadcastReceiver {

        public boolean isRegistered = false;


        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
