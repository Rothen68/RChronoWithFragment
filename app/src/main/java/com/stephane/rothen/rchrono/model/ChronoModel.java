package com.stephane.rothen.rchrono.model;

import android.content.Context;

import com.stephane.rothen.rchrono.Fonctions;

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
    protected ArrayList<Sequence> mListeSequences;


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
        mListeSequences = new ArrayList<>();
        mLibSequences = new ArrayList<>();
        mLibExercices = new ArrayList<>();
        ElementSequence e = new ElementSequence("Exercice 1", "", 10, new Playlist(), 10, new Playlist(), new NotificationExercice(0x01, new Morceau(0, "ding", "")), new SyntheseVocale(0));
        ElementSequence e2 = new ElementSequence("Exercice 2", "", 60, new Playlist(), 5, new Playlist(), new NotificationExercice(0x01, new Morceau(0, "ding", "")), new SyntheseVocale(0));
        ElementSequence e3 = new ElementSequence("Exercice 3", "", 30, new Playlist(), 5, new Playlist(), new NotificationExercice(0, new Morceau(0, "ding", "")), new SyntheseVocale(0x01));
        ElementSequence e4 = new ElementSequence("Exercice 4", "", 30, new Playlist(), 2, new Playlist(), new NotificationExercice(0x00, new Morceau(0, "ding", "")), new SyntheseVocale(0));
        ElementSequence e5 = new ElementSequence("Exercice 5", "", 30, new Playlist(), 2, new Playlist(), new NotificationExercice(0x00, new Morceau(0, "ding", "")), new SyntheseVocale(0));
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
     * Modifie la séquence dans la liste des séquences et dans la librairie
     * Si une séquence est unique dans la liste des séquences alors elle est remplacée
     * Si une séquence est présente plusieurs fois dans la liste des séquences alors elle est dupliquée dans la liste des séquences et dans la librairie
     *
     * @param indexListeSequence index de la séquence dans la liste des séquences
     * @param s                  Nouvelle séquence
     */
    public void modifierSequenceDansListe(int indexListeSequence, Sequence s) {
        Sequence ancienneSeq = mListeSequences.get(indexListeSequence);
        int nbreOccurences = 0;
        int[] tabIndexOccurences = new int[mListeSequences.size()];

        // recherchce des occurences de la séquence à modifier dans la liste des séquences
        for (int i = 0; i < mListeSequences.size(); i++) {
            if (ancienneSeq.equals(mListeSequences.get(i))) {

                tabIndexOccurences = Fonctions.ajouterDansTabInt(tabIndexOccurences, nbreOccurences, i);
                nbreOccurences++;
            }
        }
        if (nbreOccurences > 1) {
            // si plusieurs occurences dans listeSequence, ajout d'une nouvelle séquence dans la librairie et modification de la séquence modifiée
            mLibSequences.add(s);
            mListeSequences.set(indexListeSequence, s);
        } else {
            int i = mLibSequences.indexOf(mListeSequences.get(indexListeSequence));
            mListeSequences.set(indexListeSequence, s);
            mLibSequences.set(i, s);
        }
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

    /**
     * gere la mise a jour et l'enregistrerment d'un ElementSequence
     *
     * @param m_indexSequenceActive index de la séquence de l'elementSequence
     * @param m_indexExerciceActif  index de l'elementSequence
     * @param el                    ElementSequence à enregistrer
     */

    public void remplacerElementSequenceActif(int m_indexSequenceActive, int m_indexExerciceActif, ElementSequence el) {
        mListeSequences.get(m_indexSequenceActive).getTabElement().set(m_indexExerciceActif, el);
        Exercice e = el.getExercice();
        Boolean dejaEnregistrer = false;
        for (Exercice exercice : mLibExercices) {
            if (exercice.equals(e)) {
                dejaEnregistrer = true;
                break;
            }
        }
        if (!dejaEnregistrer) {
            mLibExercices.add(e);
        }

    }

    /**
     * Créer un ElementSequence et l'ajoute dans la liste des ElementSequence de la séquence dont l'index dans listeSequence est passé en parametre
     *
     * @param indexSequence index de la séquence
     * @return index de l'ElementSequence ajouté dans la séquence
     */

    public int creerElementSequence(int indexSequence) {
        mListeSequences.get(indexSequence).getTabElement().add(new ElementSequence("", "", 0, new Playlist(), 0, new Playlist(), new NotificationExercice(0, null), new SyntheseVocale(0)));
        return mListeSequences.get(indexSequence).getTabElement().size() - 1;
    }
}
