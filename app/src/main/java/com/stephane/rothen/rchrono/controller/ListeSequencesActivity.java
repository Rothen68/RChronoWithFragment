package com.stephane.rothen.rchrono.controller;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Sequence;
import com.stephane.rothen.rchrono.views.Frag_AlertDialog_Suppr;
import com.stephane.rothen.rchrono.views.Frag_Dialog_Repetition;
import com.stephane.rothen.rchrono.views.Frag_ListeItems;
import com.stephane.rothen.rchrono.views.Frag_ListeSeq_BoutonAjoutSeq;
import com.stephane.rothen.rchrono.views.Frag_ListeSeq_BoutonRetour;
import com.stephane.rothen.rchrono.views.Frag_Liste_Callback;
import com.stephane.rothen.rchrono.views.ItemListeExercice;
import com.stephane.rothen.rchrono.views.ItemListeSequence;

import java.util.concurrent.atomic.AtomicReference;


public class ListeSequencesActivity extends ActionBarActivity implements Frag_ListeSeq_BoutonRetour.Frag_ListeSeq_BoutonRetour_Callback,
        Frag_ListeSeq_BoutonAjoutSeq.Frag_ListeSeq_BoutonAjoutSeq_Callback,
        Frag_Liste_Callback,
        Frag_AlertDialog_Suppr.Frag_AlertDialog_Suppr_Callback,
        Frag_Dialog_Repetition.Frag_Dialog_Repetition_Callback

{
    private static final int TYPESEQUENCE = 1;
    private static final int TYPEEXERCICE = 2;
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
    private Frag_ListeSeq_BoutonAjoutSeq mFragBtnAjouterSeq;
    /**
     * Instance de la classe du fragment affichant le bouton retour
     */
    private Frag_ListeSeq_BoutonRetour mFragBtnRetour;
    private int mTypeASuppr = 0;

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
        mFragBtnAjouterSeq = (Frag_ListeSeq_BoutonAjoutSeq) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnAjouterSeq);
        mFragBtnRetour = (Frag_ListeSeq_BoutonRetour) getSupportFragmentManager().findFragmentById(R.id.Frag_ListeSeq_BtnRetour);


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
                    mChrono = new AtomicReference<>(new Chronometre(getApplication()));
                    chronoService.setAtomicChronometre(mChrono);
                    chronoService.updateListView();
                } else {
                    mChrono = chronoService.getAtomicChronometre();
                    chronoService.updateListView();
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
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout p = (LinearLayout) view;
        mChrono.get().setChronoAt(position);
        switch (mFragListe.getAdapter().getItemViewType(position)) {
            case CustomAdapter.TYPE_ITEM:

                break;
            case CustomAdapter.TYPE_SEPARATOR:
                DialogFragment df = Frag_Dialog_Repetition.newInstance(mChrono.get().getListeSequence().get(mChrono.get().m_indexSequenceActive).getNombreRepetition());
                df.show(getFragmentManager(), "dialog");
                break;
            default:
                throw new ClassCastException("View suppr non reconnue");
        }
    }

    @Override
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.btnRetour:
                finish();
                break;
            case R.id.btnAjouterSequence:
                break;
            case R.id.btnSuppr:
                //gestion du click sur le bouton supprimer d'un élément de la listView
                ViewParent parent = v.getParent();
                parent = parent.getParent();
                parent = parent.getParent();
                LinearLayout p = (LinearLayout) parent;
                int position = -2;
                String nom = "";
                if (p instanceof ItemListeExercice) {
                    position = ((ItemListeExercice) p).getPosition();
                    mChrono.get().setChronoAt(position);
                    ElementSequence e = mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getTabElement().get(mChrono.get().getIndexExerciceActif());
                    nom = e.getNomExercice();
                    mTypeASuppr = TYPEEXERCICE;
                } else if (p instanceof ItemListeSequence) {
                    position = ((ItemListeSequence) p).getPosition();
                    mChrono.get().setChronoAt(position);
                    Sequence s = mChrono.get().getListeSequence().get(mChrono.get().m_indexSequenceActive);
                    nom = s.getNomSequence();
                    mTypeASuppr = TYPESEQUENCE;
                } else {
                    throw new ClassCastException("View suppr non reconnue");
                }
                DialogFragment df = Frag_AlertDialog_Suppr.newInstance(nom);
                df.show(getFragmentManager(), "dialog");
                break;

            case R.id.txtLvExercice:
                Toast.makeText(this, "Exercice", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtLvSequence:
                parent = v.getParent();
                parent = parent.getParent();
                p = (LinearLayout) parent;
                position = -2;
                if (p instanceof ItemListeExercice) {
                    position = ((ItemListeExercice) p).getPosition();
                    //todo afficher popup duree exercice

                } else if (p instanceof ItemListeSequence) {
                    position = ((ItemListeSequence) p).getPosition();
                    mChrono.get().setChronoAt(position);
                    df = Frag_Dialog_Repetition.newInstance(mChrono.get().getListeSequence().get(mChrono.get().m_indexSequenceActive).getNombreRepetition());
                    df.show(getFragmentManager(), "dialog");
                } else {
                    throw new ClassCastException("View Item non reconnue");
                }
        }

    }

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
        mChrono.get().setChronoAt(0);
        mFragListe.afficheListView(0, mChrono);

    }

    public void doDialogFragCancelClick() {

    }

    @Override
    public void onClickListener(View v, int valeur) {
        mChrono.get().getListeSequence().get(mChrono.get().m_indexSequenceActive).setM_nombreRepetition(valeur);
        chronoService.resetChrono();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_liste_sequences, container, false);
            return rootView;
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

                        }
                        break;

                }
            }
        }
    }
}
