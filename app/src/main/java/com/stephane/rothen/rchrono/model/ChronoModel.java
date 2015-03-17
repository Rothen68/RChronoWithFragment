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
    protected DAOBase m_bddHelper;

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
    protected ArrayList<Sequence> mListeSequences;


    public ChronoModel(Context context) {
        m_bddHelper = new DAOBase(context);
    }

    /**
     * Restore les classes modèle depuis la base de donnée
     *
     * @return état de la restoration : true si réussie
     * @see DAOBase
     */
    public boolean restore() {
        mListeSequences = new ArrayList<>();
        mLibSequences = new ArrayList<>();
        mLibExercices = new ArrayList<>();
        ElementSequence e = new ElementSequence("Exercice 1", "", 10, new Playlist(), 10, new Playlist(), new NotificationExercice(0x01, 0), new SyntheseVocale(0));
        ElementSequence e2 = new ElementSequence("Exercice 2", "", 60, new Playlist(), 5, new Playlist(), new NotificationExercice(0x01, 0), new SyntheseVocale(0));
        ElementSequence e3 = new ElementSequence("Exercice 3", "", 30, new Playlist(), 5, new Playlist(), new NotificationExercice(0, 0), new SyntheseVocale(0x01));
        ElementSequence e4 = new ElementSequence("Exercice 4", "", 30, new Playlist(), 2, new Playlist(), new NotificationExercice(0x00, 0), new SyntheseVocale(0));
        ElementSequence e5 = new ElementSequence("Exercice 5", "", 30, new Playlist(), 2, new Playlist(), new NotificationExercice(0x00, 0), new SyntheseVocale(0));
        Sequence s = new Sequence("Sequence 1", 2, new SyntheseVocale(0x03));
        s.ajouterElement(e);
        s.ajouterElement(e2);
        Sequence s2 = new Sequence("Sequence 2", 1, new SyntheseVocale(0x03));
        s2.ajouterElement(e3);
        Sequence s3 = new Sequence("Sequence 3", 100, new SyntheseVocale(0x03));
        s3.ajouterElement(e4);
        s3.ajouterElement(e5);
        mListeSequences.add(s);
        mListeSequences.add(s2);
        mListeSequences.add(s3);

        mLibSequences.add(s);
        mLibSequences.add(s2);
        mLibSequences.add(s3);

        mLibExercices.add(e.getExercice());
        mLibExercices.add(e2.getExercice());
        mLibExercices.add(e3.getExercice());
        mLibExercices.add(e4.getExercice());
        mLibExercices.add(e5.getExercice());
        return true;
    }


    /**
     * Sauvegarde les classes modèle dans la base de donnée
     *
     * @return état de la sauvegarde : true si réussie
     */
    public boolean save() {
        return true;
    }

    /**
     * Ajoute une séquence dans la liste des séquences et dans la librairie des séquences
     *
     * @param s Séquence à ajouter
     */
    public void ajouterSequenceDansListe(Sequence s) {
        if (mLibSequences.indexOf(s) < 0)
            mLibSequences.add(s);
        mListeSequences.add(s);
    }

    /**
     * Remplace la séquence dans la liste des séquences et dans la librairie
     *
     * @param indexListeSequence index de la séquence dans la liste des séquences
     * @param s                  Nouvelle séquence
     */
    public void remplacerSequenceDansListe(int indexListeSequence, Sequence s) {
        int i = mLibSequences.indexOf(mListeSequences.get(indexListeSequence));
        mListeSequences.set(indexListeSequence, s);
        mLibSequences.set(i, s);
    }


    public ArrayList<Sequence> getLibrairieSequences() {
        return mLibSequences;
    }

    public ArrayList<Exercice> getLibrairieExercices() {
        return mLibExercices;
    }

    public ArrayList<Sequence> getListeSequences() {
        return mListeSequences;
    }
}
