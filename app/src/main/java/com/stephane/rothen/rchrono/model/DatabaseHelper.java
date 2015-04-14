package com.stephane.rothen.rchrono.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Classe contrôleur permettant d'ouvrir et de fermer la base de données
 * <p/>
 * Created by Stéphane on 17/02/2015.
 */
public class DatabaseHelper {
    /**
     * base de donnée SQLite
     */
    protected SQLiteDatabase m_db = null;
    /**
     * Gestionnaire de connexion à la base de donnée
     */
    protected DatabaseBuilder m_helper = null;


    /**
     * Constructeur
     *
     * @param context: Context de l'application
     */
    public DatabaseHelper(Context context) {
        m_helper = new DatabaseBuilder(context);
    }

    /**
     * Ouvre la base de donnée et la renvois
     *
     * @return : base de donnée SQLite ouverte
     */
    public SQLiteDatabase open() {
        m_db = m_helper.getReadableDatabase();
        return m_db;
    }

    /**
     * Ferme la base de donnée
     */
    public void close() {
        if (m_db != null)
            m_db.close();
    }


    /**
     * Restore la liste des séquences à exécuter depuis la base de donnée et la renvois
     *
     * @return Liste des séquences à exécuter
     */
    public ArrayList<Long> restoreListeSequences() {
        ArrayList<Long> tabSeq = new ArrayList<>();
        //déclaration des variables d'initialisation utilisées dans les boucles à des fins d'optimisation du code
        String requete;
        Cursor c;
        long idSequence;


        if (m_db != null) {
            requete = "SELECT * FROM " + DatabaseBuilder.LSTSEQUENCES;
            c = m_db.rawQuery(requete, null);
            int nbreSeq = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < nbreSeq; i++) {
                idSequence = c.getInt(c.getColumnIndex(DatabaseBuilder.LSTSEQUENCES_ID_SEQUENCE));
                tabSeq.add(idSequence);
            }
            c.close();
        }

