package com.stephane.rothen.rchrono.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Classe mère modèle
 * Created by stéphane on 12/03/2015.
 */
public class ChronoModel {

    /**
     * constante représentant Uri des morceaux de musique
     */
    private static Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    /**
     * Constante représentant les colones Titre et Artiste des Morceaux dans la base de donnée du téléphone
     */
    private static String[] projection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};
    /**
     * Gestionnaire de l'accès à la base de donnée
     *
     * @see DatabaseHelper
     */
    protected DatabaseHelper mBddHelper;
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
    protected ArrayList<Long> mListeSequences; //todo utiliser un sparseArray
    /**
     * ContentResolver pour la recherche des données des musiques
     */
    private ContentResolver mMusicResolver;


    /**
     * Contructeur
     *
     * @param context Context de l'application
     */
    public ChronoModel(Context context) {
        mBddHelper = new DatabaseHelper(context);
        mMusicResolver = context.getContentResolver();
    }

    /**
     * Restore les classes modèle depuis la base de donnée
     *
     * @return état de la restoration : true si réussie
     * @see DatabaseHelper
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
        }

    }

    /**
     * Renvois la séquence dont l'index dans la ListeSequence est passé en paramètre
     *
     * @param i index du tableau ListeSequence
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
     *
     * @param s Nouvelle séquence
     */
    public void modifierSequenceDansListe(Sequence s) {
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
                    mLibSequences.set(i, s);
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
                        Exercice ex = el.getExercice();
                        if (ex.getIdExercice() == mLibExercices.get(j).getIdExercice()) {
                            mLibExercices.set(j, ex);
                            mBddHelper.open();
                            mBddHelper.majExerciceDansBdd(ex);
                            mBddHelper.close();
                            elModifie = true;
                            break;
                        }
                    }
                    if (!elModifie) {
                        mLibExercices.add(el.getExercice());
                        mBddHelper.open();
                        mBddHelper.ajouterExerciceDansBdd(el.getExercice());
                        mBddHelper.close();
                    }
                }
            } else {
                Log.d("MODEL", "La séquence n'a pas été trouvée dans la librairie");
            }
        } else {
            Log.d("MODEL", "Erreur modifierSequenceDansListe : L'id de la séquence n'est pas enregistré");
        }
    }


    /**
     * Retourne le morceau dont l'id du morceau dans la base de données du téléphone est passé en paramètre
     *
     * @param idMorceauDansTelephone id du morceau dans la base de données du téléphone
     * @return Morceau
     */
    public Morceau getMorceauFromBDD(long idMorceauDansTelephone) {
        if (idMorceauDansTelephone > 0) {
            String where = android.provider.MediaStore.Audio.Media._ID + " = " + idMorceauDansTelephone;

            Cursor musicCursor = mMusicResolver.query(musicUri, projection, where, null, null);
            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                String titre = musicCursor.getString(titleColumn);
                String artiste = musicCursor.getString(artistColumn);
                Morceau m = new Morceau(-1, idMorceauDansTelephone, titre, artiste);
                return m;
            }
        }
        return null;
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
     *
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

    /**
     * Remet les playlists des ElementSequence de listeSequence à 0
     */
    public void resetPlaylists() {
        for (int i = 0; i < mListeSequences.size(); i++) {
            Sequence s = getSeqFromLstSequenceAt(i);
            for (int j = 0; j < s.getTabElement().size(); j++) {
                ElementSequence el = s.getTabElement().get(j);
                el.getPlaylistParDefaut().resetPlaylist();
            }
        }
    }
}
