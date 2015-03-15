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
import com.stephane.rothen.rchrono.model.Sequence;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_BoutonAjout;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;
import com.stephane.rothen.rchrono.views.ItemListeSequence;

import java.util.concurrent.atomic.AtomicReference;

public class AjoutSequenceActivity extends ActionBarActivity implements Frag_Liste_Callback, Frag_Bouton_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback {

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
    private ChronoService mChronoService;
    /**
     * Objet permettant la communication entre le service et l'activity
     *
     * @see ListeSequencesActivity.MyReceiver
     */
    private MyReceiver myReceiver;
    /**
     * Objet permettant de gérer la communication de l'interface vers le service, il initialise mChronoService
     *
     * @see com.stephane.rothen.rchrono.controller.AjoutSequenceActivity#mChronoService
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
     * index de la séquence a supprimer dans la librairie des séquences
     */
    private int mSeqASuppr = -1;

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
        mFragListe.setAfficheBtnSuppr(true);
        mFragListe.setTypeAffichage(Frag_ListeItems.AFFICHE_LIBSEQUENCE);

        mFragBtnCreer = (Frag_BoutonAjout) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnAjouterSeq);
        mFragBtnCreer.setTexte(R.string.ajoutsequence_creer);
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
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        //bouton actif donc pas utilisé
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
            case R.id.btnAjouterSequence:
                //todo ouvrir AjoutSequence et mettre ListeSequencesActivity comme précédant dans la pile
                break;
            case R.id.btnSuppr:
                ViewParent parent = v.getParent();
                parent = parent.getParent();
                parent = parent.getParent();
                LinearLayout p = (LinearLayout) parent;
                String nom = "";
                if (p instanceof ItemListeSequence) {
                    mSeqASuppr = ((ItemListeSequence) p).getPosition();
                    Sequence s = mChrono.get().getLibSequence().get(mSeqASuppr);
                    nom = s.getNomSequence();
                } else {
                    throw new ClassCastException("View suppr non reconnue");
                }
                afficheDialogSuppr(nom);
                break;
            case R.id.txtLvSequence:
                //todo ajouter sequence à listeSequences et afficher EditionSequence
                Toast.makeText(this, "EditionSequence", Toast.LENGTH_LONG).show();
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
     * @param nom
     *      nom du fichier à confirmer
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

        if (mSeqASuppr >= 0) {
            mChrono.get().getLibSequence().remove(mSeqASuppr);
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
        mSeqASuppr = -1;
    }



    /**
     * Classe privée MyReceiver
     * <p>Elle permet de récupérer et de traiter des broadcast venant de mChronoService</p>
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
