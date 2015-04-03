package com.stephane.rothen.rchrono.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by stéphane on 12/03/2015.
 */
public class ChronoModel {

    /**
     * Gestionnaire de l'acces à la base de donnée
     *
     * @see com.stephane.rothen.rchrono.model.DAOBase
     */
    protected DAOBase mBddHelper;

    /**
     * Instance de l'objet contenant la librairie des exercices présent dans la base de données du téléphone
     */
    protected ArrayList<Exercice> mLibExercices;
    /**
     * Instance de l'objet contenant la librairie des séquences présent dans la base de données du téléphone
     */
    protected ArrayList<Sequence> mLibSequences;
    /**
     * Instance de l'objet contenant la liste des séquences à effectuer
     */
    protected ArrayList<Integer> mListeSequences;


    public ChronoModel(Context context) {
        mBddHelper = new DAOBase(context);
    }

    /**
     * Restore les classes modèle depuis la base de donnée
     *
     * @return état de la restoration : true si réussie
     * @see DAOBase
     */
    public boolean restore() {
        mBddHelper.open();

        mLibExercices = mBddHelper.restoreLibrairieExercice();
        mLibSequences = mBddHelper.restoreLibrairieSequences(mLibExercices);
        mListeSequences = mBddHelper.restoreListeSequences();
        mBddHelper.close();
        return true;
    }


    /**
     * Sauvegarde les classes modèle dans la base de donnée
     *
     * @return état de la sauvegarde : true si réussie
     */
    public boolean save() {
        mBddHelper.open();
        mBddHelper.clearTables();
        mBddHelper.saveLibrairieExercice(mLibExercices);
        mBddHelper.saveLibrairieSequences(mLibSequences);
        mBddHelper.saveLstSequence(mListeSequences);
        mBddHelper.close();
        return true;
    }

    /**
     * Ajoute une séquence dans la liste des séquences et dans la librairie des séquences
     *
     * @param s Séquence à ajouter
     */
    public void ajouterSequenceDansListe(Sequence s) {
        if (mLibSequences.indexOf(s) < 0) {
            s.setIdSequence(mLibSequences.size() + 1);
            mLibSequences.add(s);
        }

        mListeSequences.add(s.getIdSequence());
        for (ElementSequence el : s.getTabElement()) {
            remplacerElementSequence(el);
        }
    }

    public Sequence getSeqFromLstSequenceAt(int i) {
        int indexSequence = mListeSequences.get(i);
        for (Sequence s : mLibSequences) {
            if (s.getIdSequence() == indexSequence)
                return s;
        }
        return null;
    }



    /**
     * Modifie la séquence dans la liste des séquences et dans la librairie
     * Si une séquence est unique dans la liste des séquences alors elle est remplacée
     * Si une séquence est présente plusieurs fois dans la liste des séquences alors elle est dupliquée dans la liste des séquences et dans la librairie
     *
     * @param indexListeSequence index de la séquence dans la liste des séquences
     * @param s                  Nouvelle séquence
     */
    public void modifierSequenceDansListe(int indexListeSequence, Sequence s) {
        s.setIdSequence(mListeSequences.get(indexListeSequence));
        Sequence ancienneSeq = getSeqFromLstSequenceAt(indexListeSequence);
        int nbreOccurences = 0;

        // recherche des occurences de la séquence à modifier dans la liste des séquences
        for (int i = 0; i < mListeSequences.size(); i++) {
            if (ancienneSeq.getIdSequence() == s.getIdSequence()) {
                nbreOccurences++;
            }
        }
        if (nbreOccurences > 1) {
            // si plusieurs occurences dans listeSequence, ajout d'une nouvelle séquence dans la librairie et modification de la séquence modifiée
            s.setIdSequence(mLibSequences.size() + 1);
            mLibSequences.add(s);
            mListeSequences.set(indexListeSequence, s.getIdSequence());
        } else {
            int i = mListeSequences.get(indexListeSequence);
            mLibSequences.set(s.getIdSequence() - 1, s);
        }
        for (ElementSequence el : s.getTabElement()) {
            remplacerElementSequence(el);
        }
    }


    public ArrayList<Sequence> getLibrairieSequences() {
        return mLibSequences;
    }

    public ArrayList<Exercice> getLibrairieExercices() {
        return mLibExercices;
    }

    public ArrayList<Integer> getListeSequences() {
        return mListeSequences;
    }

    /**
     * gere la mise a jour et l'enregistrerment d'un ElementSequence
     *
     * @param el ElementSequence à enregistrer
     */

    public void remplacerElementSequence(ElementSequence el) {
        Exercice e = el.getExercice();
        Boolean dejaEnregistrer = false;
        Exercice exercice;
        for (int i = 0; i < mLibExercices.size(); i++) {
            exercice = mLibExercices.get(i);
            if (exercice.getIdExercice() == e.getIdExercice()) {
                dejaEnregistrer = true;
                mLibExercices.set(i, e);
                break;
            }
        }
        if (!dejaEnregistrer) {
            mLibExercices.add(e);
        }

    }

}
