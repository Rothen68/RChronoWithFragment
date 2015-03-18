package com.stephane.rothen.rchrono.controller;

import android.content.Context;

import com.stephane.rothen.rchrono.model.ChronoModel;
import com.stephane.rothen.rchrono.model.DAOBase;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Exercice;
import com.stephane.rothen.rchrono.model.Sequence;

import java.util.ArrayList;

/**
 * Classe controleur gérant le chronometre
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class Chronometre {

    /**
     * Constantes définissant le type d'affichage du chronomètre
     */
    public static final int AFFICHAGE_TEMPS_EX = 1;
    public static final int AFFICHAGE_TEMPS_SEQ = 2;
    public static final int AFFICHAGE_TEMPS_TOTAL = 3;


    /**
     * Index de la séquence active
     */
    protected int m_indexSequenceActive;

    /**
     * Nombre de répétitions restantes pour la séquence active
     */
    protected int m_nbreRepetition;

    /**
     * Index de l'exercice dans la séquence active
     *
     * @see Chronometre#m_indexSequenceActive
     */
    protected int m_indexExerciceActif;

    /**
     * Position dans l'exercice actif
     */
    protected int m_positionDansExerciceActif;
    /**
     * Index du morceau actif dans la playlist de l'exercice
     *
     * @see com.stephane.rothen.rchrono.model.Playlist
     */
    protected int m_indexMorceauActif;
    /**
     * Position dans le morceau actif
     *
     * @see Chronometre#m_indexMorceauActif
     */
    protected int m_positionDansMorceauActif;
    /**
     * Type d'affichage du chronomètre
     *
     * @see #AFFICHAGE_TEMPS_EX
     */
    protected int m_typeAffichage;

    /**
     * Objet global model
     *
     * @see com.stephane.rothen.rchrono.model.ChronoModel
     */
    protected ChronoModel m_chronoModel;
    /**
     * durée restante dans la séquence active
     */
    protected int m_dureeRestanteSequenceActive;
    /**
     * durée restante totale
     */
    protected int m_dureeRestanteTotale;


    /**
     * Constructeur
     *
     * @param c Context de l'application, pour l'acces à la base de donnée
     * @see DAOBase
     */
    public Chronometre(Context c) {

        m_chronoModel = new ChronoModel(c);
        m_chronoModel.restore();

        m_indexExerciceActif = 0;
        m_indexSequenceActive = 0;
        m_nbreRepetition = m_chronoModel.getListeSequences().get(m_indexSequenceActive).getNombreRepetition();
        m_positionDansExerciceActif = m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().get(m_indexExerciceActif).getDureeExercice();
        m_typeAffichage = AFFICHAGE_TEMPS_EX;


    }

    public int getIndexSequenceActive() {
        return m_indexSequenceActive;
    }

    public int getIndexExerciceActif() {
        return m_indexExerciceActif;
    }

    /**
     * Supprime l'exercice actif
     */
    public void supprimeExerciceActif() {
        m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().remove(m_indexExerciceActif);
        if (m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().size() == 0)
            supprimeSequenceActive();
    }

    /**
     * Supprime la sequence active
     */
    public void supprimeSequenceActive() {
        m_chronoModel.getListeSequences().remove(m_indexSequenceActive);
    }


    /**
     * Met à jour les curseurs et renvois true si la liste des séquences n'est pas finie
     * <p>m_indexExerciceActif, m_indexSequenceActive, m_nbreRepetition</p>
     *
     * @return true : valeurs mises à jour
     * false : valeurs mises à 0, fin de la liste des séquences
     * @see Chronometre#m_indexExerciceActif
     * @see Chronometre#m_indexSequenceActive
     * @see Chronometre#m_nbreRepetition
     * @see com.stephane.rothen.rchrono.controller.Chronometre#m_chronoModel
     */
    public boolean next() {
        m_indexExerciceActif++;
        if (m_indexExerciceActif >= m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().size()) {
            m_indexExerciceActif = 0;
            m_nbreRepetition--;
            if (m_nbreRepetition <= 0) {
                m_indexSequenceActive++;
                if (m_indexSequenceActive >= m_chronoModel.getListeSequences().size()) {
                    m_indexExerciceActif = 0;
                    m_indexSequenceActive = 0;
                    m_positionDansExerciceActif = m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().get(m_indexExerciceActif).getDureeExercice();
                    return false;
                }
                m_nbreRepetition = m_chronoModel.getListeSequences().get(m_indexSequenceActive).getNombreRepetition();
            }

        }
        m_positionDansExerciceActif = m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().get(m_indexExerciceActif).getDureeExercice();
        return true;
    }


    /**
     * Réinitialise le chronometre
     *
     * @see Chronometre#m_indexExerciceActif
     * @see Chronometre#m_indexSequenceActive
     * @see Chronometre#m_nbreRepetition
     */
    public void resetChrono() {
        if (m_chronoModel.getListeSequences().size() > 0) {
            m_indexSequenceActive = 0;
            m_nbreRepetition = m_chronoModel.getListeSequences().get(0).getNombreRepetition();
            if (m_chronoModel.getListeSequences().get(0).getTabElement().size() > 0) {
                m_indexExerciceActif = 0;
            }
        } else {
            m_indexSequenceActive = -1;
            m_indexExerciceActif = -1;
            m_nbreRepetition = -1;
        }
        if (m_indexExerciceActif >= 0)
            m_positionDansExerciceActif = m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().get(m_indexExerciceActif).getDureeExercice();
        else
            m_positionDansExerciceActif = -1;

    }

    /**
     * Permet de positionner les curseurs du chronometre à une position définie
     *
     * @param indexSequence index de la séquence active
     * @param indexExercice index de l'exercice actif
     */
    public void setChronoAt(int indexSequence, int indexExercice) {
        if (indexSequence < m_chronoModel.getListeSequences().size() && indexSequence >= 0) {
            if (indexExercice < m_chronoModel.getListeSequences().get(indexSequence).getTabElement().size() && indexExercice >= 0) {
                m_indexSequenceActive = indexSequence;
                m_indexExerciceActif = indexExercice;
            }
        }
    }

    /**
     * Permet de positionner les curseurs du chronometre d'après la position d'un item cliqué dans la ListView
     *
     * @param positionDansListView position de l'item sur lequel l'utilisateur a clicker
     * @return position de l'item sur lequel affecter le focus ou -1 si erreur
     */

    public int setChronoAt(int positionDansListView) {
        int curseur = -1;
        m_indexExerciceActif = -1;
        m_indexSequenceActive = -1;
        m_nbreRepetition = -1;
        Sequence seq;
        ElementSequence el;

        //parcourt le tableau de liste des séquences
        for (int s = 0; s < m_chronoModel.getListeSequences().size(); s++) {
            curseur++;
            seq = m_chronoModel.getListeSequences().get(s);
            //si le curseur correspont à une séquence, affecte le curseur
            if (curseur == positionDansListView) {
                m_indexSequenceActive = s;
                m_nbreRepetition = seq.getNombreRepetition();
                if (seq.getTabElement().size() > 0) {
                    m_indexExerciceActif = 0;
                    m_positionDansExerciceActif = seq.getTabElement().get(m_indexExerciceActif).getDureeExercice();
                    m_dureeRestanteSequenceActive = getDureeRestanteSequenceActive();
                    m_dureeRestanteTotale = getDureeRestanteTotale();
                    curseur++;
                    return curseur;
                }
                return -1;

            }
            //sinon parcourt la liste des exercices de la séquence en cours
            else {

                for (int e = 0; e < seq.getTabElement().size(); e++) {
                    el = seq.getTabElement().get(e);
                    curseur++;
                    if (curseur == positionDansListView) {
                        m_indexSequenceActive = s;
                        m_nbreRepetition = seq.getNombreRepetition();
                        m_indexExerciceActif = e;
                        m_positionDansExerciceActif = el.getDureeExercice();
                        m_dureeRestanteSequenceActive = getDureeRestanteSequenceActive();
                        m_dureeRestanteTotale = getDureeRestanteTotale();
                        return curseur;
                    }
                }
            }
        }
        return -1;


    }

    /**
     * Renvois la durée de l'exercice actif
     *
     * @return durée de l'exercice actif
     * @see Chronometre#m_indexExerciceActif
     */
    public int getDureeExerciceActif() {
        return m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().get(m_indexExerciceActif).getDureeExercice();
    }

    /**
     * retourne le temps restant de l'exercice actif
     *
     * @return temps restant
     * @see Chronometre#m_indexExerciceActif
     */
    public int getDureeRestanteExerciceActif() {
        if (m_chronoModel.getListeSequences().size() > 0)
            return m_positionDansExerciceActif;
        else
            return 0;
    }

    /**
     * permet de définir le temps restant dans l'exercice actif
     *
     * @param position temps restant
     * @see Chronometre#m_indexExerciceActif
     */
    public void setDureeRestanteExerciceActif(int position) {
        m_positionDansExerciceActif = position;
    }

    /**
     * Renvois la liste des séquences
     *
     * @return liste des séquences
     * @see Chronometre#m_chronoModel
     */
    public ArrayList<Sequence> getListeSequence() {
        return m_chronoModel.getListeSequences();
    }

    public ArrayList<Sequence> getLibSequence() {
        return m_chronoModel.getLibrairieSequences();
    }

    public ArrayList<Exercice> getLibExercice() {
        return m_chronoModel.getLibrairieExercices();
    }


    public int getNbreRepetition() {
        return m_nbreRepetition;
    }

    /**
     * Renvois la durée restante de la séquence active
     *
     * @return durée restante
     * @see Chronometre#m_indexSequenceActive
     */
    public int getDureeRestanteSequenceActive() {
        if (m_chronoModel.getListeSequences().size() > 0) {
            Sequence s = m_chronoModel.getListeSequences().get(m_indexSequenceActive);
            int duree = 0;
            for (ElementSequence e : s.getTabElement()) {
                duree = duree + e.getDureeExercice();
            }
            duree = duree * (m_nbreRepetition - 1);
            for (int i = m_indexExerciceActif + 1; i < s.getTabElement().size(); i++)
                duree = duree + s.getTabElement().get(i).getDureeExercice();

            duree += getDureeRestanteExerciceActif();
            return duree;
        } else
            return 0;
    }

    /**
     * renvois la valeur de m_typeAffichage
     *
     * @return
     * @see #m_typeAffichage
     */
    public int getTypeAffichage() {
        return m_typeAffichage;
    }

    /**
     * met à jour la valeur de TypeAffichage
     *
     * @param type
     * @see #AFFICHAGE_TEMPS_EX
     * @see #m_typeAffichage
     */
    public void setTypeAffichage(int type) {
        if (type > 0 && type <= 3) {
            m_typeAffichage = type;
        }
    }

    /**
     * Calcule et renvois la durée totale restante
     *
     * @return durée totale restante
     */
    public int getDureeRestanteTotale() {
        int duree = getDureeRestanteSequenceActive();
        for (int i = m_indexSequenceActive + 1; i < m_chronoModel.getListeSequences().size(); i++)
            duree += m_chronoModel.getListeSequences().get(i).getDureeSequence();
        return duree;

    }

    public void setDureeRestanteTotale(int duree) {
        m_dureeRestanteTotale = duree;
    }

    /**
     * retourne la durée totale de la Liste des séquences
     *
     * @return duree totale
     */
    public int getDureeTotale() {
        int duree = 0;
        for (Sequence s : m_chronoModel.getListeSequences()) {
            duree += s.getDureeSequence();
        }
        return duree;
    }

    /**
     * retourn la durée totale de la liste des séquences sans la séquence active
     *
     * @return duree totale sans la séquence active
     */
    public int getDureeTotaleSansSeqActive() {
        int duree = getDureeTotale();
        duree -= m_chronoModel.getListeSequences().get(m_indexSequenceActive).getDureeSequence();
        return duree;
    }

    /**
     * Gere le tick du chronometre
     *
     * @return false si fin de l'exercice
     */
    public boolean tick() {
        m_positionDansExerciceActif--;
        if (m_positionDansExerciceActif <= 0) {
            next();
            return false;

        }
        return true;
    }

    /**
     * Ajoute la séquence
     *
     * @param s séquence à ajouter
     */
    public void ajouterSequenceDansListe(Sequence s) {
        m_chronoModel.ajouterSequenceDansListe(s);
    }

    /**
     * Remplace la séquence active par la séquence passée en parametre
     *
     * @param s séquence de remplacement
     */
    public void remplacerSequenceActive(Sequence s) {
        m_chronoModel.modifierSequenceDansListe(m_indexSequenceActive, s);
    }

    /**
     * Renvois l'ElementSequence actif
     *
     * @return ElementSequence actif
     */
    public ElementSequence getElementSequenceActif() {
        return m_chronoModel.getListeSequences().get(m_indexSequenceActive).getTabElement().get(m_indexExerciceActif);
    }

    /**
     * Renvois la Sequence active
     *
     * @return Sequence active
     */
    public Sequence getSequenceActive() {
        return m_chronoModel.getListeSequences().get(m_indexSequenceActive);
    }

}
