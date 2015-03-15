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
import com.stephane.rothen.rchrono.model.Sequence;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_EditSeq_BtnExercice;
import com.stephane.rothen.rchrono.views.Frag_EditSeq_Detail;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by stéphane on 15/03/2015.
 */
public class EditionSequenceActivity extends ActionBarActivity implements Frag_Liste_Callback, Frag_Bouton_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback,
        Frag_EditSeq_Detail.Frag_EditSeq_Detail_Callback {

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
    /**
     * Instance de la classe du fragment affichant la liste des séquences
     */
    private Frag_ListeItems mFragListe;
    /**
     * Instance de la classe du fragment affichant les boutons ajouter
     */
    private Frag_EditSeq_BtnExercice mFragBtnExercice;
    /**
     * Instance de la classe du fragment affichant les détails de la séquence
     */
    private Frag_EditSeq_Detail mFragDetail;
    /**
     * Instance de la classe du fragment affichant le bouton retour
     */
    private Frag_BoutonRetour mFragBtnRetour;

    /**
     * index de la séquence a supprimer dans la librairie des séquences
     */
    private int mExASuppr = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

        }
        setContentView(R.layout.editionseq_host_frag);
        if (savedInstanceState == null) {
        }
        getSupportFragmentManager().executePendingTransactions();
        mFragListe = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.Frag_EditSeq_Liste);
        mFragListe.setAfficheBtnSuppr(true);
        mFragListe.setTypeAffichage(Frag_ListeItems.AFFICHE_EXERCICESEQACTIVE);

        mFragDetail = (Frag_EditSeq_Detail) getSupportFragmentManager().findFragmentById(R.id.Frag_EditSeq_Detail);

        mFragBtnExercice = (Frag_EditSeq_BtnExercice) getSupportFragmentManager().findFragmentById(R.id.Frag_EditSeq_BtnExercice);
        mFragBtnRetour = (Frag_BoutonRetour) getSupportFragmentManager().findFragmentById(R.id.Frag_EditSeq_BtnRetour);


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
                    Sequence s = mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive());
                    mFragDetail.setTxtNom(s.getNomSequence());
                    mFragDetail.setTxtRepetition(s.getNombreRepetition());
                    mFragDetail.setTbNom(s.getSyntheseVocale().getNom());
                    mFragDetail.setTbDuree(s.getSyntheseVocale().getDuree());
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
            case R.id.editionseq_frag_btnexercice_creer:
                //todo creer un exercice dans la séquence et ouvrir EditionExercice
                Toast.makeText(this, "Creer et EditionExercice", Toast.LENGTH_LONG).show();
                break;
            case R.id.editionseq_frag_btnexercice_ajouter:
                //todo ouvrir la fenetre AjoutExercice
                Toast.makeText(this, "AjoutExercice", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnSuppr:
                /*ViewParent parent = v.getParent();
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
                afficheDialogSuppr(nom);*/
                Toast.makeText(this, "Suppr Exercice", Toast.LENGTH_LONG).show();
                break;
            case R.id.txtLvExercice:
                //todo ajouter sequence à listeSequences et afficher EditionSequence
                Toast.makeText(this, "duree exercice", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

    }

    @Override
    public void onTextChange(View v) {

    }

    @Override
    public boolean onLongClickListener(View v) {
        //pas de gestion du click long dans cette activity
        return false;
    }


    private void afficheDialogSuppr(String nom) {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("dialog") == null) {
            DialogFragment df = Frag_AlertDialog_Suppr.newInstance(R.string.alertDialog_suppr + " " + nom + " " + R.string.alertDialog_dutelephone);
            df.show(getFragmentManager(), "dialog");
        }
    }


    public void doDialogFragSupprClick() {
        Toast.makeText(this, "Suppression...", Toast.LENGTH_SHORT).show();

//        if (mSeqASuppr >= 0) {
//            mChrono.get().getLibSequence().remove(mSeqASuppr);
//        }
//        mFragListe.afficheListView(0, mChrono);

    }

    @Override
    public void doDialogFragCancelClick() {
//        mSeqASuppr = -1;
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
     * @see com.stephane.rothen.rchrono.controller.ChronoService
     */
    private class MyReceiver extends BroadcastReceiver {

        public boolean isRegistered = false;


        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}