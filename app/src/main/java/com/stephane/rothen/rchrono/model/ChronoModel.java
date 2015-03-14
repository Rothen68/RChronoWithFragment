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
    protected ArrayList<Exercice> m_libExercices;
    /**
     * Instance de l'objet contenant la librairie des séquences présent dans la base de données du téléphone
     */
    protected ArrayList<Sequence> m_libSequences;
    /**
     * Instance de l'objet contenant la liste des séquences à effectuer
     */
    protected ArrayList<Sequence> m_listeSequences;


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
        m_listeSequences = new ArrayList<>();
        ElementSequence e = new ElementSequence("Exercice 1", "", 10, null, 10, null, new NotificationExercice(0x01, 0), new SyntheseVocale(0));
        ElementSequence e2 = new ElementSequence("Exercice 2", "", 60, null, 5, null, new NotificationExercice(0x01, 0), new SyntheseVocale(0));
        ElementSequence e3 = new ElementSequence("Exercice 3", "", 30, null, 5, null, new NotificationExercice(0, 0), new SyntheseVocale(0x01));
        ElementSequence e4 = new ElementSequence("Exercice 4", "", 30, null, 2, null, new NotificationExercice(0x00, 0), new SyntheseVocale(0));
        ElementSequence e5 = new ElementSequence("Exercice 5", "", 30, null, 2, null, new NotificationExercice(0x00, 0), new SyntheseVocale(0));
        Sequence s = new Sequence("Sequence 1", 2, new SyntheseVocale(0x03));
        s.ajouterElement(e);
        s.ajouterElement(e2);
        Sequence s2 = new Sequence("Sequence 2", 1, new SyntheseVocale(0x03));
        s2.ajouterElement(e3);
        Sequence s3 = new Sequence("Sequence 3", 100, new SyntheseVocale(0x03));
        s3.ajouterElement(e4);
        s3.ajouterElement(e5);
        m_listeSequences.add(s);
        m_listeSequences.add(s2);
        m_listeSequences.add(s3);
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


    public ArrayList<Sequence> getLibrairieSequences() {
        return m_libSequences;
    }

    public ArrayList<Exercice> getLibrairieExercices() {
        return m_libExercices;
    }

    public ArrayList<Sequence> getListeSequences() {
        return m_listeSequences;
    }
}
