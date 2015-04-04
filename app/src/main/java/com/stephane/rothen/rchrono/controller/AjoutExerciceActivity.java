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
import android.widget.Toast;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.Exercice;
import com.stephane.rothen.rchrono.model.Sequence;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_BoutonAjout;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;

import java.util.concurrent.atomic.AtomicReference;

//todo controler fonctionnement de l'activity

/**
 * Classe Activity affichant l'écran AjoutExercice
 */
public class AjoutExerciceActivity extends ActionBarActivity implements Frag_Liste_Callback, Frag_Bouton_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback {

    public static final int RESULT_AJOUT = 50;
    public static final int RESULT_CREER = 51;


    /**
     * Instance de la classe AtomicReference<Chronometre> pour éviter les conflits d'acces entre le ChronoService et l'activity
     *
     * @see Chronometre
     * @see java.util.concurrent.atomic.AtomicReference
     */
    private AtomicReference<Chronometre> mChrono;
    /**
     * Objet permettant de récupérer l'instance du service ChronoService
     *
     * @see ChronoService
     */
    private ChronoService mChronoService;
    /**
     * Objet permettant la communication entre le service et l'activity
     *
     * @see com.stephane.rothen.rchrono.controller.ListeSequencesActivity.MyReceiver
     */
    private MyReceiver myReceiver;
    /**
     * Objet permettant de gérer la communication de l'interface vers le service, il initialise mChronoService
     *
     * @see com.stephane.rothen.rchrono.controller.ListeSequencesActivity#chronoService
     */
    private ServiceConnection mConnexion;
    /**
     * Instance de la classe du fragment affichant la liste des séquences
     */
    private Frag_ListeItems mFragListe;
    /**
     * Instance de la classe du fragment affichant le bouton ajouter sequence
     */
    private Frag_BoutonAjout mFragBtnCreer;
    /**
     * Instance de la classe du fragment affichant le bouton retour
     */
    private Frag_BoutonRetour mFragBtnRetour;

    /**
     * index de l'exercice a supprimer dans la librairie des exercices
     */
    private int mExASuppr = -1;


    /**
     * Gestion de la creation de la vue
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

        }
        setContentView(R.layout.ajout_host_frag);
        if (savedInstanceState == null) {
        }
        getSupportFragmentManager().executePendingTransactions();
        mFragListe = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_Liste);
        mFragListe.setAfficheBtnSupprExercice(false);
        mFragListe.setTypeAffichage(Frag_ListeItems.AFFICHE_LIBEXERCICE);

        mFragBtnCreer = (Frag_BoutonAjout) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnAjouterSeq);
        mFragBtnCreer.setTexte(R.string.ajoutexercice_creer);
        mFragBtnRetour = (Frag_BoutonRetour) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnRetour);


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

                mChronoService = ((ChronoService.MonBinder) service).getService();
                if (mChronoService.getAtomicChronometre() == null) {
                    //todo gérer erreur dans service
                } else {
                    mChrono = mChronoService.getAtomicChronometre();
                    mChronoService.setPersistance(false);
                    mFragListe.afficheListView(0, mChrono);
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mChronoService = null;
            }
        };
        Intent intent = new Intent(getApplicationContext(), ChronoService.class);
        intent.putExtra(ChronoService.SER_ACTION, 0);
        startService(intent);
        bindService(intent, mConnexion, BIND_AUTO_CREATE);
        //initialisation du receiver qui permet la communication vers l'interface depuis mChronoService
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

    /**
     * Gere la creation du menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ajout, menu);
        return true;
    }

    /**
     * Gère les interractions avec le menu
     * Affiche ou non les boutons de suppression sur la listView
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_ajout_supprimer) {
            if (mFragListe.getAfficheBtnSupprExercice()) {
                mFragListe.setAfficheBtnSupprExercice(false);
                mFragListe.afficheListView(0, mChrono);
            } else {
                mFragListe.setAfficheBtnSupprExercice(true);
                mFragListe.afficheListView(0, mChrono);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Gere le click sur un item de la listView
     * Si mode supprimer actif, affiche la popup de confirmation
     * Sinon mémorise l'exercice cliqué et retourne à la fenetre editionSequence
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    @Override
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        if (mFragListe.getAfficheBtnSupprExercice()) {
            mExASuppr = position;
            Exercice e = mChrono.get().getLibExercice().get(mExASuppr);
            String nom = e.getNomExercice();
            if (mChrono.get().isExerciceUtilise(position)) {
                Toast.makeText(this, getString(R.string.ajoutexercice_exercice_utilise), Toast.LENGTH_SHORT).show();
            } else {
                afficheDialogSuppr(nom);
            }
        } else {
            Exercice e = mChrono.get().getLibExercice().get(position);
            Sequence s = (Sequence) mChrono.get().getSeqTemp().clone();
            s.ajouterExercice(e);
            if (mChrono.get().getDureeTotaleSansSeqActive() + s.getDureeSequence() > 10 * 60 * 60) {
                Toast.makeText(this, R.string.alert_dureeTotaleTropGrande, Toast.LENGTH_LONG).show();
            } else {
                mChrono.get().getSeqTemp().ajouterExercice(e);
                setResult(RESULT_AJOUT);
                finish();
            }
        }
    }

    @Override
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        //bouton actif donc pas utilisé
        return false;
    }

    /**
     * Gestion du Callback onclickListener des vues filles
     *
     * @param v View sur laquelle l'utilisateur a cliqué
     */
    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.btnRetour:
                finish();
                break;
            case R.id.Frag_ListeSeq_BtnAjouterSeq:
                setResult(RESULT_CREER);
                finish();
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onLongClickListener(View v) {
        //pas de gestion du click long dans cette activity
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
            DialogFragment df = Frag_AlertDialog_Suppr.newInstance(getString(R.string.alertDialog_suppr) + " " + nom + " " + getString(R.string.alertDialog_dutelephone));
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

        if (mExASuppr >= 0) {
            mChrono.get().supprimerExerciceDansLibrairie(mExASuppr);
        }
        mFragListe.afficheListView(0, mChrono);

    }

    /**
     * Gestion de l'appuis sur Cancel de la popup confirmation de suppression
     *
     * @see com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr
     */
    @Override
    public void doDialogFragCancelClick() {
        mExASuppr = -1;
    }


    /**
     * Classe privée MyReceiver
     * <p>Elle permet de récupérer et de traiter des broadcast venant de mChronoService</p>
     *
     * @see ChronoService
     */
    private class MyReceiver extends BroadcastReceiver {

        public boolean isRegistered = false;


        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
