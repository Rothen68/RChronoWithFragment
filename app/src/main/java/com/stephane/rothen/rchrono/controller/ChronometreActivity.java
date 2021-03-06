package com.stephane.rothen.rchrono.controller;

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
import android.widget.ImageButton;
import android.widget.TextView;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_Chrono_Affichage;
import com.stephane.rothen.rchrono.views.Frag_Chrono_Boutons;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe Activity affichant l'écran Chronometre
 */
public class ChronometreActivity extends ActionBarActivity implements Frag_Chrono_Affichage.Frag_Chrono_Affichage_Callback,
        Frag_Liste_Callback,
        Frag_Bouton_Callback {


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
     * @see ChronometreActivity.MyReceiver
     */
    private MyReceiver myReceiver;

    /**
     * Objet permettant de gérer la communication de l'interface vers le service, il initialise chronoService
     *
     * @see ChronometreActivity#chronoService
     */
    private ServiceConnection mConnexion;

    /**
     * Stockage du fragment Frag_Chrono_Affichage
     */
    private Frag_Chrono_Affichage mFragAffichage;
    /**
     * Stockage du fragment Frag_ListeItems
     */
    private Frag_ListeItems mFragListe;
    /**
     * Stockage du fragment Frag_Chrono_boutons
     */
    private Frag_Chrono_Boutons mFragBoutons;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        chronoService.setPersistance(true);
    }

    /**
     * Gestion de la creation de la vue
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chrono_host_frag);
        if (savedInstanceState == null) {
        }
        getSupportFragmentManager().executePendingTransactions();
        mFragAffichage = (Frag_Chrono_Affichage) getSupportFragmentManager().findFragmentById(R.id.Frag_Chrono_Affichage);
        mFragListe = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.Frag_Chrono_Liste);
        mFragListe.setAfficheCurseur(true);
        mFragBoutons = (Frag_Chrono_Boutons) getSupportFragmentManager().findFragmentById(R.id.Frag_Chrono_Boutons);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chronometre, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_quitter) {
            chronoService.setPersistance(false);
            if (chronoService.getChronoStart())
                chronoService.stopChrono();

            finish();
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
                    mChrono = new AtomicReference<>(new Chronometre(getApplication()));
                    chronoService.setAtomicChronometre(mChrono);
                    chronoService.updateListView();
                } else {
                    mChrono = chronoService.getAtomicChronometre();
                    chronoService.updateListView();
                    chronoService.updateChrono();
                    if (chronoService.getChronoStart())
                        mFragBoutons.setImageBtnStart(R.drawable.pause);
                    else
                        mFragBoutons.setImageBtnStart(R.drawable.play);
                    chronoService.setPersistance(false);
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
        ifilter.addAction(ChronoService.SER_TEMPS_RESTANT);
        ifilter.addAction(ChronoService.SER_UPDATE_LISTVIEW);
        ifilter.addAction(ChronoService.SER_FIN_LISTESEQUENCE);
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

    /**
     * Arrêt du service ChronoService dans onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnexion);


    }


    @Override
    protected void onDestroy() {
        if (!chronoService.getPersistance() && !chronoService.getChronoStart()) {
            stopService(new Intent(this, ChronoService.class));
            chronoService = null;
            mConnexion = null;
        }

        super.onDestroy();
    }

    /**
     * Implémentation de l'interface permettant de détecter le click sur un bouton d'un fragment
     *
     * @param v
     */
    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                if (mChrono.get().getListeSequence().size() > 0) {
                    if (chronoService != null) {
                        if (chronoService.getChronoStart()) {
                            chronoService.stopChrono();
                            ((ImageButton) v).setImageResource(R.drawable.play);
                        } else {
                            chronoService.startChrono();
                            ((ImageButton) v).setImageResource(R.drawable.pause);
                        }
                    }
                } else {

                    goToListeSequencesActivity();
                }
                break;
            case R.id.btnReset:
                if (chronoService != null) {
                    chronoService.resetChrono();
                }

                break;
            case R.id.txtChrono:
                int type = mChrono.get().getTypeAffichage();
                switch (type) {
                    case Chronometre.AFFICHAGE_TEMPS_EX:
                        mChrono.get().setTypeAffichage(Chronometre.AFFICHAGE_TEMPS_SEQ);
                        ((TextView) findViewById(R.id.txtDescChrono)).setText(R.string.descChronometre_Sequence);
                        mFragAffichage.setTxtChrono(mChrono.get().getDureeRestanteSequenceActive());

                        break;
                    case Chronometre.AFFICHAGE_TEMPS_SEQ:
                        mChrono.get().setTypeAffichage(Chronometre.AFFICHAGE_TEMPS_TOTAL);
                        ((TextView) findViewById(R.id.txtDescChrono)).setText(R.string.descChronometre_Total);
                        mFragAffichage.setTxtChrono(mChrono.get().getDureeRestanteTotale());
                        break;
                    case Chronometre.AFFICHAGE_TEMPS_TOTAL:
                        mChrono.get().setTypeAffichage(Chronometre.AFFICHAGE_TEMPS_EX);
                        ((TextView) findViewById(R.id.txtDescChrono)).setText(R.string.descChronometre_Exercice);
                        mFragAffichage.setTxtChrono(mChrono.get().getDureeRestanteExerciceActif());
                        break;
                    default:
                        break;
                }
                int position = 1;
                int exercice = mChrono.get().getIndexExerciceActif();
                int seq = mChrono.get().getIndexSequenceActive();
                if (exercice >= 0) {
                    for (int j = 0; j < seq; j++) {
                        position++;
                        for (ElementSequence e : mChrono.get().getSeqFromLstSequenceAt(j).getTabElement()) {
                            position++;
                        }
                    }
                    position = position + exercice;
                }
                mFragListe.afficheListView(position, mChrono);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onLongClickListener(View v) {
        return false;
    }

    /**
     * implémentation de l'interface permettant de détecter le click sur la ListView d'un fragment
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    @Override
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            /*
             * Lors d'un appuis court sur un item de la ListView quand le chrono est arreté, place le curseur sur l'exercice ou sur le premier exercice de la séquence cliqué
             * @param parent
             */
            case R.id.Frag_Liste_listView:
                if (!chronoService.getChronoStart()) {
                    chronoService.stopChrono();
                    int posExercice = mChrono.get().setChronoAt(position);
                    if (posExercice > -1)
                        mFragListe.afficheListView(posExercice, mChrono);
                    mFragBoutons.setImageBtnStart(R.drawable.play);
                    chronoService.updateChrono();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Implémentation de l'interface permettant de détecter le long click sur la ListView d'un fragment
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        goToListeSequencesActivity();
        return true;
    }

    private void goToListeSequencesActivity() {
        chronoService.stopChrono();

        Intent i = new Intent(this, ListeSequencesActivity.class);
        startActivity(i);
    }

    private void goToListeSequencesActivityForCreation() {
        chronoService.stopChrono();

        Intent i = new Intent(this, ListeSequencesActivity.class);
        i.putExtra("MODE", ListeSequencesActivity.CREATION);
        startActivity(i);
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
            if (mChrono != null) {
                switch (intent.getAction()) {
                    case ChronoService.SER_TEMPS_RESTANT:
                        int tempsRestant = intent.getIntExtra(ChronoService.SER_TEMPS_RESTANT, -1);
                        if (tempsRestant != -1) {
                            mFragAffichage.setTxtChrono(tempsRestant);
                        }
                        break;
                    case ChronoService.SER_UPDATE_LISTVIEW:
                        int position = intent.getIntExtra(ChronoService.SER_UPDATE_LISTVIEW, -1);
                        if (position != -1) {
                            mFragListe.afficheListView(position, mChrono);
                            if (mChrono.get().getListeSequence().size() == 0) {
                                mFragBoutons.setImageBtnStart(R.drawable.add);
                            } else if (chronoService.getChronoStart())
                                mFragBoutons.setImageBtnStart(R.drawable.pause);
                            else
                                mFragBoutons.setImageBtnStart(R.drawable.play);

                        }
                        break;
                    case ChronoService.SER_FIN_LISTESEQUENCE:
                        mFragBoutons.setImageBtnStart(R.drawable.play);
                        mFragListe.afficheListView(0, mChrono);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
