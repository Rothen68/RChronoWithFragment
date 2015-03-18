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
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_Dialog_EnregistrementSeq;
import com.stephane.rothen.rchrono.views.Frag_EditSeq_BtnExercice;
import com.stephane.rothen.rchrono.views.Frag_EditSeq_Detail;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;
import com.stephane.rothen.rchrono.views.ItemListeExercice;

import java.util.concurrent.atomic.AtomicReference;


/**
 * Classe Activity affichant l'écran EditionSequence
 * Created by stéphane on 15/03/2015.
 */
public class EditionSequenceActivity extends ActionBarActivity implements Frag_Liste_Callback, Frag_Bouton_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback,
        Frag_EditSeq_Detail.Frag_EditSeq_Detail_Callback,
        Frag_Dialog_EnregistrementSeq.Frag_Dialog_EnregistrementSeq_Callback {

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

    /**
     * Séquence temporaire pour la fenetre
     */
    private Sequence mSeqTemp;

    /**
     * Permet de gérer l'appuis double sur le bouton retour
     */
    private boolean mRetourListeVide;

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
                    Sequence s = mChrono.get().getSequenceActive();
                    mSeqTemp = (Sequence) s.clone();
                    mFragDetail.setTxtNom(mSeqTemp.getNomSequence());
                    mFragDetail.setTxtRepetition(mSeqTemp.getNombreRepetition());
                    mFragDetail.setTbNom(mSeqTemp.getSyntheseVocale().getNom());
                    mFragDetail.setTbDuree(mSeqTemp.getSyntheseVocale().getDuree());
                    mFragListe.afficheListView(mSeqTemp);

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
                if (isSequenceModifiee())
                    if (mSeqTemp.getTabElement().size() == 0) {
                        if (!mRetourListeVide) {
                            Toast.makeText(this, getString(R.string.editionseq_listeVide), Toast.LENGTH_LONG).show();
                            mRetourListeVide = true;
                        } else {
                            mChrono.get().resetChrono();
                            finish();
                        }
                    } else {
                        afficheDialogEnrSeq();
                    }
                else {
                    mChrono.get().resetChrono();
                    finish();
                }
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
                ViewParent parent = v.getParent();
                parent = parent.getParent();
                parent = parent.getParent();
                LinearLayout p = (LinearLayout) parent;
                String nom = "";
                if (p instanceof ItemListeExercice) {
                    mExASuppr = ((ItemListeExercice) p).getPosition();
                    nom = mSeqTemp.getTabElement().get(mExASuppr).getNomExercice();
                } else {
                    throw new ClassCastException("View suppr non reconnue");
                }
                afficheDialogSuppr(nom);
                break;
            case R.id.txtLvExercice:
                //todo afficher la popup duree
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

        mSeqTemp.getTabElement().remove(mExASuppr);
        mFragListe.afficheListView(mSeqTemp);

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
     * Affiche la popup d'enregistrement de séquence
     *
     * @see com.stephane.rothen.rchrono.views.Frag_Dialog_EnregistrementSeq
     */
    private void afficheDialogEnrSeq() {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("dialog") == null) {
            DialogFragment df = Frag_Dialog_EnregistrementSeq.newInstance();
            df.show(getFragmentManager(), "dialog");
        }
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
     * Fonction appelée lors du retour de la popup enregistrer sequence avec vérification de la durée de la liste de séquence
     *
     * @param v View du bouton qui a été cliqué
     */
    @Override
    public void onRetourDialogEnrSeq(View v) {
        boolean erreur = false;
        switch (v.getId()) {
            case R.id.dialFragEnrSeqEcraser:
                mSeqTemp.setNomSequence(mFragDetail.getTxtNom());
                mSeqTemp.setmNombreRepetition(mFragDetail.getTxtRepetition());
                mSeqTemp.getSyntheseVocale().setNom(mFragDetail.getTbNom());
                mSeqTemp.getSyntheseVocale().setDuree(mFragDetail.getTbDuree());
                //vérifie que le temps total ne dépasse pas 100h
                int t = mChrono.get().getDureeTotaleSansSeqActive();
                if (t + mSeqTemp.getDureeSequence() > 100 * 60 * 60) {
                    Toast.makeText(this, R.string.alert_dureeTotaleTropGrande, Toast.LENGTH_LONG).show();
                    erreur = true;
                } else {
                    mChrono.get().remplacerSequenceActive(mSeqTemp);
                }
                break;
            case R.id.dialFragEnrSeqNouvelle:
                mSeqTemp.setNomSequence(mFragDetail.getTxtNom());
                mSeqTemp.setmNombreRepetition(mFragDetail.getTxtRepetition());
                mSeqTemp.getSyntheseVocale().setNom(mFragDetail.getTbNom());
                mSeqTemp.getSyntheseVocale().setDuree(mFragDetail.getTbDuree());
                //vérifie que le temps total ne dépasse pas 100h
                t = mChrono.get().getDureeTotale();
                if (t + mSeqTemp.getDureeSequence() > 100 * 60 * 60) {
                    Toast.makeText(this, R.string.alert_dureeTotaleTropGrande, Toast.LENGTH_LONG).show();
                    erreur = true;
                } else {
                    mChrono.get().ajouterSequenceDansListe(mSeqTemp);
                }
                break;
            case R.id.dialFragEnrSeqCancel:
                break;
            default:
                break;
        }
        if (!erreur) {
            mChrono.get().resetChrono();
            finish();
        }
    }

    /**
     * Vérifie si la séquence a été modifiée
     *
     * @return true si la séquence a été modifiée
     */
    private boolean isSequenceModifiee() {
        if (mSeqTemp.equals(mChrono.get().getSequenceActive()))
            return false;
        else
            return true;


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