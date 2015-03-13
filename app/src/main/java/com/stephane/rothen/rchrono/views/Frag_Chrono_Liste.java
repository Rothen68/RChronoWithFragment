package com.stephane.rothen.rchrono.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stephane.rothen.rchrono.Fonctions;
import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.controller.Chronometre;
import com.stephane.rothen.rchrono.controller.CustomAdapter;
import com.stephane.rothen.rchrono.model.Sequence;

/**
 * Fragment stockant la listView de la fenetre RChrono
 * Created by stéphane on 13/03/2015.
 */
public class Frag_Chrono_Liste extends Fragment {

    public static final String FRAG_CHRONO_LISTE = "FRAG_CHRONO_LISTE";


    /**
     * Instance de l'interface OnClickListener
     */
    private Frag_Chrono_Liste_Callback mCallback;
    /**
     * Objet de l'interface, ListView qui contient la liste des séquences et des exercices
     */
    private ListView mLv;
    /**
     * Objet permettant de remplir la ListView
     *
     * @see com.stephane.rothen.rchrono.controller.CustomAdapter
     */
    private CustomAdapter mAdapter;

    public Frag_Chrono_Liste() {
    }

    /**
     * Fonction appelée quand le fragment est attaché à son Activity
     *
     * @param activity
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Frag_Chrono_Liste_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_Chrono_Liste_Callback");
        }


    }

    /**
     * Initialisation de l'interface du fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chrono_frag_liste, container, false);
        mLv = (ListView) rootView.findViewById(R.id.lstChrono);
        mAdapter = new CustomAdapter(getActivity().getApplicationContext());
        mLv.setAdapter(mAdapter);

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onItemClickListener(parent, view, position, id);
            }
        });
        mLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return mCallback.onItemLongClickListener(parent, view, position, id);
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Initialise le ListView Adapter mAdapter et l'affecte à la ListView mLv
     *
     * @param positionFocus Permet de définir quel item de la ListView est mis en surbrillance
     */
    public void afficheListView(int positionFocus, Chronometre mChrono) {
        if (mAdapter.getCount() > 0) {
            mAdapter.deleteAll();
        }
        mAdapter.setFocusPosition(positionFocus);
        //Parcours la liste des séquences et des exercices pour chaque séquence et les ajoute dans mAdapter
        for (int i = 0; i < mChrono.getListeSequence().size(); i++) {
            Sequence s = mChrono.getListeSequence().get(i);
            //si s est la séquence active, afficher le nombre de répétitions restantes
            if (mChrono.getIndexSequenceActive() == i) {
                if (mChrono.getNbreRepetition() == 0) {
                    mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + Fonctions.convertSversHMS(mChrono.getDureeRestanteSequenceActive()));
                } else {
                    mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + mChrono.getNbreRepetition() + "x" + " - " + Fonctions.convertSversHMS(mChrono.getDureeRestanteSequenceActive()));
                }
            }
            //si s est avant la séquence active, donc est déjà passée, met 0 à la durée de la séquence
            else if (mChrono.getIndexSequenceActive() > i) {
                mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + Fonctions.convertSversHMS(0));
            } else {
                mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + s.getNombreRepetition() + "x" + " - " + Fonctions.convertSversHMS(s.getDureeSequence()));
            }
            for (int j = 0; j < s.getTabElement().size(); j++) {
                mAdapter.addItem(s.getTabElement().get(j).getNomExercice() + " - " + Fonctions.convertSversHMS(s.getTabElement().get(j).getDureeExercice()));
            }
        }
        mAdapter.notifyDataSetChanged();
        final int position = positionFocus;


        mLv.postDelayed(new Runnable() {
            @Override
            public void run() {


                if (mLv.getLastVisiblePosition() <= position) {
                    int milieu = (int) ((mLv.getLastVisiblePosition() - mLv.getFirstVisiblePosition()) / 2);
                    int pos = position + milieu;
                    if (pos < mLv.getCount()) {
                        mLv.smoothScrollToPosition(pos);

                    } else
                        mLv.smoothScrollToPosition(mLv.getCount());
                } else if (mLv.getFirstVisiblePosition() >= position) {
                    int milieu = (int) ((mLv.getLastVisiblePosition() - mLv.getFirstVisiblePosition()) / 2);
                    int pos = position - milieu;
                    if (pos > 0) {
                        mLv.smoothScrollToPosition(pos);

                    } else
                        mLv.smoothScrollToPosition(0);
                }

            }
        }, 100L);

    }


    /**
     * interface OnClickListener
     * <p>Cette interface permet d'envoyer l'évenement OnClick d'un Button vers la classe activité qui a lancé le fragment</p>
     */
    public interface Frag_Chrono_Liste_Callback {
        /**
         * Evenement OnItemClickListener
         *
         * @param parent   ListView contenant l'item cliqué
         * @param view     View sur laquelle l'utilisateur a cliqué
         * @param position position de la View dans la ListView
         * @param id
         */
        public void onItemClickListener(AdapterView<?> parent, View view, int position, long id);

        /**
         * Evenement OnItemLongClickListener
         *
         * @param parent   ListView contenant l'item cliqué
         * @param view     View sur laquelle l'utilisateur a cliqué
         * @param position position de la View dans la ListView
         * @param id
         */
        public boolean onItemLongClickListener(AdapterView<?> parent, View view, int position, long id);
    }
}
