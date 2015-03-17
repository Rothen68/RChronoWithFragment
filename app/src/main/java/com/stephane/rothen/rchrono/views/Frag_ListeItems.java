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
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Exercice;
import com.stephane.rothen.rchrono.model.Sequence;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Fragment stockant la listView de la fenetre RChrono
 * Created by stéphane on 13/03/2015.
 */
public class Frag_ListeItems extends Fragment {

    public static final String FRAG_CHRONO_LISTE = "FRAG_CHRONO_LISTE";

    public static final int AFFICHE_LISTVIEW = 1;
    public static final int AFFICHE_LIBEXERCICE = 2;
    public static final int AFFICHE_LIBSEQUENCE = 3;
    public static final int AFFICHE_EXERCICESEQACTIVE = 4;
    private static final int NBRE_TYPE_AFFICHAGE = 4;


    /**
     * Instance de l'interface OnClickListener
     */
    private Frag_Liste_Callback mCallback;
    /**
     * Objet de l'interface, ListView qui contient la liste des séquences et des exercices
     */
    private ListView mLv;
    /**
     * Objet permettant de remplir la ListView
     *
     * @see CustomAdapter
     */
    private CustomAdapter mAdapter;

    private int mTypeAffichage = 1;

    /**
     * Gère l'état du mode suppression, actif ou non
     */
    private boolean mModeSuppression = false;


