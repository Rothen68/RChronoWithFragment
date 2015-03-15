package com.stephane.rothen.rchrono.model;

import java.util.ArrayList;

/**
 * Classe métier permettant de stocker les donnees d'une séquence
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class Sequence {
    /**
     * Nom de la séquence
     */
    protected String m_nomSequence;

    /**
     * Nombre de répétitions de la séquence :  1 = la séquence est lu une fois
     */
    protected int m_nombreRepetition;

    /**
     * Synthèse vocale de la séquence
     *
     * @see SyntheseVocale
     */
    protected SyntheseVocale m_syntheseVocale;

    /**
     * tableau contenant les exercices à executer durant la séquence
     *
     * @see ElementSequence
     */
    protected ArrayList<ElementSequence> m_tabElement;

    /**
     * Constructeur
     *
     * @param nomSequence
     * @param nombreRepetition
     * @param syntheseVocale
     */
    public Sequence(String nomSequence, int nombreRepetition, SyntheseVocale syntheseVocale) {
        this.m_nomSequence = nomSequence;
        this.m_nombreRepetition = nombreRepetition;
        this.m_syntheseVocale = syntheseVocale;
        m_tabElement = new ArrayList<>();
    }


    public String getNomSequence() {
        return m_nomSequence;
    }

    public void setNomSequence(String nomSequence) {
        this.m_nomSequence = nomSequence;
    }

    public int getNombreRepetition() {
        return m_nombreRepetition;
    }

    public void setM_nombreRepetition(int nombreRepetition) {
        this.m_nombreRepetition = nombreRepetition;
    }

    public SyntheseVocale getSyntheseVocale() {
        return m_syntheseVocale;
    }

    public void setM_syntheseVocale(SyntheseVocale syntheseVocale) {
        this.m_syntheseVocale = syntheseVocale;
    }

    public ArrayList<ElementSequence> getTabElement() {
        return m_tabElement;
    }

    public void setTabElement(ArrayList<ElementSequence> tabElement) {
        this.m_tabElement = tabElement;
    }

    /**
     * Ajoute un ElementSequence à la séquence
     *
     * @param e ElementSequence à ajouter
     * @see ElementSequence
     * @see Sequence#m_tabElement
     */
    public void ajouterElement(ElementSequence e) {
        m_tabElement.add(e);
    }


    /**
     * Fonction qui retourne la durée d'une séquence incluant les répétitions
     *
     * @return durée de la séquence
     */
    public int getDureeSequence() {
        int duree = 0;
        for (ElementSequence e : m_tabElement) {
            duree = duree + e.getDureeExercice();
        }
        duree = duree * m_nombreRepetition;
        return duree;
    }

    public Sequence getClone() {
        Sequence s = new Sequence(m_nomSequence, m_nombreRepetition, m_syntheseVocale.getClone());
        for (ElementSequence e : m_tabElement) {
            s.ajouterElement(e.getClone());
        }
        return s;
    }

    public boolean egale(Sequence s) {
        if (m_nomSequence.equals(s.m_nomSequence) && m_nombreRepetition == s.m_nombreRepetition && m_syntheseVocale.egale(s.m_syntheseVocale) && m_tabElement.equals(s.m_tabElement))
            return true;
        else
            return false;
    }
}
