package com.stephane.rothen.rchrono.controller;

import android.content.Context;

import com.stephane.rothen.rchrono.model.ChronoModel;
import com.stephane.rothen.rchrono.model.DatabaseHelper;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.Exercice;
import com.stephane.rothen.rchrono.model.Morceau;
import com.stephane.rothen.rchrono.model.Sequence;

import java.util.ArrayList;

/**
 * Classe contrôleur gérant le chronometre
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
    protected int mIndexSequenceActive;

    /**
     * Nombre de répétitions restantes pour la séquence active
     */
    protected int mNbreRepetition;

    /**
     * Index de l'exercice dans la séquence active
     *
     * @see Chronometre#mIndexSequenceActive
     */
    protected int mIndexExerciceActif;

    /**
     * Position dans l'exercice actif
     */
    protected int mPositionDansExerciceActif;

    /**
     * Type d'affichage du chronomètre
     *
     * @see #AFFICHAGE_TEMPS_EX
     */
    protected int mTypeAffichage;

    /**
     * Objet global model
     *
     * @see com.stephane.rothen.rchrono.model.ChronoModel
     */
    protected ChronoModel mChronoModel;
    /**
     * durée restante dans la séquence active
     */
    protected int mDureeRestanteSequenceActive;
    /**
     * durée restante totale
     */
    protected int mDureeRestanteTotale;


    /**
     * ElementSequence temporaire utilisé pour garder les modifications lors du passage de la fenêtre editionexercice vers editionexerciceplaylist
     */
    protected ElementSequence mElementSeqTemp;
    /**
     * index de l'ElementSequenceTemp dans la sequence temporaire
     */
    protected int mIndexElementSeqTemp;

    /**
     * Sequence temporaire utilisée pour garder les modifications
     */
    protected Sequence mSeqTemp;


    /**
     * Constructeur
     *
     * @param c Context de l'application, pour l'acces à la base de donnée
     * @see DatabaseHelper
     */
    public Chronometre(Context c) {

        mChronoModel = new ChronoModel(c);
        mChronoModel.restore();
        if (mChronoModel.getListeSequences().size() > 0) {
            mIndexExerciceActif = 0;
            mIndexSequenceActive = 0;
            mNbreRepetition = mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getNombreRepetition();

            mPositionDansExerciceActif = mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getTabElement().get(mIndexExerciceActif).getDureeExercice();
            mTypeAffichage = AFFICHAGE_TEMPS_EX;
        } else {
            mIndexExerciceActif = -1;
            mIndexSequenceActive = -1;
            mNbreRepetition = -1;
            mTypeAffichage = AFFICHAGE_TEMPS_EX;
        }


    }

    /**
     * Retourne l'index de la séquence active
     *
     * @return index de la séquence active
     */
    public int getIndexSequenceActive() {
        return mIndexSequenceActive;
    }

    /**
     * Retourne l'index de l'ElementSequence actif
     *
     * @return index de l'ElementSequence actif dans la séquence active
     */
    public int getIndexExerciceActif() {
        return mIndexExerciceActif;
    }


    /**
     * Supprime la sequence active
     */
    public void supprimeSequenceActive() {
        mChronoModel.supprimerSequenceDansListe(mIndexSequenceActive);
    }


    /**
     * Met à jour les curseurs et renvois true si la liste des séquences n'est pas finie
     * <p>mIndexExerciceActif, mIndexSequenceActive, mNbreRepetition</p>
     *
     * @return true : valeurs mises à jour
     * false : valeurs mises à 0, fin de la liste des séquences
     * @see Chronometre#mIndexExerciceActif
     * @see Chronometre#mIndexSequenceActive
     * @see Chronometre#mNbreRepetition
     * @see com.stephane.rothen.rchrono.controller.Chronometre#mChronoModel
     */
    public boolean next() {
        mIndexExerciceActif++;
        if (mIndexExerciceActif >= mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getTabElement().size()) {
            mIndexExerciceActif = 0;
            mNbreRepetition--;
            if (mNbreRepetition <= 0) {
                mIndexSequenceActive++;
                if (mIndexSequenceActive >= mChronoModel.getListeSequences().size()) {
                    mIndexExerciceActif = 0;
                    mIndexSequenceActive = 0;
                    mPositionDansExerciceActif = mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getTabElement().get(mIndexExerciceActif).getDureeExercice();
                    return false;
                }
                mNbreRepetition = mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getNombreRepetition();
            }

        }
        mPositionDansExerciceActif = mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getTabElement().get(mIndexExerciceActif).getDureeExercice();
        return true;
    }


    /**
     * Réinitialise le chronometre
     *
     * @see Chronometre#mIndexExerciceActif
     * @see Chronometre#mIndexSequenceActive
     * @see Chronometre#mNbreRepetition
     */
    public void resetChrono() {
        if (mChronoModel.getListeSequences().size() > 0) {
            mIndexSequenceActive = 0;
            mNbreRepetition = mChronoModel.getSeqFromLstSequenceAt(0).getNombreRepetition();
            if (mChronoModel.getSeqFromLstSequenceAt(0).getTabElement().size() > 0) {
                mIndexExerciceActif = 0;
            }
            mChronoModel.resetPlaylists();
        } else {
            mIndexSequenceActive = -1;
            mIndexExerciceActif = -1;
            mNbreRepetition = -1;
        }
        if (mIndexExerciceActif >= 0)
            mPositionDansExerciceActif = mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getTabElement().get(mIndexExerciceActif).getDureeExercice();
        else
            mPositionDansExerciceActif = -1;

    }

    /**
     * Retourne la séquence de la librairie des séquences depuis l'index dans ListSequence passé en paramètre
     * @param index index de la séquence dans listSequence
     * @return Sequence
     */
    public Sequence getSeqFromLstSequenceAt(int index) {
        return mChronoModel.getSeqFromLstSequenceAt(index);
    }

    /**
     * Permet de positionner les curseurs du chronometre à une position définie
     *
     * @param indexSequence index de la séquence active
     * @param indexExercice index de l'exercice actif
     */
    public void setChronoAt(int indexSequence, int indexExercice) {
        if (indexSequence < mChronoModel.getListeSequences().size() && indexSequence >= 0) {
            if (indexExercice < mChronoModel.getSeqFromLstSequenceAt(indexSequence).getTabElement().size() && indexExercice >= 0) {
                mIndexSequenceActive = indexSequence;
                mIndexExerciceActif = indexExercice;
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
        mIndexExerciceActif = -1;
        mIndexSequenceActive = -1;
        mNbreRepetition = -1;
        Sequence seq;
        ElementSequence el;

        //parcourt le tableau de liste des séquences
        for (int s = 0; s < mChronoModel.getListeSequences().size(); s++) {
            curseur++;
            seq = mChronoModel.getSeqFromLstSequenceAt(s);
            //si le curseur correspond à une séquence, affecte le curseur
            if (curseur == positionDansListView) {
                mIndexSequenceActive = s;
                mNbreRepetition = seq.getNombreRepetition();
                if (seq.getTabElement().size() > 0) {
                    mIndexExerciceActif = 0;
                    mPositionDansExerciceActif = seq.getTabElement().get(mIndexExerciceActif).getDureeExercice();
                    mDureeRestanteSequenceActive = getDureeRestanteSequenceActive();
                    mDureeRestanteTotale = getDureeRestanteTotale();
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
                        mIndexSequenceActive = s;
                        mNbreRepetition = seq.getNombreRepetition();
                        mIndexExerciceActif = e;
                        mPositionDansExerciceActif = el.getDureeExercice();
                        mDureeRestanteSequenceActive = getDureeRestanteSequenceActive();
                        mDureeRestanteTotale = getDureeRestanteTotale();
                        return curseur;
                    }
                }
            }
        }
        return -1;


    }



    /**
     * retourne le temps restant de l'exercice actif
     *
     * @return temps restant
     * @see Chronometre#mIndexExerciceActif
     */
    public int getDureeRestanteExerciceActif() {
        if (mChronoModel.getListeSequences().size() > 0)
            return mPositionDansExerciceActif;
        else
            return 0;
    }



    /**
     * Renvois la liste des séquences
     *
     * @return liste des séquences
     * @see Chronometre#mChronoModel
     */
    public ArrayList<Long> getListeSequence() {
        return mChronoModel.getListeSequences();
    }

    /**
     * Renvois la librairie des séquences
     * @return librairie des séquence
     */
    public ArrayList<Sequence> getLibSequence() {
        return mChronoModel.getLibrairieSequences();
    }

    /**
     * Renvois la librairie des exercices
     * @return librairie des exercices
     */
    public ArrayList<Exercice> getLibExercice() {
        return mChronoModel.getLibrairieExercices();
    }

    /**
     * Retourne le nombre de répétitions restantes pour la séquence active
     * @return nombre de répétitions
     */
    public int getNbreRepetition() {
        return mNbreRepetition;
    }

    /**
     * Renvois la durée restante de la séquence active
     *
     * @return durée restante
     * @see Chronometre#mIndexSequenceActive
     */
    public int getDureeRestanteSequenceActive() {
        if (mChronoModel.getListeSequences().size() > 0) {
            Sequence s = mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive);
            int duree = 0;
            for (ElementSequence e : s.getTabElement()) {
                duree = duree + e.getDureeExercice();
            }
            duree = duree * (mNbreRepetition - 1);
            for (int i = mIndexExerciceActif + 1; i < s.getTabElement().size(); i++)
                duree = duree + s.getTabElement().get(i).getDureeExercice();

            duree += getDureeRestanteExerciceActif();
            return duree;
        } else
            return 0;
    }

    /**
     * renvois la valeur de mTypeAffichage
     *
     * @return type d'affichage
     * @see #mTypeAffichage
     */
    public int getTypeAffichage() {
        return mTypeAffichage;
    }

    /**
     * met à jour la valeur de TypeAffichage
     *
     * @param type type d'affichage
     * @see #AFFICHAGE_TEMPS_EX
     * @see #mTypeAffichage
     */
    public void setTypeAffichage(int type) {
        if (type > 0 && type <= 3) {
            mTypeAffichage = type;
        }
    }

    /**
     * Calcule et renvois la durée totale restante
     *
     * @return durée totale restante
     */
    public int getDureeRestanteTotale() {
        int duree = getDureeRestanteSequenceActive();
        for (int i = mIndexSequenceActive + 1; i < mChronoModel.getListeSequences().size(); i++)
            duree += mChronoModel.getSeqFromLstSequenceAt(i).getDureeSequence();
        return duree;

    }


    /**
     * retourne la durée totale de la Liste des séquences
     *
     * @return duree totale
     */
    public int getDureeTotale() {
        int duree = 0;
        Sequence s;
        for (int i = 0; i < mChronoModel.getListeSequences().size(); i++) {
            s = mChronoModel.getSeqFromLstSequenceAt(i);
            duree += s.getDureeSequence();
        }
        return duree;
    }

    /**
     * retourne la durée totale de la liste des séquences sans la séquence active
     *
     * @return duree totale sans la séquence active
     */
    public int getDureeTotaleSansSeqActive() {
        int duree = getDureeTotale();
        if (mIndexSequenceActive >= 0)
            duree -= mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getDureeSequence();
        return duree;
    }

    /**
     * Gère le tick du chronometre
     *
     * @return false si fin de l'exercice
     */
    public boolean tick() {
        mPositionDansExerciceActif--;
        if (mPositionDansExerciceActif <= 0) {
            next();
            return false;

        }
        return true;
    }

    /**
     * Retourne true si l'exercice va se terminer
     * @return true si fin de l'exercice actif
     */
    public boolean isNextComing() {
        if (mPositionDansExerciceActif <= 1)
            return true;
        else return false;
    }

    /**
     * Ajoute la séquence
     *
     * @param s séquence à ajouter
     */
    public void ajouterSequenceDansListe(Sequence s) {
        mChronoModel.ajouterSequenceDansListe(s);
    }

    /**
     * Remplace la séquence active par la séquence passée en paramètre
     *
     * @param s séquence de remplacement
     */
    public void remplacerSequenceActive(Sequence s) {
        mChronoModel.modifierSequenceDansListe(s);
    }


    /**
     * Renvois l'ElementSequence actif
     *
     * @return ElementSequence actif
     */
    public ElementSequence getElementSequenceActif() {
        return mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive).getTabElement().get(mIndexExerciceActif);
    }

    /**
     * Renvois la Sequence active
     *
     * @return Sequence active
     */
    public Sequence getSequenceActive() {
        if (mIndexSequenceActive >= 0)
            return mChronoModel.getSeqFromLstSequenceAt(mIndexSequenceActive);
        else
            return null;
    }


    /**
     * Renvois l'ElementSequence temporaire
     * @return ElementSequence
     */
    public ElementSequence getElementSeqTemp() {
        return mElementSeqTemp;
    }

    /**
     * Définit l'ElementSequence temporaire
     * @param el    ElementSequence
     */
    public void setElementSeqTemp(ElementSequence el) {
        mElementSeqTemp = el;
    }

    /**
     * Renvois la Sequence temporaire
     * @return Sequence
     */
    public Sequence getSeqTemp() {
        return mSeqTemp;
    }

    /**
     * Définit la Sequence temporaire
     * @param s Sequence
     */
    public void setSeqTemp(Sequence s) {
        mSeqTemp = s;
    }

    /**
     * Renvois l'index de l'ElementSequence temporaire
     * @return index
     */

    public int getIndexElementSeqTemp() {
        return mIndexElementSeqTemp;
    }

    /**
     * Définit l'index de l'ElementSequence temporaire
     * @param index index
     */
    public void setIndexElementSeqTemp(int index) {
        mIndexElementSeqTemp = index;
    }

    /**
     * Supprime la séquence dans la librairie dont l'index dans la librairie est passé en paramètre
     * @param indexSequenceDansLibrairie    index de la Sequence
     *                                      @see com.stephane.rothen.rchrono.model.ChronoModel#supprimerSequenceDansLibrairie(int)
     */
    public void supprimerSequenceDansLibrairie(int indexSequenceDansLibrairie) {
        mChronoModel.supprimerSequenceDansLibrairie(indexSequenceDansLibrairie);
    }

    /**
     * Supprime l'exercice dans la librairie dont l'index dans la librairie est passé en paramètre
     * @param indexExerciceDansLibrairie    index de l'Exercice
     *                                      @see com.stephane.rothen.rchrono.model.ChronoModel#supprimerExerciceDansLibrairie(int)
     */
    public void supprimerExerciceDansLibrairie(int indexExerciceDansLibrairie) {
        mChronoModel.supprimerExerciceDansLibrairie(indexExerciceDansLibrairie);
    }


    /**
     * Retourne le morceau dont l'id du morceau dans la base de données du téléphone est passé en paramètre
     * @param idMorceauDansTelephone    id du morceau dans la base de donnée du téléphone
     * @return Morceau
     * @see com.stephane.rothen.rchrono.model.ChronoModel#getMorceauFromBDD(long)
     */
    public Morceau getMorceauFromBDD(long idMorceauDansTelephone) {
        return mChronoModel.getMorceauFromBDD(idMorceauDansTelephone);
    }


    /**
     * Renvois true si la séquence dont l'index dans la librairie des séquences est utilisée dans ListeSequence
     * @param indexLibSequence  index de la séquence
     * @return true si utilisée
     * @see com.stephane.rothen.rchrono.model.ChronoModel#isSequenceUtilisee(int)
     */
    public boolean isSequenceUtilisee(int indexLibSequence) {
        return mChronoModel.isSequenceUtilisee(indexLibSequence);
    }

    /**
     * Renvois true si l'exercice dont l'index dans la librairie des exercices est utilisé dans ListeSequence
     * @param indexLibExercice  index de l'exercice
     * @return true si utilisé
     * @see com.stephane.rothen.rchrono.model.ChronoModel#isExerciceUtilise(int)
     */
    public boolean isExerciceUtilise(int indexLibExercice) {
        return mChronoModel.isExerciceUtilise(indexLibExercice);
    }

}
