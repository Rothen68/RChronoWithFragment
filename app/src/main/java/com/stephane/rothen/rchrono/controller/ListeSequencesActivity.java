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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stephane.rothen.rchrono.Fonctions;
import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Sequence;
import com.stephane.rothen.rchrono.views.CustomAdapter;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_BoutonAjout;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_Dialog_Duree;
import com.stephane.rothen.rchrono.views.Frag_Dialog_Repetition;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe Activity affichant l'écran LsiteSequences
 */
public class ListeSequencesActivity extends ActionBarActivity implements Frag_Bouton_Callback,
        Frag_Liste_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback,
        Frag_Dialog_Repetition.Frag_Dialog_Repetition_Callback,
        Frag_Dialog_Duree.Frag_Dialog_Duree_Callback

{
    private static final int TYPESEQUENCE = 1;
    private static final int TYPEEXERCICE = 2;
    private static final int ACTIVITY_AJOUT_SEQ = 1;

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
     * Instance de la classe du fragment affichant le bouton ajouter sequence
     */
    private Frag_BoutonAjout mFragBtnAjouterSeq;
    /**
     * TextView affichant la durée totale de la liste des séquences
     */
    private TextView mTxtDuree;
    /**
     * Instance de la classe du fragment affichant le bouton retour
     */
    private Frag_BoutonRetour mFragBtnRetour;

    private int mTypeASuppr = 0;


    /**
     * Gestion de la creation de la vue
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listeseq_host_frag);

        getSupportFragmentManager().executePendingTransactions();
        mFragListe = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_Liste);
        mFragListe.setAfficheBtnSuppr(false);
        mFragBtnAjouterSeq = (Frag_BoutonAjout) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnAjouterSeq);
        mFragBtnRetour = (Frag_BoutonRetour) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnRetour);
        mTxtDuree = (TextView) findViewById(R.id.ListeSeq_txtDuree);


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
                    mChrono.get().resetChrono();
                    if (mChrono.get().getListeSequence().size() > 0) {
                        chronoService.updateListView();
                        chronoService.setPersistance(false);
                        mTxtDuree.setText(getString(R.string.listeSequences_tempstotal) + " " + Fonctions.convertSversHMS(mChrono.get().getDureeTotale()));
                    } else {
                        goToAjoutSequenceActivity();
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
        if (id == R.id.menu_supprimer) {
            if (mFragListe.getAfficheBtnSuppr()) {
                mFragListe.setAfficheBtnSuppr(false);
                mFragListe.afficheListView(0, mChrono);
            } else {
                mFragListe.setAfficheBtnSuppr(true);
                mFragListe.afficheListView(0, mChrono);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                mChrono.get().resetChrono();
                finish();
                break;
            case R.id.btnAjouterSequence:
                goToAjoutSequenceActivity();
                break;
            default:
                break;
        }
    }

    /**
     * Gère le click sur un item de la listView lorsque le mode suppression n'est pas actif
     *
     * @param parent   ListView contenant l'item cliqué
     * @param view     View sur laquelle l'utilisateur a cliqué
     * @param position position de la View dans la ListView
     * @param id
     */
    @Override
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {

        if (mFragListe.getAfficheBtnSuppr()) {//si mode suppression
            String nom = "";
            switch (mFragListe.getAdapter().getItemViewType(position)) {
                case CustomAdapter.TYPE_ITEM:
                    mChrono.get().setChronoAt(position);
                    ElementSequence e = mChrono.get().getElementSequenceActif();
                    nom = e.getNomExercice();
                    mTypeASuppr = TYPEEXERCICE;
                    break;
                case CustomAdapter.TYPE_SEPARATOR:
                    mChrono.get().setChronoAt(position);
                    Sequence s = mChrono.get().getSeqFromLstSequenceAt(mChrono.get().m_indexSequenceActive);
                    nom = s.getNomSequence();
                    mTypeASuppr = TYPESEQUENCE;
                    break;
                default:
                    break;
            }
            afficheDialogSuppr(nom);

        } else {
            LinearLayout p = (LinearLayout) view;
            mChrono.get().setChronoAt(position);
            switch (mFragListe.getAdapter().getItemViewType(position)) {
                case CustomAdapter.TYPE_ITEM:
                    DialogFragment df = Frag_Dialog_Duree.newInstance(mChrono.get().getElementSequenceActif().getDureeExercice());
                    df.show(getFragmentManager(), "dialog");
                    break;
                case CustomAdapter.TYPE_SEPARATOR:
                    df = Frag_Dialog_Repetition.newInstance(mChrono.get().getSeqFromLstSequenceAt(mChrono.get().m_indexSequenceActive).getNombreRepetition());
                    df.show(getFragmentManager(), "dialog");
                    break;
                default:
                    throw new ClassCastException("View suppr non reconnue");
            }
        }
    }

    @Override
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        mChrono.get().setChronoAt(position);
        goToEditionSequenceActivity();
        return true;

    }


    @Override
    public boolean onLongClickListener(View v) {
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
            DialogFragment df = Frag_AlertDialog_Suppr.newInstance(nom);
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
        switch (mTypeASuppr) {
            case TYPEEXERCICE:
                mChrono.get().supprimeExerciceActif();
                break;
            case TYPESEQUENCE:
                mChrono.get().supprimeSequenceActive();
                break;
            default:
                break;
        }
        mChrono.get().resetChrono();
        mFragListe.afficheListView(0, mChrono);
        mTxtDuree.setText(getString(R.string.listeSequences_tempstotal) + " " + Fonctions.convertSversHMS(mChrono.get().getDureeTotale()));

    }

    /**
     * Gestion de l'appuis sur Cancel de la popup confirmation de suppression
     *
     * @see com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr
     */
    public void doDialogFragCancelClick() {
        mChrono.get().resetChrono();
    }

    /**
     * Affiche la popup nombre de répétitions
     *
     * @see com.stephane.rothen.rchrono.views.Frag_Dialog_Repetition
     */
    private void afficheDialogRepetition() {
        //todo gérer modification d'une séquence ou d'un exercice par creation d'un ou plusieurs doublons
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("dialog") == null) {
            DialogFragment df = Frag_Dialog_Repetition.newInstance(mChrono.get().getSeqFromLstSequenceAt(mChrono.get().m_indexSequenceActive).getNombreRepetition());
            df.show(getFragmentManager(), "dialog");
        }
    }


    /**
     * Gestion de l'appuis sur valider de la Dialog nombre de répétitions
     *
     * @param valeur valeur saisie
     * @see com.stephane.rothen.rchrono.views.Frag_Dialog_Repetition
     */
    @Override
    public void onRetourDialogRepetition(int valeur) {
        Sequence s = (Sequence) mChrono.get().getSequenceActive().clone();
        s.setmNombreRepetition(valeur);
        if (mChrono.get().getDureeTotaleSansSeqActive() + s.getDureeSequence() > 100 * 60 * 60) {
            Toast.makeText(this, R.string.alert_dureeTotaleTropGrande, Toast.LENGTH_LONG).show();
            afficheDialogRepetition();
        } else {
            mChrono.get().remplacerSequenceActive(s);
            chronoService.resetChrono();
            mTxtDuree.setText(getString(R.string.listeSequences_tempstotal) + " " + Fonctions.convertSversHMS(mChrono.get().getDureeTotale()));
        }

    }

    /**
     * Affiche la popup durée
     *
     * @see com.stephane.rothen.rchrono.views.Frag_Dialog_Duree
     */
    private void afficheDialogDuree() {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("dialog") == null) {
            DialogFragment df = Frag_Dialog_Duree.newInstance(mChrono.get().getSeqFromLstSequenceAt(mChrono.get().m_indexSequenceActive).getTabElement().get(mChrono.get().getIndexExerciceActif()).getDureeExercice());
            df.show(getFragmentManager(), "dialog");
        }
    }

    /**
     * Gestion de l'appuis sur valider de la popup durée
     *
     * @param valeur valeur saisie
     */
    @Override
    public void onRetourDialogDuree(int valeur) {
        ElementSequence e = (ElementSequence) mChrono.get().getElementSequenceActif().clone();
        Sequence s = (Sequence) mChrono.get().getSequenceActive().clone();
        e.setDureeExercice(valeur);
        s.getTabElement().set(mChrono.get().m_indexExerciceActif, e);
        if (mChrono.get().getDureeTotaleSansSeqActive() + s.getDureeSequence() > 100 * 60 * 60) {
            Toast.makeText(this, R.string.alert_dureeTotaleTropGrande, Toast.LENGTH_LONG).show();
            afficheDialogDuree();

        } else {
            mChrono.get().remplacerSequenceActive(s);
            chronoService.resetChrono();
            mTxtDuree.setText(getString(R.string.listeSequences_tempstotal) + " " + Fonctions.convertSversHMS(mChrono.get().getDureeTotale()));
        }

    }

    private void goToAjoutSequenceActivity() {
        Intent i = new Intent(this, AjoutSequenceActivity.class);
        startActivityForResult(i, ACTIVITY_AJOUT_SEQ);
    }


    private void goToEditionSequenceActivity() {
        Intent i = new Intent(this, EditionSequenceActivity.class);
        i.putExtra("MODE", 0);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_AJOUT_SEQ) {
            if (resultCode == AjoutSequenceActivity.RESULT_AJOUT) {
                //todo gérer l'erreur
                int derniereSeq = mChrono.get().getListeSequence().size() - 1;
                mChrono.get().setChronoAt(derniereSeq, 0);
                goToEditionSequenceActivity();
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
            if (mChrono != null) {
                switch (intent.getAction()) {
                    case ChronoService.SER_UPDATE_LISTVIEW:
                        int position = intent.getIntExtra(ChronoService.SER_UPDATE_LISTVIEW, -1);
                        if (position != -1) {
                            mFragListe.afficheListView(position, mChrono);
                            mTxtDuree.setText(getString(R.string.listeSequences_tempstotal) + " " + Fonctions.convertSversHMS(mChrono.get().getDureeTotale()));

                        }
                        break;

                }
            }
        }
    }
}