    public Frag_ListeItems() {
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
            mCallback = (Frag_Liste_Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implements Frag_Liste_Callback");
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
        View rootView = inflater.inflate(R.layout.frag_liste, container, false);
        mLv = (ListView) rootView.findViewById(R.id.Frag_Liste_listView);
        mAdapter = new CustomAdapter(getActivity().getApplicationContext());
        mAdapter.setCallback(mCallback);
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

    public boolean getAfficheBtnSuppr() {
        return mModeSuppression;
    }

    /**
     * Affiche ou non le bouton Supprimer sur les éléments de la listView
     *
     * @param etat Etat de l'affichage du bouton
     */
    public void setAfficheBtnSuppr(boolean etat) {
        if (mAdapter != null) {
            mAdapter.setAfficheBtnSuppr(etat);
            if (etat)
                mAdapter.setCallback(mCallback);
            else
                mAdapter.setCallback(null);
            mModeSuppression = etat;
        }
    }

    public void setTypeAffichage(int t) {
        if (t > 0 && t <= NBRE_TYPE_AFFICHAGE)
            mTypeAffichage = t;
    }

    public CustomAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Affiche ou non le curseur sur l'exercice actif
     *
     * @param etat Etat de l'affichage du curseur
     */
    public void setAfficheCurseur(boolean etat) {
        if (mAdapter != null)
            mAdapter.setAfficheCurseur(etat);
    }


    /**
     * Initialise le ListView Adapter mAdapter et l'affecte à la ListView mLv
     *
     * @param positionFocus Permet de définir quel item de la ListView est mis en surbrillance
     */
    public void afficheListView(int positionFocus, AtomicReference<Chronometre> mChrono) {
        if (mAdapter.getCount() > 0) {
            mAdapter.deleteAll();
        }
        switch (mTypeAffichage) {
            case AFFICHE_LISTVIEW:
                affiche_ListView(positionFocus, mChrono);
                break;
            case AFFICHE_LIBSEQUENCE:
                affiche_LibSequence(mChrono);
                break;
            case AFFICHE_LIBEXERCICE:
                affiche_LibExercice(mChrono);
                break;
            case AFFICHE_EXERCICESEQACTIVE:
                affiche_ExerciceSeqActive(mChrono);
            default:
                break;
        }

    }

    /**
     * Affiche la liste des exercices de la séquence active
     *
     * @param mChrono
     */
    private void affiche_ExerciceSeqActive(AtomicReference<Chronometre> mChrono) {
        mAdapter.setFocusPosition(0);
        Sequence s = mChrono.get().getSequenceActive();
        for (int i = 0; i < s.getTabElement().size(); i++) {
            ElementSequence e = s.getTabElement().get(i);
            mAdapter.addItem(e.getNomExercice() + " - " + Fonctions.convertSversHMSSansZeros(e.getDureeExercice()));
        }
        mAdapter.notifyDataSetChanged();
    }

    public void afficheListView(Sequence s) {
        mAdapter.setFocusPosition(0);
        mAdapter.deleteAll();
        for (int i = 0; i < s.getTabElement().size(); i++) {
            ElementSequence e = s.getTabElement().get(i);
            mAdapter.addItem(e.getNomExercice() + " - " + Fonctions.convertSversHMSSansZeros(e.getDureeExercice()));
        }
        mAdapter.notifyDataSetChanged();
    }


    private void affiche_LibSequence(AtomicReference<Chronometre> mChrono) {
        mAdapter.setFocusPosition(0);
        for (int i = 0; i < mChrono.get().getLibSequence().size(); i++) {
            Sequence s = mChrono.get().getLibSequence().get(i);
            mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + s.getNombreRepetition() + "x - " + Fonctions.convertSversHMSSansZeros(s.getDureeSequence()));

        }
        mAdapter.notifyDataSetChanged();

    }

    private void affiche_LibExercice(AtomicReference<Chronometre> mChrono) {
        mAdapter.setFocusPosition(0);
        for (int i = 0; i < mChrono.get().getLibSequence().size(); i++) {
            Exercice e = mChrono.get().getLibExercice().get(i);
            mAdapter.addSectionHeaderItem(e.getNomExercice() + " - " + Fonctions.convertSversHMSSansZeros(e.getDureeParDefaut()) + " s");

        }
        mAdapter.notifyDataSetChanged();

    }


    private void affiche_ListView(int positionFocus, AtomicReference<Chronometre> mChrono) {
        mAdapter.setFocusPosition(positionFocus);
        //Parcours la liste des séquences et des exercices pour chaque séquence et les ajoute dans mAdapter
        for (int i = 0; i < mChrono.get().getListeSequence().size(); i++) {
            Sequence s = mChrono.get().getListeSequence().get(i);
            //si s est la séquence active, afficher le nombre de répétitions restantes
            if (mChrono.get().getIndexSequenceActive() == i) {
                if (mChrono.get().getNbreRepetition() == 0) {
                    mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + Fonctions.convertSversHMSSansZeros(mChrono.get().getDureeRestanteSequenceActive()));
                } else {
                    mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + mChrono.get().getNbreRepetition() + "x" + " - " + Fonctions.convertSversHMSSansZeros(mChrono.get().getDureeRestanteSequenceActive()));
                }
            }
            //si s est avant la séquence active, donc est déjà passée, met 0 à la durée de la séquence et des exercices qui la composent
            else if (mChrono.get().getIndexSequenceActive() > i) {
                mAdapter.addSectionHeaderItem(s.getNomSequence());
            } else {
                mAdapter.addSectionHeaderItem(s.getNomSequence() + " - " + s.getNombreRepetition() + "x" + " - " + Fonctions.convertSversHMSSansZeros(s.getDureeSequence()));
            }
            for (int j = 0; j < s.getTabElement().size(); j++) {
                if (mChrono.get().getIndexSequenceActive() > i)
                    mAdapter.addItem(s.getTabElement().get(j).getNomExercice());
                else
                    mAdapter.addItem(s.getTabElement().get(j).getNomExercice() + " - " + Fonctions.convertSversHMSSansZeros(s.getTabElement().get(j).getDureeExercice()));
            }
        }
        mAdapter.notifyDataSetChanged();
        final int position = positionFocus;


        mLv.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mTypeAffichage == AFFICHE_LISTVIEW) {
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

            }
        }, 100L);
    }


}
