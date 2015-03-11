package com.stephane.rothen.rchrono;

/**
 * Created by stéphane on 11/03/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.stephane.rothen.rchrono.controller.Chronometre;
import com.stephane.rothen.rchrono.model.Sequence;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChronometreFragment extends Fragment {

    public static final String CHRONOMETREFRAGMENT = "CHRONOMETREFRAGMENT";
    /**
     * Instance de la classe Chronometre
     * @see com.stephane.rothen.rchrono.controller.Chronometre
     */
    private Chronometre mChrono;


    /**
     * Objet de l'interface, ListView qui contient la liste des séquences et des exercices
     */
    private ListView mLv;
    /**
     * Objet permettant de remplir la ListView
     * @see CustomAdapter
     *
     */
    private CustomAdapter mAdapter;
    /**
     * Objet de l'interface, Button qui est l'instance du bouton Start/Pause
     */
    private Button mbtnStart;
    /**
     * Objet de l'inteface, Button qui est l'instance du bouton Reset
     */
    private Button mbtnReset;
    /**
     * Objet de l'interface, TextView qui est l'instance de la zone de texte permettant d'afficer le temps restant pour l'exercice en cours
     */
    private TextView mtxtChrono;


    /**
     * Instance de l'interface OnClickListener
     */
    OnClickListener mClickListener;
    /**
     * Instance de l'interface OnItemClickListener
     */
    OnItemClickListener mItemClickListener;
    /**
     * Instance de l'interface OnItemLongClickListener
     */
    OnItemLongClickListener mItemLongClickListener;


    /**
     * interface OnClickListener
     * <p>Cette interface permet d'envoyer l'évenement OnClick d'un Button vers la classe activité qui a lancé le fragment</p>
     */
    public interface OnClickListener{
        /**
         * Evenement OnClick sur un button
         * @param v
         *      View sur laquelle l'utilisateur aa cliqué
         */
        public void onClickListener(View v);
    }

    /**
     * interface OnItemClickListener
     * <p>Cette interface permet d'envoyer l'évenement OnItemClickListener d'une ListView vers la classe activité qui a lancé le fragment</p>
     */
    public interface OnItemClickListener{
        /**
         * Evenement OnItemClickListener
         * @param parent
         *          ListView contenant l'item cliqué
         * @param view
         *          View sur laquelle l'utilisateur a cliqué
         * @param position
         *          position de la View dans la ListView
         * @param id
         */
        public void onItemClickListener(AdapterView<?> parent, View view, int position, long id);
    }
    /**
     * interface OnItemLongClickListener
     * <p>Cette interface permet d'envoyer l'évenement OnItemLongClickListener d'une ListView vers la classe activité qui a lancé le fragment</p>
     */
    public interface OnItemLongClickListener{
        /**
         * Evenement OnItemLongClickListener
         * @param parent
         *          ListView contenant l'item cliqué
         * @param view
         *          View sur laquelle l'utilisateur a cliqué
         * @param position
         *          position de la View dans la ListView
         * @param id
         */
        public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id);
    }


    /**
     * Fonction appelée quand le fragment est attaché à son Activity
     * @param activity
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mClickListener = (OnClickListener) activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implements OnClickListener");
        }
        try {
            mItemClickListener = (OnItemClickListener) activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implements OnItemClickListener");
        }
        try {
            mItemLongClickListener = (OnItemLongClickListener) activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implements OnItemLongClickListener");
        }

    }

    public ChronometreFragment() {
    }


    /**
     * Initialisation de l'interface du fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chronometre, container, false);
        //Initialisation des éléments de l'interface
        mbtnStart = (Button) rootView.findViewById(R.id.btnStart);
        mbtnStart.setAllCaps(false);

        mbtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Appel de la fonction de l'interface implémentée par l'Activity qui contient le fragment
                mClickListener.onClickListener(v);
            }
        });
        mbtnReset = (Button) rootView.findViewById(R.id.btnReset);
        mbtnReset.setAllCaps(false);
        mbtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClickListener(v);
            }
        });
        mtxtChrono=(TextView) rootView.findViewById(R.id.txtChrono);
        mLv = (ListView) rootView.findViewById(R.id.listView);
        mAdapter=new CustomAdapter(rootView.getContext());
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mItemClickListener.onItemClickListener(parent,view,position,id);
            }
        });
        mLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return mItemLongClickListener.onItemLongClickListener(parent,view,position,id);
            }
        });
        mLv.setAdapter(mAdapter);
        afficheListView(0);
        return rootView;
    }






    public Button getBtnStart(){return mbtnStart;}
    public Button getBtnReset(){return mbtnReset;}
    public TextView getTxtChrono(){return mtxtChrono;}
    public void setChrono(Chronometre c){mChrono=c;}


    /**
     * Initialise le ListView Adapter mAdapter et l'affecte à la ListView mLv
     * @param positionFocus
     *      Permet de définir quel item de la ListView est mis en surbrillance
     *
     * @see com.stephane.rothen.rchrono.ChronometreFragment#mAdapter
     * @see com.stephane.rothen.rchrono.ChronometreFragment#mLv
     *
     */
    public void afficheListView(int positionFocus)
    {
        if( mAdapter.getCount()>0)
        {
            mAdapter.deleteAll();
        }
        mAdapter.setFocusPosition(positionFocus);
        //Parcours la liste des séquences et des exercices pour chaque séquence et les ajoute dans mAdapter
        for (int i=0 ; i <mChrono.getListeSequence().size();i++)
        {
            Sequence s = mChrono.getListeSequence().get(i);
            //si s est la séquence active, afficher le nombre de répétitions restantes
            if( mChrono.getIndexSequenceActive()==i)
            {
                if (mChrono.getNbreRepetition()==0)
                {
                    mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + convertSversHMS(mChrono.getDureeRestanteSequenceActive()));
                }
                else
                {
                    mAdapter.addSectionHeaderItem(s.getNomSequence()+ " - " + mChrono.getNbreRepetition() + "x" + " - " + convertSversHMS(mChrono.getDureeRestanteSequenceActive()));
                }
            }
            //si s est avant la séquence active, donc est déjà passée, met 0 à la durée de la séquence
            else if (mChrono.getIndexSequenceActive()>i) {
                mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + convertSversHMS(0));
            }
            else
            {
                mAdapter.addSectionHeaderItem(s.getNomSequence()+ " - " + s.getNombreRepetition() + "x" + " - " + convertSversHMS(s.getDureeSequence()) );
            }
            for (int j = 0 ; j <s.getTabElement().size();j++)
            {
                mAdapter.addItem(s.getTabElement().get(j).getNomExercice() + " - " + convertSversHMS( s.getTabElement().get(j).getDureeExercice()));
            }
        }
        mAdapter.notifyDataSetChanged();
        final int position = positionFocus;


        mLv.postDelayed(new Runnable() {
            @Override
            public void run() {


                if(mLv.getLastVisiblePosition()<=position)
                {
                    int milieu = (int) ((mLv.getLastVisiblePosition()-mLv.getFirstVisiblePosition())/2);
                    int pos=position+milieu;
                    if(pos<mLv.getCount())
                    {
                        mLv.smoothScrollToPosition(pos);

                    }
                    else
                        mLv.smoothScrollToPosition(mLv.getCount());
                }
                else if (mLv.getFirstVisiblePosition()>=position)
                {
                    int milieu = (int) ((mLv.getLastVisiblePosition()-mLv.getFirstVisiblePosition())/2);
                    int pos=position-milieu;
                    if(pos>0)
                    {
                        mLv.smoothScrollToPosition(pos);

                    }
                    else
                        mLv.smoothScrollToPosition(0);
                }

            }
        },100L);

    }

    /**
     * Met à jour le texte dans la TextView txtChrono
     * @param valeur
     *          valeur à afficher dans la TextView apres conversion en HMS
     *@see com.stephane.rothen.rchrono.ChronometreFragment#mtxtChrono
     */
    public void setTxtChrono(int valeur)
    {
        mtxtChrono.setText(convertSversHMS(valeur));
        mtxtChrono.invalidate();
    }

    /**
     * Fonction permettant de convertir une durée en seconde stocké dans un entier en une chaine de caractere au format HH : MM : SS
     * @param s
     *      durée en seconde à convertir
     * @return
     *      chaine de caractere contenant la durée convertie
     */
    private String convertSversHMS(int s){
        int heure = (int)s/3600;
        int minute =(int) (s-3600*heure)/60;
        int seconde =(int) s- 3600*heure - 60* minute;
        String valeur = new String();
        if( heure<10)
            valeur = valeur + "0" ;
        valeur = valeur + String.valueOf(heure) + " : ";

        if(minute<10)
            valeur = valeur + "0";
        valeur=valeur + String.valueOf(minute) + " : ";

        if(seconde<10)
            valeur = valeur + "0";
        valeur= valeur + String.valueOf(seconde);
        return valeur;
    }



}
