package com.stephane.rothen.rchrono;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.stephane.rothen.rchrono.controller.Chronometre;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Sequence;


public class ChronometreActivity extends ActionBarActivity implements ChronometreFragment.OnClickListener,ChronometreFragment.OnItemClickListener,ChronometreFragment.OnItemLongClickListener
{


    /**
     * Instance de la classe Chronometre
     * @see com.stephane.rothen.rchrono.controller.Chronometre
     */
    private Chronometre mChrono;
    /**
     * Objet permettant de récupérer l'instance du service ChronoService
     * @see com.stephane.rothen.rchrono.ChronoService
     */
    private ChronoService chronoService;
    /**
     * Objet permettant la communication entre le service et l'activity
     * @see com.stephane.rothen.rchrono.ChronometreActivity.MyReceiver
     *
     */
    private MyReceiver myReceiver;

    /**
     * Objet permettant de gérer la communication de l'interface vers le service, il initialise chronoService
     * @see com.stephane.rothen.rchrono.ChronometreActivity#chronoService
     */
    private ServiceConnection mConnexion;

    /**
     * Stockage du fragment actuel
     */
    private ChronometreFragment mFragment;


 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometre);
        if (savedInstanceState == null) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, new ChronometreFragment(),ChronometreFragment.CHRONOMETREFRAGMENT);
            ft.commit();
        }
        getSupportFragmentManager().executePendingTransactions();
        mFragment = (ChronometreFragment) getSupportFragmentManager().findFragmentByTag(ChronometreFragment.CHRONOMETREFRAGMENT);
        mChrono = new Chronometre(getApplication());

        mFragment.setChrono(mChrono);
        //initialisation du receiver qui permet la communication vers l'interface depuis chronoService
        myReceiver=new MyReceiver();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(ChronoService.SER_TEMPS_RESTANT);
        ifilter.addAction(ChronoService.SER_UPDATE_LISTVIEW);
        ifilter.addAction(ChronoService.SER_FIN_LISTESEQUENCE);
        registerReceiver(myReceiver, ifilter);





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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Lancement du ChronoService dans onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Service", "Dans onResume");
        //Lancement du service ChronoService
        mConnexion =new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                chronoService =  ((ChronoService.MonBinder) service).getService();
                if(chronoService.getChronometre()==null)
                    chronoService.setChronometre(mChrono);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                chronoService=null;
            }
        };
        Intent intent = new Intent(getApplicationContext(),ChronoService.class);
        intent.putExtra(ChronoService.SER_ACTION, 0);
        startService(intent);
        bindService(intent,mConnexion,BIND_AUTO_CREATE);
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d("Service", "Dans onPause");
    }

    /**
     * Arrêt du service ChronoService dans onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Service", "Dans onStop");
        unbindService(mConnexion);


    }


    @Override
    protected void onDestroy() {
        Log.d("Service", "Dans onDestroy");

        stopService(new Intent(this,ChronoService.class));
        chronoService=null;
        mConnexion=null;
        super.onDestroy();
    }

    /**
     * Implémentation de l'interface permettant de détecter le click sur un bouton d'un fragment
     * @param v
     */
    @Override
    public void onClickListener(View v) {
        switch(v.getId())
        {
            case R.id.btnStart:
                if (chronoService != null) {
                    if (chronoService.getChronoStart()) {
                        chronoService.stopChrono();
                        ((Button)v).setText(R.string.chronometre_start);
                    } else {
                        chronoService.startChrono();
                        ((Button)v).setText(R.string.chronometre_pause);
                    }
                }
                break;
            case R.id.btnReset:
                if (chronoService != null) {
                    chronoService.resetChrono();
                }

                break;
            case R.id.txtChrono:
                int type = mChrono.getTypeAffichage();
                switch (type)
                {
                    case Chronometre.AFFICHAGE_TEMPS_EX:
                        mChrono.setTypeAffichage(Chronometre.AFFICHAGE_TEMPS_SEQ);
                        ((TextView)findViewById(R.id.txtDescChrono)).setText(R.string.descChronometre_Sequence);
                        mFragment.setTxtChrono(mChrono.getDureeRestanteSequenceActive());
                        break;
                    case Chronometre.AFFICHAGE_TEMPS_SEQ:
                        mChrono.setTypeAffichage(Chronometre.AFFICHAGE_TEMPS_TOTAL);
                        ((TextView)findViewById(R.id.txtDescChrono)).setText(R.string.descChronometre_Total);
                        mFragment.setTxtChrono(mChrono.getDureeRestanteTotale());
                        break;
                    case Chronometre.AFFICHAGE_TEMPS_TOTAL:
                        mChrono.setTypeAffichage(Chronometre.AFFICHAGE_TEMPS_EX);
                        ((TextView)findViewById(R.id.txtDescChrono)).setText(R.string.descChronometre_Exercice);
                        mFragment.setTxtChrono(mChrono.getDureeRestanteExerciceActif());
                        break;
                    default :
                        break;
                }
                int position=1;
                int exercice =mChrono.getIndexExerciceActif();
                int seq = mChrono.getIndexSequenceActive();
                if(exercice>=0) {
                    for (int j = 0; j < seq; j++) {
                        position++;
                        for (ElementSequence e : mChrono.getListeSequence().get(j).getTabElement()) {
                            position++;
                        }
                    }
                    position = position + exercice;
                }
                mFragment.afficheListView( position);
                break;

            default :
                break;
        }
    }

    /**
     * implémentation de l'interface permettant de détecter le click sur la ListView d'un fragment
     * @param parent
     *          ListView contenant l'item cliqué
     * @param view
     *          View sur laquelle l'utilisateur a cliqué
     * @param position
     *          position de la View dans la ListView
     * @param id
     */
    @Override
    public void onItemClickListener(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId())
        {
            /*
             * Lors d'un appuis court sur un item de la ListView, arrête le chrono et le place sur l'exercice sélectionné, ou en cas de séquence sélectionnée, sur le premier exercice de la séquence
             * @param parent
             */
            case R.id.listView:
                chronoService.stopChrono();
                int posExercice = mChrono.setChronoAt(position);
                if(posExercice>-1)
                    mFragment.afficheListView(posExercice);
                mFragment.getBtnStart().setText(R.string.chronometre_start);
                chronoService.updateChrono();
                break;
            default:
                break;
        }
    }

    /**
     * Implémentation de l'interface permettant de détecter le long click sur la ListView d'un fragment
     * @param parent
     *          ListView contenant l'item cliqué
     * @param view
     *          View sur laquelle l'utilisateur a cliqué
     * @param position
     *          position de la View dans la ListView
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id) {
        chronoService.stopChrono();
        //TODO : ouvrir la fenetre ListeSequenceActivity
        return true;
    }




    /**
     * Classe privée MyReceiver
     * <p>Elle permet de récupérer et de traiter des broadcast venant de chronoService</p>
     * @see com.stephane.rothen.rchrono.ChronoService
     */
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction())
            {
                case ChronoService.SER_TEMPS_RESTANT :
                    int tempsRestant = intent.getIntExtra(ChronoService.SER_TEMPS_RESTANT,-1);
                    if( tempsRestant!=-1)
                    {
                        mFragment.setTxtChrono(tempsRestant);
                    }
                    break;
                case ChronoService.SER_UPDATE_LISTVIEW:
                    int position = intent.getIntExtra(ChronoService.SER_UPDATE_LISTVIEW,-1);
                    if ( position !=-1) {
                        mFragment.afficheListView(position);

                    }
                    break;
                case ChronoService.SER_FIN_LISTESEQUENCE:
                    mFragment.getBtnStart().setText(R.string.chronometre_start);
                    mFragment.getBtnStart().invalidate();
                    mFragment.afficheListView( 0);
            }
        }
    }
}
