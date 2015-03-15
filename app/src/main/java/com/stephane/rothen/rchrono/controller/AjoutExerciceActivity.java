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
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.Exercice;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_BoutonAjout;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;
import com.stephane.rothen.rchrono.views.ItemListeExercice;

import java.util.concurrent.atomic.AtomicReference;

//todo controler fonctionnement de l'activity


public class AjoutExerciceActivity extends ActionBarActivity implements Frag_Liste_Callback, Frag_Bouton_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback {

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
    private ChronoService chronoService;
    /**
     * Objet permettant la communication entre le service et l'activity
     *
     * @see com.stephane.rothen.rchrono.controller.ListeSequencesActivity.MyReceiver
     */
    private MyReceiver myReceiver;
    /**
     * Objet permettant de gérer la communication de l'interface vers le service, il initialise chronoService
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

        }
        setContentView(R.layout.listeseq_host_frag);
        if (savedInstanceState == null) {
        }
        getSupportFragmentManager().executePendingTransactions();
        mFragListe = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_Liste);
        mFragListe.setAfficheBtnSuppr(true);
        mFragListe.setTypeAffichage(Frag_ListeItems.AFFICHE_LIBEXERCICE);

        mFragBtnCreer = (Frag_BoutonAjout) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnAjouterSeq);
        mFragBtnCreer.setTexte(R.string.ajoutsequence_creer);
        mFragBtnRetour = (Frag_BoutonRetour) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnRetour);


    }


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
                    mFragListe.afficheListView(0, mChrono);
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


    @Override
    protected void onPause() {
        super.onPause();
        if (myReceiver.isRegistered)
            unregisterReceiver(myReceiver);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_liste_sequences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.btnRetour:
                finish();
                break;
            case R.id.btnAjouterSequence:
                //todo ouvrir AjoutSequence et mettre ListeSequencesActivity comme précédant dans la pile
                break;
            case R.id.btnSuppr:
                ViewParent parent = v.getParent();
                parent = parent.getParent();
                parent = parent.getParent();
                LinearLayout p = (LinearLayout) parent;
                String nom = "";
                if (p instanceof ItemListeExercice) {
                    mExASuppr = ((ItemListeExercice) p).getPosition();
                    Exercice e = mChrono.get().getLibExercice().get(mExASuppr);
                    nom = e.getNomExercice();
                } else {
                    throw new ClassCastException("View suppr non reconnue");
                }
                afficheDialogSuppr(nom);
                break;
            case R.id.txtLvExercice:
                //todo ajouter exercice à la sequence et afficher EditionExercice
                Toast.makeText(this, "EditionExercice", Toast.LENGTH_LONG).show();
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


    private void afficheDialogSuppr(String nom) {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("dialog") == null) {
            DialogFragment df = Frag_AlertDialog_Suppr.newInstance(getString(R.string.alertDialog_suppr) + " " + nom + " " + getString(R.string.alertDialog_dutelephone));
            df.show(getFragmentManager(), "dialog");
        }
    }


    public void doDialogFragSupprClick() {
        Toast.makeText(this, "Suppression...", Toast.LENGTH_SHORT).show();

        if (mExASuppr >= 0) {
            mChrono.get().getLibExercice().remove(mExASuppr);
        }
        mFragListe.afficheListView(0, mChrono);

    }

    @Override
    public void doDialogFragCancelClick() {
        mExASuppr = -1;
    }

    @Override
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        //bouton actif donc pas utilisé
    }

    @Override
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        //bouton actif donc pas utilisé
        return false;
    }

    /**
     * Classe privée MyReceiver
     * <p>Elle permet de récupérer et de traiter des broadcast venant de chronoService</p>
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
