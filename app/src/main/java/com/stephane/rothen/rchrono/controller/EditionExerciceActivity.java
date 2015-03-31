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
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Morceau;
import com.stephane.rothen.rchrono.model.NotificationExercice;
import com.stephane.rothen.rchrono.model.Sequence;
import com.stephane.rothen.rchrono.model.SyntheseVocale;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Bouton_Callback;
import com.stephane.rothen.rchrono.views.Frag_Dialog_Duree;
import com.stephane.rothen.rchrono.views.Frag_EditEx_BtnAjoutMorceau;
import com.stephane.rothen.rchrono.views.Frag_EditEx_BtnValider;
import com.stephane.rothen.rchrono.views.Frag_EditEx_Detail;
import com.stephane.rothen.rchrono.views.Frag_EditEx_Playlist;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;
import com.stephane.rothen.rchrono.views.ItemListeExercice;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by stéphane on 31/03/2015.
 */
public class EditionExerciceActivity extends ActionBarActivity implements Frag_Liste_Callback, Frag_Bouton_Callback,
        Frag_EditEx_Detail.Frag_EditEx_Detail_Callback,
        Frag_Dialog_Duree.Frag_Dialog_Duree_Callback,
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
     * Instance de la classe du fragment affichant les détails de l'exercice
     */
    private Frag_EditEx_Detail mFragDetail;

    /**
     * Instance de la classe du fragment affichant la liste des morceaux de la playlist de l'exercice
     */
    private Frag_ListeItems mFragListe;
    /**
     * Instance de la classe du fragment affichant le togglebutton jouer la playlist
     */
    private Frag_EditEx_Playlist mFragPlaylist;
    /**
     * Instance de la classe du fragment affichant le bouton ajouter un morceau à la playlist
     */
    private Frag_EditEx_BtnAjoutMorceau mFragBtnAjoutMorceau;

    /**
     * Instance de la classe du fragment affichant le bouton valider
     */
    private Frag_EditEx_BtnValider mFragBtnValider;
    /**
     * Instance de la classe du fragment affichant le bouton retour
     */
    private Frag_BoutonRetour mFragBtnRetour;


    /**
     * ElementSequence temporaire pour la fenetre
     */
    private ElementSequence mElementSeqTemp;

    /**
     * Morceau à supprimer
     */
    private Morceau mMorceauASuppr;

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
        setContentView(R.layout.editionex_host_frag);
        if (savedInstanceState == null) {
        }
        getSupportFragmentManager().executePendingTransactions();
        mFragDetail = (Frag_EditEx_Detail) getSupportFragmentManager().findFragmentById(R.id.Frag_EditEx_Detail);

        mFragPlaylist = (Frag_EditEx_Playlist) getSupportFragmentManager().findFragmentById(R.id.Frag_EditEx_BtnPlaylist);

        mFragListe = (Frag_ListeItems) getSupportFragmentManager().findFragmentById(R.id.Frag_EditEx_Liste);
        mFragListe.setAfficheBtnSuppr(false);
        mFragListe.setTypeAffichage(Frag_ListeItems.AFFICHE_PLAYLISTEXERCICEACTIF);

        mFragBtnAjoutMorceau = (Frag_EditEx_BtnAjoutMorceau) getSupportFragmentManager().findFragmentById(R.id.Frag_EditEx_BtnAjouterMorceau);

        mFragBtnValider = (Frag_EditEx_BtnValider) getSupportFragmentManager().findFragmentById(R.id.Frag_EditEx_BtnValider);

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
                    ElementSequence el = mChrono.get().getElementSequenceActif();
                    mElementSeqTemp = (ElementSequence) el.clone();
                    mFragDetail.setTxtNom(mElementSeqTemp.getNomExercice());
                    mFragDetail.setTxtDescription(mElementSeqTemp.getDescriptionExercice());
                    mFragDetail.setTxtDuree(mElementSeqTemp.getDureeExercice());
                    mFragDetail.setTbNom(mElementSeqTemp.getSyntheseVocale().getNom());
                    mFragDetail.setTbDuree(mElementSeqTemp.getSyntheseVocale().getDuree());
                    mFragDetail.setTbPopup(mElementSeqTemp.getNotificationExercice().getPopup());
                    mFragDetail.setTbVibreur(mElementSeqTemp.getNotificationExercice().getVibreur());
                    mFragDetail.setTbSonnerie(mElementSeqTemp.getNotificationExercice().getSonnerie());
                    mFragDetail.setSonnerie(mElementSeqTemp.getNotificationExercice().getFichierSonnerie());
                    mFragPlaylist.setJouerPlaylist(mElementSeqTemp.getPlaylistExercice().getJouerPlaylist());
                    mFragListe.afficheListView(mElementSeqTemp.getPlaylistExercice());

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
                mChrono.get().resetChrono();
                finish();
                break;

            case R.id.editionex_frag_valider_btnValider:
                mElementSeqTemp.setNomExercice(mFragDetail.getTxtNom());
                mElementSeqTemp.setDescriptionExercice(mFragDetail.getTxtDescription());
                mElementSeqTemp.setDureeExercice(mFragDetail.getTxtDuree());
                mElementSeqTemp.setSyntheseVocale(new SyntheseVocale(mFragDetail.getTbNom(), mFragDetail.getTbDuree()));
                mElementSeqTemp.setNotificationExercice(new NotificationExercice(mFragDetail.getTbVibreur(), mFragDetail.getTbPopup(), mFragDetail.getTbSonnerie(), mFragDetail.getSonnerie()));
                mElementSeqTemp.getPlaylistExercice().setJouerPlaylist(mFragPlaylist.getJouerPlaylist());
                mChrono.get().remplacerElementSequenceActif(mElementSeqTemp);
                finish();
                break;
            case R.id.btnSuppr:
                ViewParent parent = v.getParent();
                parent = parent.getParent();
                parent = parent.getParent();
                LinearLayout p = (LinearLayout) parent;
                int indexMorceauASuppr = -1;
                if (p instanceof ItemListeExercice) {
                    indexMorceauASuppr = ((ItemListeExercice) p).getPosition();

                } else {
                    throw new ClassCastException("View suppr non reconnue");
                }
                if (indexMorceauASuppr >= 0) {
                    mMorceauASuppr = mElementSeqTemp.getPlaylistExercice().getMorceauAt(indexMorceauASuppr);
                    afficheDialogSuppr(mMorceauASuppr.getTitre());
                }
                break;
            case R.id.editionex_frag_detail_etxtDuree:
                afficheDialogDuree();
                break;
            case R.id.editionex_frag_detail_etxtSonnerie:
                //todo affiche liste fichiers audio
                break;
            case R.id.editionex_frag_ajoutmorceau_btAjout:
                //todo affiche liste fichiers audio
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

        mElementSeqTemp.getPlaylistExercice().remove(mMorceauASuppr);
        mFragListe.afficheListView(mElementSeqTemp.getPlaylistExercice());

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
     * Affiche la popup durée
     *
     * @see com.stephane.rothen.rchrono.views.Frag_Dialog_Duree
     */
    private void afficheDialogDuree() {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("dialog") == null) {
            DialogFragment df = Frag_Dialog_Duree.newInstance(mElementSeqTemp.getDureeExercice());
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

        Sequence s = (Sequence) mChrono.get().getSequenceActive().clone();
        s.getTabElement().set(mChrono.get().getIndexExerciceActif(), mElementSeqTemp);
        s.getTabElement().get(mChrono.get().getIndexExerciceActif()).setDureeExercice(valeur);
        if (mChrono.get().getDureeTotaleSansSeqActive() + s.getDureeSequence() > 100 * 60 * 60) {
            Toast.makeText(this, R.string.alert_dureeTotaleTropGrande, Toast.LENGTH_LONG).show();
            afficheDialogDuree();

        } else {
            mElementSeqTemp.setDureeExercice(valeur);
            mFragDetail.setTxtDuree(valeur);
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
     * Vérifie si la séquence a été modifiée
     *
     * @return true si la séquence a été modifiée
     */
    private boolean isElementSequenceModifiee() {
        if (mElementSeqTemp.equals(mChrono.get().getElementSequenceActif()))
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