        return tabSeq;
    }


    /**
     * Sauvegarde le tableau de séquences dans la base de donnée
     *
     * @param tab tableau à sauvegarder
     */
    public void saveLstSequence(ArrayList<Long> tab) {
        m_db.execSQL(DatabaseBuilder.LSTSEQUENCES_TABLE_DROP);
        m_db.execSQL(DatabaseBuilder.LSTSEQUENCES_TABLE_CREATE);
        for (Long i : tab) {
            // Ajoute la séquence dans la table ListeSequence
            ContentValues map = new ContentValues();
            map.put(DatabaseBuilder.LSTSEQUENCES_ID_SEQUENCE, String.valueOf(i));
            m_db.insert(DatabaseBuilder.LSTSEQUENCES, null, map);
        }
    }


    /**
     * Récupère et renvois la librairie des exercices
     *
     * @return Arraylist contenant les exercices récupérés
     */
    public ArrayList<Exercice> restoreLibrairieExercice() {

        ArrayList<Exercice> tab = new ArrayList<>();
        if (m_db != null) {
            Cursor cExercice = m_db.rawQuery("SELECT * FROM " + DatabaseBuilder.EXERCICE + ";", null);
            while (cExercice.moveToNext()) {
                int idExercice = cExercice.getInt(cExercice.getColumnIndex(DatabaseBuilder.EXERCICE_ID));
                String nom = cExercice.getString(cExercice.getColumnIndex(DatabaseBuilder.EXERCICE_NOM));
                String description = cExercice.getString(cExercice.getColumnIndex(DatabaseBuilder.EXERCICE_DESCRIPTION));
                int dureeParDefaut = cExercice.getInt(cExercice.getColumnIndex(DatabaseBuilder.EXERCICE_DUREEPARDEFAUT));
                int jouerPlaylist = cExercice.getInt(cExercice.getColumnIndex(DatabaseBuilder.EXERCICE_JOUERPLAYLISTPARDEFAUT));

                // recherche les morceaux composant la playlist de l'exercice
                String requete = "SELECT * FROM " + DatabaseBuilder.PLAYLIST + " WHERE " +
                        DatabaseBuilder.PLAYLIST_ID_EXERCICE + " = " + String.valueOf(idExercice) + " ORDER BY " +
                        DatabaseBuilder.PLAYLIST_POSITION + ";";

                Cursor cPlaylist = m_db.rawQuery(requete, null);

                Playlist pl = new Playlist();
                if (jouerPlaylist > 0)
                    pl.setJouerPlaylist(true);
                else
                    pl.setJouerPlaylist(false);

                while (cPlaylist.moveToNext()) {
                    pl.ajouterMorceau(cPlaylist.getInt(cPlaylist.getColumnIndex(DatabaseBuilder.PLAYLIST_ID_MORCEAU)));
                }
                cPlaylist.close();
                Exercice exercice = new Exercice(idExercice, nom, description, dureeParDefaut, pl);

                tab.add(exercice);
            }
            cExercice.close();
        }
        return tab;
    }


    /**
     * Récupère et renvois la librairie des sequences
     *
     * @return Arraylist contenant les sequences récupérées
     */
    public ArrayList<Sequence> restoreLibrairieSequences(ArrayList<Exercice> libExercices) {
        ArrayList<Sequence> tab = new ArrayList<>();

        //récupération de toutes les séquences de la table Sequence
        if (m_db != null) {
            Cursor c = m_db.rawQuery("SELECT * FROM " + DatabaseBuilder.SEQUENCE + ";", null);
            while (c.moveToNext()) {
                int idSequence = c.getInt(c.getColumnIndex(DatabaseBuilder.SEQUENCE_ID));
                String nomSequence = c.getString(c.getColumnIndex(DatabaseBuilder.SEQUENCE_NOM));
                int nbreRepetition = c.getInt(c.getColumnIndex(DatabaseBuilder.SEQUENCE_NOMBREREPETITON));
                int syntheseVocale = c.getInt(c.getColumnIndex(DatabaseBuilder.SEQUENCE_SYNTHESEVOCALE));
                Sequence seq = new Sequence(idSequence, nomSequence, nbreRepetition, new SyntheseVocale(syntheseVocale));
                //Recherche des ElementSequence et Exercices pour la séquence en cours
                String requete = "SELECT * FROM " + DatabaseBuilder.ELEMENTSEQUENCE + " INNER JOIN " + DatabaseBuilder.EXERCICE + " ON " +
                        DatabaseBuilder.ELEMENTSEQUENCE + "." + DatabaseBuilder.ELEMENTSEQUENCE_ID_EXERCICE + " = " + DatabaseBuilder.EXERCICE + "." +
                        DatabaseBuilder.EXERCICE_ID + " WHERE " + DatabaseBuilder.ELEMENTSEQUENCE + "." + DatabaseBuilder.ELEMENTSEQUENCE_ID_SEQUENCE +
                        " = " + String.valueOf(idSequence) + ";";
                Cursor cElement = m_db.rawQuery(requete, null);
                while (cElement.moveToNext()) {


                    int idElement = cElement.getInt(cElement.getColumnIndex(DatabaseBuilder.ELEMENTSEQUENCE_ID));
                    int idExercice = cElement.getInt(cElement.getColumnIndex(DatabaseBuilder.EXERCICE_ID));
                    int syntheseVocaleElement = cElement.getInt(cElement.getColumnIndex(DatabaseBuilder.ELEMENTSEQUENCE_SYNTHESEVOCALE));
                    int dureeElement = cElement.getInt(cElement.getColumnIndex(DatabaseBuilder.ELEMENTSEQUENCE_DUREE));
                    int notificationElement = cElement.getInt(cElement.getColumnIndex(DatabaseBuilder.ELEMENTSEQUENCE_NOTIFICATION));
                    int jouerPlaylist = cElement.getInt(cElement.getColumnIndex(DatabaseBuilder.ELEMENTSEQUENCE_JOUERPLAYLIST));
                    long idMorceauSonnerie = cElement.getInt(cElement.getColumnIndex(DatabaseBuilder.ELEMENTSEQUENCE_FICHIERAUDIONOTIFICATION));

                    //recherche de l'exercice dans la librairie des exercices
                    Exercice exercice = null;
                    for (Exercice e : libExercices) {
                        if (idExercice == e.getIdExercice()) {
                            exercice = e;
                            break;
                        }
                    }
                    if (exercice == null) {
                        Log.d("SQL", "Erreur l'exercice n'existe pas dans la librairie");
                    } else {


                        ElementSequence element = new ElementSequence(exercice.getIdExercice(), idElement, exercice.getNomExercice(), exercice.getDescriptionExercice(),
                                exercice.getDureeParDefaut(), exercice.getPlaylistParDefaut(), dureeElement, null,
                                new NotificationExercice(notificationElement, idMorceauSonnerie), new SyntheseVocale(syntheseVocaleElement));
                        seq.ajouterElement(element);
                    }
                }
                cElement.close();
                tab.add(seq);

            }
            c.close();
        }
        return tab;
    }


    /**
     * Ajoute la séquence passée en paramètre dans la base de données et renvois son id
     *
     * @param s séquence
     * @return identifiant de la séquence dans la base de données
     */
    public long ajouterSequenceDansBdd(Sequence s) {
        long idSequence;
        ContentValues map = new ContentValues();
        map.put(DatabaseBuilder.SEQUENCE_NOM, s.getNomSequence());
        map.put(DatabaseBuilder.SEQUENCE_NOMBREREPETITON, String.valueOf(s.getNombreRepetition()));
        map.put(DatabaseBuilder.SEQUENCE_SYNTHESEVOCALE, String.valueOf(s.getSyntheseVocale().getSyntheseVocaleForBdd()));
        idSequence = m_db.insert(DatabaseBuilder.SEQUENCE, null, map);
        s.setIdSequence(idSequence);
        for (ElementSequence el : s.getTabElement()) {
            ajouterElementSequenceDansBdd(el, s.getIdSequence());

        }


        return idSequence;
    }

    /**
     * Ajoute l'ElementSequence dans la base de données et renvois son id
     *
     * @param el         ElementSequence
     * @param idSequence id de la séquence contenant l'ElementSequence
     * @return id de l'ElementSequence dans la base de données
     */
    public long ajouterElementSequenceDansBdd(ElementSequence el, long idSequence) {
        ContentValues map;
        long retour = el.getIdExercice();
        long idMorceau;
        if (retour == -1) {
            retour = ajouterExerciceDansBdd(el.getExercice());
        }
        if (retour != -1) {
            el.setIdExercice(retour);

            idMorceau = el.getNotificationExercice().getFichierSonnerie();

            map = new ContentValues();
            map.put(DatabaseBuilder.ELEMENTSEQUENCE_ID_EXERCICE, el.getIdExercice());
            map.put(DatabaseBuilder.ELEMENTSEQUENCE_ID_SEQUENCE, String.valueOf(idSequence));
            map.put(DatabaseBuilder.ELEMENTSEQUENCE_NOTIFICATION, String.valueOf(el.getNotificationExercice().getNotificationForBdd()));
            map.put(DatabaseBuilder.ELEMENTSEQUENCE_FICHIERAUDIONOTIFICATION, String.valueOf(idMorceau));
            map.put(DatabaseBuilder.ELEMENTSEQUENCE_SYNTHESEVOCALE, String.valueOf(el.getSyntheseVocale().getSyntheseVocaleForBdd()));
            map.put(DatabaseBuilder.ELEMENTSEQUENCE_DUREE, String.valueOf(el.getDureeExercice()));
            map.put(DatabaseBuilder.ELEMENTSEQUENCE_POSITION, String.valueOf(el));
            retour = m_db.insert(DatabaseBuilder.ELEMENTSEQUENCE, null, map);
            if (retour != -1) {
                el.setIdElementSequence(retour);
                majPlaylistDansBdd(el.getPlaylistParDefaut(), -1, el.getIdElementSequence());
            }
        } else {
            Log.d("BDD", "Erreur ajouter Exercice dans bdd");
        }
        return retour;
    }

    /**
     * Ajoute l'Exercice dans la base de données et renvois son id
     *
     * @param e Exercice
     * @return id de l'Exercice dans la base de données
     */
    public long ajouterExerciceDansBdd(Exercice e) {
        long idExercice;
        ContentValues map = new ContentValues();
        map.put(DatabaseBuilder.EXERCICE_NOM, e.getNomExercice());
        map.put(DatabaseBuilder.EXERCICE_DESCRIPTION, e.getDescriptionExercice());
        map.put(DatabaseBuilder.EXERCICE_DUREEPARDEFAUT, String.valueOf(e.getDureeParDefaut()));
        map.put(DatabaseBuilder.EXERCICE_JOUERPLAYLISTPARDEFAUT, String.valueOf((e.getPlaylistParDefaut().getJouerPlaylist()) ? (1) : (0)));
        idExercice = m_db.insert(DatabaseBuilder.EXERCICE, null, map);
        e.setIdExercice(idExercice);
        majPlaylistDansBdd(e.getPlaylistParDefaut(), e.getIdExercice(), -1);

        return idExercice;
    }


    /**
     * Met à jour la séquence dans la base de données et retourne son identifiant
     *
     * @param s Sequence
     * @return identifiant de la séquence dans la base de données
     */
    public long majSequenceDansBdd(Sequence s) {
        long idSequence = s.getIdSequence();
        if (idSequence > 0) {
            ContentValues map = new ContentValues();
            map.put(DatabaseBuilder.SEQUENCE_NOM, s.getNomSequence());
            map.put(DatabaseBuilder.SEQUENCE_NOMBREREPETITON, String.valueOf(s.getNombreRepetition()));
            map.put(DatabaseBuilder.SEQUENCE_SYNTHESEVOCALE, String.valueOf(s.getSyntheseVocale().getSyntheseVocaleForBdd()));
            String where = DatabaseBuilder.SEQUENCE_ID + " = " + s.getIdSequence();
            long retour = m_db.update(DatabaseBuilder.SEQUENCE, map, where, null);
            if (retour == 1) {
                ElementSequence el;
                where = DatabaseBuilder.ELEMENTSEQUENCE_ID_SEQUENCE + " = " + s.getIdSequence();
                m_db.delete(DatabaseBuilder.ELEMENTSEQUENCE, where, null);

                for (int i = 0; i < s.getTabElement().size(); i++) {
                    el = s.getTabElement().get(i);
                    ajouterElementSequenceDansBdd(el, s.getIdSequence());
                }
            } else {
                Log.d("BDD", "Erreur maj sequence dans bdd : " + retour + " lignes modifiées");
                return -1;
            }
        } else {
            Log.d("BDD", "Erreur maj sequence dans bdd : id pas enregistré");
            return -1;
        }
        return idSequence;
    }


    /**
     * Met à jour l'Exercice dans la base de données
     *
     * @param e Exercice
     * @return id de l'Exercice
     */
    public long majExerciceDansBdd(Exercice e) {
        ContentValues map = new ContentValues();
        map.put(DatabaseBuilder.EXERCICE_NOM, e.getNomExercice());
        map.put(DatabaseBuilder.EXERCICE_DESCRIPTION, e.getDescriptionExercice());
        map.put(DatabaseBuilder.EXERCICE_DUREEPARDEFAUT, String.valueOf(e.getDureeParDefaut()));
        map.put(DatabaseBuilder.EXERCICE_JOUERPLAYLISTPARDEFAUT, String.valueOf((e.getPlaylistParDefaut().getJouerPlaylist()) ? (1) : (0)));
        String where = DatabaseBuilder.EXERCICE_ID + " = " + String.valueOf(e.getIdExercice());
        m_db.update(DatabaseBuilder.EXERCICE, map, where, null);
        majPlaylistDansBdd(e.getPlaylistParDefaut(), e.getIdExercice(), -1);

        return e.getIdExercice();
    }


    /**
     * Met à jour la Playlist dans la base de données
     *
     * @param pl                Playlist
     * @param idExercice        id de l'exercice lié à la Playlist ou -1
     * @param idElementSequence id de l'ElementSequence lié à la Playlist ou -1
     */
    public void majPlaylistDansBdd(Playlist pl, long idExercice, long idElementSequence) {
        if (idExercice > 0) {
            idElementSequence = -1;
        } else {
            idExercice = -1;
        }

        //supprime tous les champs playlist qui pointent vers ce morceau

        String requete = "DELETE FROM " + DatabaseBuilder.PLAYLIST + " WHERE " + DatabaseBuilder.PLAYLIST_ID_EXERCICE + " = " +
                String.valueOf(idExercice) + " AND " + DatabaseBuilder.PLAYLIST_ID_ELEMENTSEQUENCE + " = " + String.valueOf(idElementSequence) + " ;";
        m_db.execSQL(requete);

        ContentValues map;
        for (int i = 0; i < pl.getNbreMorceaux(); i++) {
            //ajout de la ligne de la playlist dans la base de donnée
            map = new ContentValues();
            map.put(DatabaseBuilder.PLAYLIST_ID_MORCEAU, pl.getMorceauAt(i));
            map.put(DatabaseBuilder.PLAYLIST_ID_EXERCICE, idExercice);
            map.put(DatabaseBuilder.PLAYLIST_ID_ELEMENTSEQUENCE, idElementSequence);
            map.put(DatabaseBuilder.PLAYLIST_POSITION, i);
            m_db.insert(DatabaseBuilder.PLAYLIST, null, map);
        }
    }


    /**
     * Supprime la séquence passée en paramètre de la base de données
     *
     * @param s Sequence
     */
    public void supprimerSequenceDansBdd(Sequence s) {
        int retour;

        String where = DatabaseBuilder.SEQUENCE_ID + " = " + s.getIdSequence();
        retour = m_db.delete(DatabaseBuilder.SEQUENCE, where, null);
        if (retour != 1) {
            Log.d("BDD", "Erreur supprimerSequenceDansLibrairie : " + retour + " lignes supprimées");
        } else {
            for (ElementSequence el : s.getTabElement()) {
                where = DatabaseBuilder.PLAYLIST_ID_ELEMENTSEQUENCE + " = " + el.getIdElementSequence();
                m_db.delete(DatabaseBuilder.PLAYLIST, where, null);

            }
            where = DatabaseBuilder.ELEMENTSEQUENCE_ID_SEQUENCE + " = " + s.getIdSequence();
            m_db.delete(DatabaseBuilder.ELEMENTSEQUENCE, where, null);
        }
    }


    /**
     * Supprime l'exercice passé en paramètre
     *
     * @param e Exercice
     */
    public void supprimerExerciceDansBdd(Exercice e) {
        int retour;
        String where = DatabaseBuilder.EXERCICE_ID + " = " + e.getIdExercice();
        retour = m_db.delete(DatabaseBuilder.EXERCICE, where, null);
        if (retour != 1) {
            Log.d("BDD", "Erreur supprimerExerciceDansBdd : " + retour + " lignes supprimées");
        } else {
            where = DatabaseBuilder.PLAYLIST_ID_EXERCICE + " = " + e.getIdExercice();
            m_db.delete(DatabaseBuilder.PLAYLIST, where, null);
        }
    }
}
