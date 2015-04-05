package com.stephane.rothen.rchrono.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Classe mère modèle
 * Created by stéphane on 12/03/2015.
 */
public class ChronoModel {

    /**
     * Gestionnaire de l'accès à la base de donnée
     *
     * @see com.stephane.rothen.rchrono.model.DAOBase
     */
    protected DAOBase mBddHelper;

    /**
     * Instance de l'objet contenant la librairie des exercices présents dans la base de données du téléphone
     */
    protected ArrayList<Exercice> mLibExercices;
    /**
     * Instance de l'objet contenant la librairie des séquences présentes dans la base de données du téléphone
     */
    protected ArrayList<Sequence> mLibSequences;
    /**
     * Instance de l'objet contenant la liste des séquences à effectuer
     */
    protected ArrayList<Long> mListeSequences;

    /**
     * Tableau contenant les sons utilisés dans l'application
     */
    protected ArrayList<Morceau> mLibMorceaux;


    /**
     * Contructeur
     *
     * @param context Context de l'application
     */
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
        mLibMorceaux = mBddHelper.restoreLibrairieMorceau();
        mBddHelper.close();
        return true;
    }


    /**
     * Sauvegarde les classes modèle dans la base de donnée
     *
     * @return état de la sauvegarde : true si réussie
     */
    private boolean saveListeSequence() {
        mBddHelper.open();
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
            long idSequence = ajouterSequenceDansBdd(s);
            s.setIdSequence(idSequence);
            mLibSequences.add(s);
            for (int i = 0; i < s.getTabElement().size(); i++) {
                ajouterExerciceDansLibrairie(s.getTabElement().get(i).getExercice());
                long fichierSonnerie = s.getTabElement().get(i).getNotificationExercice().getFichierSonnerie();
                if (fichierSonnerie > 0)
                    getMorceauFromLibMorceau(fichierSonnerie).ajouteUtilisation();
            }

        }
        mListeSequences.add(s.getIdSequence());
        saveListeSequence();
    }


    /**
     * Ajoute l'exercice passé en paramètre dans la Librairie des exercices
     *
     * @param e Exercice
     */
    private void ajouterExerciceDansLibrairie(Exercice e) {
        boolean exerciceTrouve = false;
        for (int i = 0; i < mLibExercices.size(); i++) {
            if (mLibExercices.get(i).getIdExercice() == e.getIdExercice()) {
                exerciceTrouve = true;

                break;
            }
        }
        if (!exerciceTrouve) {
            mLibExercices.add(e);
            for (int i = 0; i < e.getPlaylistParDefaut().getNbreMorceaux(); i++) {
                getMorceauFromLibMorceau(e.getPlaylistParDefaut().getMorceauAt(i)).ajouteUtilisation();
            }
        }

    }

    /**
     * Renvois la séquence dont l'index dans la ListeSequence est passé en paramètre
     * @param i     index du tableau ListeSequence
     * @return Sequence
     */
    public Sequence getSeqFromLstSequenceAt(int i) {
        long indexSequence = mListeSequences.get(i);
        for (Sequence s : mLibSequences) {
            if (s.getIdSequence() == indexSequence)
                return s;
        }
        return null;
    }



    /**
     * Modifie la séquence dans la liste des séquences et dans la librairie

     * @param s                  Nouvelle séquence
     */
    public void modifierSequenceDansListe(Sequence s) {//todo tester gestion libmorceau
        if (s.getIdSequence() > 0) {
            boolean seqModifiee = false;
            Sequence anciennneSeq;
            ElementSequence el;
            Exercice ancienEx;
            long sonnerie;
            int j, k;
            for (int i = 0; i < mLibSequences.size(); i++) {
                anciennneSeq = mLibSequences.get(i);
                if (anciennneSeq.getIdSequence() == s.getIdSequence()) {
                    //supprime les morceaux de l'ancienne sequence
                    for (j = 0; j < anciennneSeq.getTabElement().size(); j++) {
                        el = anciennneSeq.getTabElement().get(j);
                        sonnerie = el.getNotificationExercice().getFichierSonnerie();
                        if (sonnerie > 0)
                            enleverUtilisation(sonnerie);
                        for (k = 0; k < el.getPlaylistParDefaut().getNbreMorceaux(); k++) {
                            enleverUtilisation(el.getPlaylistParDefaut().getMorceauAt(k));
                        }
                    }
                    mLibSequences.set(i, s);
                    //ajoute les morceaux de la nouvelle sequence
                    for (j = 0; j < s.getTabElement().size(); j++) {
                        el = s.getTabElement().get(j);
                        sonnerie = el.getNotificationExercice().getFichierSonnerie();
                        if (sonnerie > 0)
                            ajouterUtilisation(sonnerie);
                        for (k = 0; k < el.getPlaylistParDefaut().getNbreMorceaux(); k++) {
                            ajouterUtilisation(el.getPlaylistParDefaut().getMorceauAt(k));
                        }
                    }
                    modifierSequenceDansBdd(s);
                    seqModifiee = true;
                    break;
                }
            }
            if (seqModifiee) {
                //pour chaque ElementSequence, met à jour l'exercice dans la librairie des exercices
                for (int i = 0; i < s.getTabElement().size(); i++) {
                    el = s.getTabElement().get(i);
                    boolean elModifie = false;
                    for (j = 0; j < mLibExercices.size(); j++) {
                        if (el.getIdExercice() == mLibExercices.get(j).getIdExercice()) {
                            //supprime l'utilisation des morceaux pour l'exercice mis à jour
                            ancienEx = mLibExercices.get(j);
                            for (k = 0; k < ancienEx.getPlaylistParDefaut().getNbreMorceaux(); k++) {
                                enleverUtilisation(ancienEx.getPlaylistParDefaut().getMorceauAt(k));
                            }
                            mLibExercices.set(j, el.getExercice());
                            //affecte les utilisations des morceaux du nouvel exercice
                            for (k = 0; k < el.getPlaylistParDefaut().getNbreMorceaux(); k++) {
                                ajouterUtilisation(el.getPlaylistParDefaut().getMorceauAt(k));
                            }
                            elModifie = true;
                            break;
                        }
                    }
                    if (!elModifie) {
                        mLibExercices.add(el.getExercice());
                        //affecte les utilisations des morceaux du nouvel exercice
                        for (k = 0; k < el.getPlaylistParDefaut().getNbreMorceaux(); k++) {
                            ajouterUtilisation(el.getPlaylistParDefaut().getMorceauAt(k));
                        }
                    }

                }
                nettoyerLibMorceaux();
            } else {
                Log.d("MODEL", "La séquence n'a pas été trouvée dans la librairie");
            }
        } else {
            Log.d("MODEL", "Erreur modifierSequenceDansListe : L'id de la séquence n'est pas enregistré");
        }
    }

    /**
     * Ajoute un Morceau dans la librairie et renvois son id
     *
     * @param idMorceauDansTelephone id du morceau dans la base de données du téléphone
     * @param titre                  titre du morceau
     * @param artiste                artiste du morceau
     * @return id du morceau dans la base de données de l'application
     */
    public long ajouterMorceau(long idMorceauDansTelephone, String titre, String artiste) {
        //recherche si le morceau est déjà dans la librairie
        for (int i = 0; i < mLibMorceaux.size(); i++) {
            if (mLibMorceaux.get(i).getIdMorceauDansTelephone() == idMorceauDansTelephone) {
                mLibMorceaux.get(i).ajouteUtilisation();

                return idMorceauDansTelephone;
            }
        }
        //enregistre le morceau
        Morceau m = new Morceau(-1, idMorceauDansTelephone, titre, artiste);
        m.ajouteUtilisation();
        ajouterMorceauDansBdd(m);
        mLibMorceaux.add(m);
        return idMorceauDansTelephone;

    }

    /**
     * Retourne le morceau dont l'id du morceau dans la base de données du téléphone est passé en paramètre
     *
     * @param idMorceauDansTelephone id du morceau dans la base de données du téléphone
     * @return Morceau
     */
    public Morceau getMorceauFromLibMorceau(long idMorceauDansTelephone) {
        for (int i = 0; i < mLibMorceaux.size(); i++) {
            if (mLibMorceaux.get(i).getIdMorceauDansTelephone() == idMorceauDansTelephone)
                return mLibMorceaux.get(i);
        }
        return null;
    }

    /**
     * Enlève une utilisation sur un Morceau de la librairie des morceaux
     *
     *
     * @param idMorceau id du morceau
     */
    public void enleverUtilisation(long idMorceau) {
        Morceau m = getMorceauFromLibMorceau(idMorceau);
        m.enleveUtilisation();
    }

    /**
     * Ajoute une utilisation à un morceau de la librairie des morceaux
     *
     * @param idMorceau id du morceau
     */
    public void ajouterUtilisation(long idMorceau) {
        Morceau m = getMorceauFromLibMorceau(idMorceau);
        m.ajouteUtilisation();
    }

    /**
     * Parcours la librairie des morceaux et supprime ceux qui sont à 0 utilisation
     */
    private void nettoyerLibMorceaux() {
        int[] morceauxASuppr = new int[0];
        int[] tmp;

        for (int i = 0; i < mLibMorceaux.size(); i++) {
            Morceau m = mLibMorceaux.get(i);
            if (m.getNbreUtilisations() <= 0) {

                mBddHelper.supprimerMorceauDansBdd(m.getIdMorceau());

                tmp = morceauxASuppr;
                morceauxASuppr = new int[morceauxASuppr.length + 1];
                System.arraycopy(tmp, 0, morceauxASuppr, 0, tmp.length);
                morceauxASuppr[tmp.length] = i;
            }
        }
        for (int aMorceauxASuppr : morceauxASuppr) {
            mLibMorceaux.remove(aMorceauxASuppr);
        }
    }

    /**
     * Modifie la séquence dans la base de données
     *
     * @param s Sequence
     * @return index de la séquence modifiée
     */
    private long modifierSequenceDansBdd(Sequence s) {
        mBddHelper.open();
        long id = mBddHelper.majSequenceDansBdd(s);
        mBddHelper.close();
        return id;
    }


    /**
     * Ajoute la séquence dans la base de données
     *
     * @param s Sequence
     * @return id de la séquence dans la base de données
     */
    private long ajouterSequenceDansBdd(Sequence s) {
        mBddHelper.open();
        long id = mBddHelper.ajouterSequenceDansBdd(s);
        mBddHelper.close();
        return id;
    }




    public ArrayList<Sequence> getLibrairieSequences() {
        return mLibSequences;
    }

    public ArrayList<Exercice> getLibrairieExercices() {
        return mLibExercices;
    }

    public ArrayList<Long> getListeSequences() {
        return mListeSequences;
    }

    /**
     * Supprime la séquence de la ListeSequence
     * @param index index de la séquence dans ListeSequence à supprimer
     */

    public void supprimerSequenceDansListe(int index) {
        mListeSequences.remove(index);
        saveListeSequence();
    }

    /**
     * Supprime la séquence dans la librairie des séquences
     *
     * @param index index de la séquence dans LibSequences
     */
    public void supprimerSequenceDansLibrairie(int index) {
        Sequence s = mLibSequences.get(index);
        mBddHelper.open();
        mBddHelper.supprimerSequenceDansBdd(s);
        mBddHelper.close();
        ElementSequence el;
        long idMorceau;
        for (int i = 0; i < s.getTabElement().size(); i++) {
            el = s.getTabElement().get(i);
            idMorceau = el.getNotificationExercice().getFichierSonnerie();
            if (getMorceauFromLibMorceau(idMorceau).enleveUtilisation() == 0)
                supprimerMorceauDansBdd(getMorceauFromLibMorceau(idMorceau));


        }
    }

    /**
     * Ajoute un morceau dans la base de données
     *
     * @param m Morceau
     * @return id du morceau ajouté
     */
    private long ajouterMorceauDansBdd(Morceau m) {
        long idMorceau;
        mBddHelper.open();
        idMorceau = mBddHelper.ajouterMorceauDansBdd(m);
        m.setIdMorceau(idMorceau);
        mBddHelper.close();
        return idMorceau;
    }

    /**
     * Supprime le morceau de la base de données
     *
     * @param m Morceau
     */
    private void supprimerMorceauDansBdd(Morceau m) {
        mBddHelper.open();
        mBddHelper.supprimerMorceauDansBdd(m.getIdMorceau());
        mBddHelper.close();
    }


    /**
     * Supprime l'exercice de la libExercice
     *
     * @param index index de l'Exercice dans libExercice
     */
    public void supprimerExerciceDansLibrairie(int index) {
        Exercice e = mLibExercices.get(index);
        long idMorceau;
        mBddHelper.open();
        mBddHelper.supprimerExerciceDansBdd(e);
        mBddHelper.close();
        for (int i = 0; i < e.getPlaylistParDefaut().getNbreMorceaux(); i++) {
            idMorceau = e.getPlaylistParDefaut().getMorceauAt(i);
            if (getMorceauFromLibMorceau(idMorceau).enleveUtilisation() == 0)
                supprimerMorceauDansBdd(getMorceauFromLibMorceau(idMorceau));
        }
    }

    /**
     * Vérifie si la séquence dont l'index est passé en paramètre est présente dans listeSequence
     *
     * @param indexLibSequence index de la séquence
     * @return true si présente dans listeSequence
     */
    public boolean isSequenceUtilisee(int indexLibSequence) {
        Sequence s = mLibSequences.get(indexLibSequence);
        for (int i = 0; i < mListeSequences.size(); i++) {
            if (s.getIdSequence() == mListeSequences.get(i)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Vérifie si l'exercice dont l'index est passé en paramètre est présent dans listeSequence
     *
     * @param indexLibExercice index de l'exercice
     * @return true si présent dans listeSequence
     */
    public boolean isExerciceUtilise(int indexLibExercice) {
        Exercice e = mLibExercices.get(indexLibExercice);
        Sequence s;
        for (int i = 0; i < mListeSequences.size(); i++) {
            s = getSeqFromLstSequenceAt(i);
            for (int j = 0; j < s.getTabElement().size(); j++) {
                if (s.getTabElement().get(j).getIdExercice() == e.getIdExercice())
                    return true;
            }
        }
        return false;
    }
}
