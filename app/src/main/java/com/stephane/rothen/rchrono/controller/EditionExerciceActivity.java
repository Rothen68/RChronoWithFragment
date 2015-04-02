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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.stephane.rothen.rchrono.Fonctions;
import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Morceau;
import com.stephane.rothen.rchrono.model.NotificationExercice;
import com.stephane.rothen.rchrono.model.Playlist;
import com.stephane.rothen.rchrono.model.Sequence;
import com.stephane.rothen.rchrono.model.SyntheseVocale;
import com.stephane.rothen.rchrono.views.Frag_Dialog_Duree;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by stéphane on 31/03/2015.
 */
public class EditionExerciceActivity extends ActionBarActivity implements View.OnClickListener,
        Frag_Dialog_Duree.Frag_Dialog_Duree_Callback {


    private static final int LISTESONS_SONNERIE = 1;
    private static final int LISTESONS_MORCEAU = 2;

    private static final int CREATION = 1;
    private static final int EDITION = 2;
    private int mMode = EDITION;
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
    private EditText mEtxtNom;
    private EditText mEtxtDescription;
    private EditText mEtxtDuree;
    private int mDuree;
    private ToggleButton mTbNom;
    private boolean mEtatTbNom = false;
    private ToggleButton mTbDuree;
    private boolean mEtatTbDuree = false;
    private ToggleButton mTbVibreur;
    private boolean mEtatTbVibreur = false;
    private ToggleButton mTbPopup;
    private boolean mEtatTbPopup = false;
    private ToggleButton mTbSonnerie;
    private boolean mEtatTbSonnerie = false;
    private EditText mEtxtSonnerie;
    private Morceau mSonnerie = null;
    private ToggleButton mTbJouerPlaylist;
    private boolean mEtatTbJouerPlaylist = false;
    private Button mBtnValider;
    private Button mBtnRetour;
    /**
     * Permet de stocker les données de l'ElementSequence en cours de modification lors du basculement vers la fenetre de la playlist
     */
    private boolean mSauvegarderDonneesTemp = false;
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

        Intent i = getIntent();
        if (i.getExtras() != null) {
            mMode = i.getExtras().getInt("MODE");
        }
        setContentView(R.layout.editionex_main);
        mEtxtNom = (EditText) findViewById(R.id.editionex_main_etxtNom);
        mEtxtDescription = (EditText) findViewById(R.id.editionex_main_etxtDescription);
        mEtxtDuree = (EditText) findViewById(R.id.editionex_main_etxtDuree);
        mTbNom = (ToggleButton) findViewById(R.id.editionex_main_tbNom);
        mTbDuree = (ToggleButton) findViewById(R.id.editionex_main_tbDuree);
        mTbPopup = (ToggleButton) findViewById(R.id.editionex_main_tbPopup);
        mTbVibreur = (ToggleButton) findViewById(R.id.editionex_main_tbVibreur);
        mTbSonnerie = (ToggleButton) findViewById(R.id.editionex_main_tbSonnerie);
        mEtxtSonnerie = (EditText) findViewById(R.id.editionex_main_etxtSonnerie);
        mTbJouerPlaylist = (ToggleButton) findViewById(R.id.editionex_main_tbJouerPlaylist);
        mBtnValider = (Button) findViewById(R.id.editionex_main_btnValider);
        mBtnRetour = (Button) findViewById(R.id.editionex_main_btnRetour);

        mEtxtDuree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficheDialogDuree();
            }
        });

        mEtxtSonnerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeSonsPourSonnerie();
            }
        });

        mTbJouerPlaylist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mEtatTbJouerPlaylist != isChecked) {
                    mEtatTbJouerPlaylist = isChecked;
                    if (isChecked) {
                        gotToEditionExercicePlaylist();
                    }
                }
            }
        });

        mTbNom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbNom = isChecked;
            }
        });

        mTbDuree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbDuree = isChecked;
            }
        });

        mTbPopup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbPopup = isChecked;
            }
        });

        mTbVibreur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbVibreur = isChecked;
            }
        });

        mTbSonnerie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEtatTbSonnerie = isChecked;
                if (isChecked && mSonnerie == null) {
                    listeSonsPourSonnerie();
                }
            }
        });

        mBtnValider.setOnClickListener(this);
        mBtnRetour.setOnClickListener(this);







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
                    if (mMode == EDITION) {
                        ElementSequence el = mChrono.get().getElementSeqTemp();
                        if (el == null) {
                            el = mChrono.get().getSeqTemp().getTabElement().get(mChrono.get().getIndexElementSeqTemp());
                            if (mElementSeqTemp == null)
                                mElementSeqTemp = (ElementSequence) el.clone();
                        } else {
                            mElementSeqTemp = el;
                            mSauvegarderDonneesTemp = false;
                        }
                    } else {
                        mElementSeqTemp = new ElementSequence("", "", 1, new Playlist(), 1, new Playlist(), new NotificationExercice(false, false, false, null), new SyntheseVocale(false, false));
                    }
                    mEtxtNom.setText(mElementSeqTemp.getNomExercice());
                    mEtxtDescription.setText(mElementSeqTemp.getDescriptionExercice());
                    mEtxtDuree.setText(Fonctions.convertSversHMSSansZeros(mElementSeqTemp.getDureeExercice()));
                    mDuree = mElementSeqTemp.getDureeExercice();
                    mEtatTbNom = mElementSeqTemp.getSyntheseVocale().getNom();
                    mTbNom.setChecked(mEtatTbNom);
                    mEtatTbDuree = mElementSeqTemp.getSyntheseVocale().getDuree();
                    mTbDuree.setChecked(mEtatTbDuree);
                    mEtatTbPopup = mElementSeqTemp.getNotificationExercice().getPopup();
                    mTbPopup.setChecked(mEtatTbPopup);
                    mEtatTbVibreur = mElementSeqTemp.getNotificationExercice().getVibreur();
                    mTbVibreur.setChecked(mEtatTbVibreur);
                    mEtatTbSonnerie = mElementSeqTemp.getNotificationExercice().getSonnerie();
                    mTbSonnerie.setChecked(mEtatTbSonnerie);
                    mSonnerie = mElementSeqTemp.getNotificationExercice().getFichierSonnerie();
                    if (mSonnerie != null)
                        mEtxtSonnerie.setText(mSonnerie.getTitre());
                    mEtatTbJouerPlaylist = mElementSeqTemp.getPlaylistExercice().getJouerPlaylist();
                    mTbJouerPlaylist.setChecked(mEtatTbJouerPlaylist);
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
        if (mSauvegarderDonneesTemp) {
            mElementSeqTemp.setNomExercice(mEtxtNom.getText().toString());
            mElementSeqTemp.setDescriptionExercice(mEtxtDescription.getText().toString());
            mElementSeqTemp.setDureeExercice(mDuree);
            mElementSeqTemp.setSyntheseVocale(new SyntheseVocale(mEtatTbNom, mEtatTbDuree));
            mElementSeqTemp.setNotificationExercice(new NotificationExercice(mEtatTbVibreur, mEtatTbPopup, mEtatTbSonnerie, mSonnerie));
            mElementSeqTemp.getPlaylistExercice().setJouerPlaylist(mEtatTbJouerPlaylist);
            mChrono.get().setElementSeqTemp(mElementSeqTemp);
        } else {
            mChrono.get().setElementSeqTemp(null);
        }
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


    private void listeSonsPourSonnerie() {

        Intent i = new Intent(this, ListeSonsActivity.class);
        startActivityForResult(i, LISTESONS_SONNERIE);
    }

    private void gotToEditionExercicePlaylist() {
        mSauvegarderDonneesTemp = true;
        Intent i = new Intent(this, EditionExercicePlaylistActivity.class);
        startActivity(i);
    }


    /**
     * Gestion de l'appuis sur valider de la popup durée
     *
     * @param valeur valeur saisie
     */
    @Override
    public void onRetourDialogDuree(int valeur) {

        Sequence s = (Sequence) mChrono.get().getSeqTemp().clone();
        if (mMode == EDITION) {
            s.getTabElement().set(mChrono.get().getIndexElementSeqTemp(), mElementSeqTemp);
            s.getTabElement().get(mChrono.get().getIndexElementSeqTemp()).setDureeExercice(valeur);
        } else {
            s.ajouterElement(mElementSeqTemp);
        }
        if (mChrono.get().getDureeTotaleSansSeqActive() + s.getDureeSequence() > 100 * 60 * 60) {
            Toast.makeText(this, R.string.alert_dureeTotaleTropGrande, Toast.LENGTH_LONG).show();
            afficheDialogDuree();

        } else {
            mDuree = valeur;
            mEtxtDuree.setText(Fonctions.convertSversHMSSansZeros(valeur));
        }

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
        if ((requestCode == LISTESONS_MORCEAU) && resultCode == ListeSonsActivity.RESULT_OK) {
            long id = data.getLongExtra("ID", -1);
            String titre = data.getStringExtra("TITRE");
            String artiste = data.getStringExtra("ARTISTE");
            if (id != -1) {
                switch (requestCode) {

                    case LISTESONS_SONNERIE:

                        mElementSeqTemp.getNotificationExercice().setFichierSonnerie(new Morceau(id, titre, artiste));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editionex_main_btnRetour:
                mChrono.get().setElementSeqTemp(null);
                finish();
                break;

            case R.id.editionex_main_btnValider:
                mElementSeqTemp.setNomExercice(mEtxtNom.getText().toString());
                mElementSeqTemp.setDescriptionExercice(mEtxtDescription.getText().toString());
                mElementSeqTemp.setDureeExercice(mDuree);
                mElementSeqTemp.setSyntheseVocale(new SyntheseVocale(mEtatTbNom, mEtatTbDuree));
                mElementSeqTemp.setNotificationExercice(new NotificationExercice(mEtatTbVibreur, mEtatTbPopup, mEtatTbSonnerie, mSonnerie));
                mElementSeqTemp.getPlaylistExercice().setJouerPlaylist(mEtatTbJouerPlaylist);
                if (mMode == EDITION) {

                    mChrono.get().getSeqTemp().getTabElement().set(mChrono.get().getIndexElementSeqTemp(), mElementSeqTemp);
                    mChrono.get().setElementSeqTemp(null);
                    finish();
                } else {
                    mChrono.get().getSeqTemp().ajouterElement(mElementSeqTemp);
                    finish();
                }
                break;
            default:
                break;
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
