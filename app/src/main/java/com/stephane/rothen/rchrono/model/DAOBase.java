package com.stephane.rothen.rchrono.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Classe controleur permettant d'ouvrir et de fermer la base de donnée
 * <p/>
 * Created by Stéphane on 17/02/2015.
 */
public class DAOBase {
    /**
     * base de donnée SQLite
     */
    protected SQLiteDatabase m_db = null;
    /**
     * Gestionnaire de connexion à la base de donnée
     */
    protected DatabaseHelper m_helper = null;


    /**
     * Constructeur
     *
     * @param context: Context de l'application
     */
    public DAOBase(Context context) {
        m_helper = new DatabaseHelper(context);
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

    public void clearTables() {
        m_db.execSQL(DatabaseHelper.LSTSEQUENCES_TABLE_DROP);
        m_db.execSQL(DatabaseHelper.LSTSEQUENCES_TABLE_CREATE);
        m_db.execSQL(DatabaseHelper.ELEMENTSEQUENCE_TABLE_DROP);
        m_db.execSQL(DatabaseHelper.ELEMENTSEQUENCE_TABLE_CREATE);
    }

    /**
     * Renvois la base de donnée
     *
     * @return
     */
    public SQLiteDatabase getDb() {
        return m_db;
    }


    public ArrayList<Integer> restoreListeSequences() {
        ArrayList<Integer> tabSeq = new ArrayList<>();
        //déclaration des variables d'initialisation utilisées dans les boucles à des fins d'optimisation du code
        String requete;
        Cursor c;
        int idSequence;


        if (m_db != null) {
            requete = "SELECT * FROM " + DatabaseHelper.LSTSEQUENCES;
            c = m_db.rawQuery(requete, null);
            int nbreSeq = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < nbreSeq; i++) {
                idSequence = c.getInt(c.getColumnIndex(DatabaseHelper.LSTSEQUENCES_ID_SEQUENCE));
                tabSeq.add(idSequence);
            }
        }
        return tabSeq;
    }


    /**
     * Renvois le nombre de séquences dans la ListeSequences
     *
     * @return
     */

    public int getNbreSequence() {
        Cursor c = m_db.rawQuery("SELECT * FROM " + DatabaseHelper.LSTSEQUENCES + " ;", null);
        return c.getCount();
    }

    /**
     * Sauvegarde le tableau de séquences dans la base de donnée
     *
     * @param tab
     */
    public void saveLstSequence(ArrayList<Integer> tab) {

        for (Integer i : tab) {
            // Ajoute la séquence dans la table ListeSequence
            ContentValues map = new ContentValues();
            map.put(DatabaseHelper.LSTSEQUENCES_ID_SEQUENCE, String.valueOf(i));
            m_db.insert(DatabaseHelper.LSTSEQUENCES, null, map);
            }
        }


    /**
     * Sauvegarde la librairie des exercices dans la base de donnée
     *
     * @param tab Arraylist contenant les exercices
     */

    public void saveLibrairieExercice(ArrayList<Exercice> tab) {
        for (Exercice e : tab) {
            //recherche si l'exercice est déja dans la base
            String where = DatabaseHelper.EXERCICE_NOM + " = '" + e.getNomExercice() + "'";
            Cursor c = m_db.rawQuery("SELECT * FROM " + DatabaseHelper.EXERCICE + " WHERE " + where, null);
            int idExercice;
            ContentValues map;
            if (c.getCount() == 1) {
                // si exercice déja dans la base, met a jour ses informations
                c.moveToFirst();
                idExercice = c.getInt(c.getColumnIndex(DatabaseHelper.EXERCICE_ID));
                map = new ContentValues();
                map.put(DatabaseHelper.EXERCICE_DESCRIPTION, e.getDescriptionExercice());
                map.put(DatabaseHelper.EXERCICE_DUREEPARDEFAUT, String.valueOf(e.getDureeParDefaut()));
                map.put(DatabaseHelper.EXERCICE_JOUERPLAYLISTPARDEFAUT, String.valueOf((e.getPlaylistParDefaut().getJouerPlaylist()) ? (1) : (0)));
                where = DatabaseHelper.EXERCICE_ID + " = " + String.valueOf(idExercice);
                m_db.update(DatabaseHelper.EXERCICE, map, where, null);
            } else {
                //sinon rajoute l'exercice à la base
                map = new ContentValues();
                map.put(DatabaseHelper.EXERCICE_NOM, e.getNomExercice());
                map.put(DatabaseHelper.EXERCICE_DESCRIPTION, e.getDescriptionExercice());
                map.put(DatabaseHelper.EXERCICE_DUREEPARDEFAUT, String.valueOf(e.getDureeParDefaut()));
                map.put(DatabaseHelper.EXERCICE_JOUERPLAYLISTPARDEFAUT, String.valueOf((e.getPlaylistParDefaut().getJouerPlaylist()) ? (1) : (0)));
                idExercice = (int) m_db.insert(DatabaseHelper.EXERCICE, null, map);
            }

            //récupere la playlist de l'exercice
            Playlist pl = e.getPlaylistParDefaut();

            //supprime tous les elements de playlist pointant vers l'exercice en cours

            String requete = "DELETE FROM " + DatabaseHelper.PLAYLIST + " WHERE " + DatabaseHelper.PLAYLIST_ID_EXERCICE + " = " +
                    String.valueOf(idExercice) + " ;";
            m_db.execSQL(requete);

            // Verifie que le morceau est déja dans la base et sinon ajoute le morceau dans la table Morceau
            // Ajoute l'element de playlist dans la table playlist
            for (int i = 0; i < pl.getNbreMorceaux(); i++) {
                long identifiant = pl.getMorceauAt(i).getIdMorceau();
                Cursor cMorceau = m_db.query(DatabaseHelper.MORCEAU, new String[]{DatabaseHelper.MORCEAU_TITRE, DatabaseHelper.MORCEAU_ID,
                        DatabaseHelper.MORCEAU_IDENTIFIANT, DatabaseHelper.MORCEAU_ARTISTE}, DatabaseHelper.MORCEAU_IDENTIFIANT + " ='" +
                        String.valueOf(identifiant) + "'", null, null, null, null);
                int idMorceau = -1;
                cMorceau.moveToFirst();
                if (cMorceau.getCount() == 0) {
                    map = new ContentValues();
                    map.put(DatabaseHelper.MORCEAU_TITRE, pl.getMorceauAt(i).getTitre());
                    map.put(DatabaseHelper.MORCEAU_IDENTIFIANT, String.valueOf(identifiant));
                    map.put(DatabaseHelper.MORCEAU_ARTISTE, pl.getMorceauAt(i).getArtiste());
                    idMorceau = (int) m_db.insert(DatabaseHelper.MORCEAU, null, map);

                } else {
                    idMorceau = cMorceau.getInt(cMorceau.getColumnIndex(DatabaseHelper.MORCEAU_ID));
                }
                map = new ContentValues();
                map.put(DatabaseHelper.PLAYLIST_ID_MORCEAU, idMorceau);
                map.put(DatabaseHelper.PLAYLIST_ID_EXERCICE, idExercice);
                map.put(DatabaseHelper.PLAYLIST_ID_ELEMENTSEQUENCE, "-1");
                map.put(DatabaseHelper.PLAYLIST_POSITION, i);
                m_db.insert(DatabaseHelper.PLAYLIST, null, map);
            }
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
            Cursor cExercice = m_db.rawQuery("SELECT * FROM " + DatabaseHelper.EXERCICE + ";", null);
            while (cExercice.moveToNext()) {
                int idExercice = cExercice.getInt(cExercice.getColumnIndex(DatabaseHelper.EXERCICE_ID));
                String nom = cExercice.getString(cExercice.getColumnIndex(DatabaseHelper.EXERCICE_NOM));
                String description = cExercice.getString(cExercice.getColumnIndex(DatabaseHelper.EXERCICE_DESCRIPTION));
                int dureeParDefaut = cExercice.getInt(cExercice.getColumnIndex(DatabaseHelper.EXERCICE_DUREEPARDEFAUT));
                int jouerPlaylist = cExercice.getInt(cExercice.getColumnIndex(DatabaseHelper.EXERCICE_JOUERPLAYLISTPARDEFAUT));

                // recherche les morceaux composant la playlist de l'exercice
                String requete = "SELECT " + DatabaseHelper.MORCEAU + "." + DatabaseHelper.MORCEAU_IDENTIFIANT + ", " + DatabaseHelper.MORCEAU + "."
                        + DatabaseHelper.MORCEAU_TITRE + ", " + DatabaseHelper.MORCEAU + "." + DatabaseHelper.MORCEAU_ARTISTE + " FROM " +
                        DatabaseHelper.MORCEAU + " INNER JOIN " + DatabaseHelper.PLAYLIST + " ON " + DatabaseHelper.MORCEAU + "." +
                        DatabaseHelper.MORCEAU_ID + " = " + DatabaseHelper.PLAYLIST + "." + DatabaseHelper.PLAYLIST_ID_MORCEAU + " WHERE " +
                        DatabaseHelper.PLAYLIST + "." + DatabaseHelper.PLAYLIST_ID_EXERCICE + " = " + String.valueOf(idExercice) + " ORDER BY " +
                        DatabaseHelper.PLAYLIST + "." + DatabaseHelper.PLAYLIST_POSITION + ";";

                Cursor cPlaylist = m_db.rawQuery(requete, null);

                Playlist pl = new Playlist();
                if (jouerPlaylist > 0)
                    pl.setJouerPlaylist(true);
                else
                    pl.setJouerPlaylist(false);

                while (cPlaylist.moveToNext()) {
                    pl.ajouterMorceau(new Morceau(cPlaylist.getLong(cPlaylist.getColumnIndex(DatabaseHelper.MORCEAU_IDENTIFIANT)),
                            cPlaylist.getString(cPlaylist.getColumnIndex(DatabaseHelper.MORCEAU_TITRE)),
                            cPlaylist.getString(cPlaylist.getColumnIndex(DatabaseHelper.MORCEAU_ARTISTE))));
                }

                Exercice exercice = new Exercice(idExercice, nom, description, dureeParDefaut, pl);

                tab.add(exercice);
            }
        }
        return tab;
    }

    /**
     * Sauvegarde la librairie des séquences dans la base de donnée via l'Arraylist passé en parametre
     *
     * @param tab
     */

    public void saveLibrairieSequences(ArrayList<Sequence> tab) {


        long idSonnerie = -1;
        for (Sequence s : tab) {
            //recherche la séquence dans la base de donnée ( séquence identique si meme nom et meme nombre de repetitions )
            String requete = "SELECT * FROM " + DatabaseHelper.SEQUENCE + " WHERE " + DatabaseHelper.SEQUENCE + "." + DatabaseHelper.SEQUENCE_NOM +
                    " = '" + s.getNomSequence() + "' AND " + DatabaseHelper.SEQUENCE + "." + DatabaseHelper.SEQUENCE_NOMBREREPETITON + " = " +
                    String.valueOf(s.getNombreRepetition());

            Cursor c = m_db.rawQuery(requete, null);
            if (c.getCount() == 1) {
                // mise a jour de la séquence dans la base
                c.moveToFirst();
                ContentValues map = new ContentValues();
                map.put(DatabaseHelper.SEQUENCE_NOMBREREPETITON, String.valueOf(s.getNombreRepetition()));
                map.put(DatabaseHelper.SEQUENCE_SYNTHESEVOCALE, String.valueOf(s.getSyntheseVocale().getSyntheseVocaleForBdd()));
                String where = DatabaseHelper.SEQUENCE_ID + " = " + c.getString(c.getColumnIndex(DatabaseHelper.SEQUENCE_ID));
                m_db.update(DatabaseHelper.SEQUENCE, map, where, null);
                int position = 0;
                for (ElementSequence element : s.getTabElement()) {

                    //mise a jour des élémentSequence

                    //recherche de l'id de l'exercice avec son nom
                    Cursor cExercice = m_db.query(DatabaseHelper.EXERCICE, new String[]{DatabaseHelper.EXERCICE_ID}, DatabaseHelper.EXERCICE_NOM
                            + "='" + element.getNomExercice() + "'", null, null, null, null);
                    if (cExercice.getCount() != 1) {
                        //erreur l'exercice n'est pas référencé dans la table
                        Log.d("SQL", "L'exercice n'est pas référencé dans la table ");
                    } else {
                        cExercice.moveToFirst();

                        //enregistrement du morceau de notification
                        Morceau m = element.getNotificationExercice().getFichierSonnerie();
                        if (m != null) {
                            Cursor cMorceau = m_db.rawQuery("SELECT * FROM " + DatabaseHelper.MORCEAU + " WHERE " + DatabaseHelper.MORCEAU_IDENTIFIANT +
                                    "='" + m.getIdMorceau() + "'", null);

                            if (cMorceau.getCount() == 1) {
                                cMorceau.moveToFirst();
                                idSonnerie = cMorceau.getLong(cMorceau.getColumnIndex(DatabaseHelper.MORCEAU_ID));
                            } else {
                                map = new ContentValues();
                                map.put(DatabaseHelper.MORCEAU_IDENTIFIANT, m.getIdMorceau());
                                map.put(DatabaseHelper.MORCEAU_TITRE, m.getTitre());
                                map.put(DatabaseHelper.MORCEAU_ARTISTE, m.getArtiste());
                                idSonnerie = m_db.insert(DatabaseHelper.MORCEAU, null, map);
                            }
                        }
                        map = new ContentValues();
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_ID_EXERCICE, cExercice.getString(cExercice.getColumnIndex(DatabaseHelper.EXERCICE_ID)));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_ID_SEQUENCE, c.getString(c.getColumnIndex(DatabaseHelper.SEQUENCE_ID)));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_NOTIFICATION, String.valueOf(element.getNotificationExercice().getNotificationForBdd()));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_FICHIERAUDIONOTIFICATION, String.valueOf(idSonnerie));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_SYNTHESEVOCALE, String.valueOf(element.getSyntheseVocale().getSyntheseVocaleForBdd()));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_DUREE, String.valueOf(element.getDureeExercice()));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_POSITION, String.valueOf(position));
                        m_db.insert(DatabaseHelper.ELEMENTSEQUENCE, null, map);
                    }
                    position++;
                }
            } else {
                ContentValues map = new ContentValues();
                map.put(DatabaseHelper.SEQUENCE_NOM, s.getNomSequence());
                map.put(DatabaseHelper.SEQUENCE_NOMBREREPETITON, String.valueOf(s.getNombreRepetition()));
                map.put(DatabaseHelper.SEQUENCE_SYNTHESEVOCALE, String.valueOf(s.getSyntheseVocale().getSyntheseVocaleForBdd()));
                int idSequence = (int) m_db.insert(DatabaseHelper.SEQUENCE, null, map);
                int position = 0;
                for (ElementSequence element : s.getTabElement()) {

                    //mise a jour des élémentSequence

                    //recherche de l'id de l'exercice avec son nom
                    Cursor cExercice = m_db.rawQuery("SELECT * FROM " + DatabaseHelper.EXERCICE + " WHERE " + DatabaseHelper.EXERCICE_NOM +
                            "='" + element.getNomExercice() + "'", null);
                    if (cExercice.getCount() != 1) {
                        //erreur l'exercice n'est pas référencé dans la table
                        Log.d("SQL", "L'exercice n'est pas référencé dans la table ");
                    } else {
                        cExercice.moveToFirst();
                        map = new ContentValues();
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_ID_EXERCICE, cExercice.getString(cExercice.getColumnIndex(DatabaseHelper.EXERCICE_ID)));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_ID_SEQUENCE, String.valueOf(idSequence));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_NOTIFICATION, String.valueOf(element.getNotificationExercice().getNotificationForBdd()));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_FICHIERAUDIONOTIFICATION, String.valueOf(element.getNotificationExercice()
                                .getFichierSonnerie()));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_SYNTHESEVOCALE, String.valueOf(element.getSyntheseVocale().getSyntheseVocaleForBdd()));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_DUREE, String.valueOf(element.getDureeExercice()));
                        map.put(DatabaseHelper.ELEMENTSEQUENCE_POSITION, String.valueOf(position));
                        m_db.insert(DatabaseHelper.ELEMENTSEQUENCE, null, map);
                    }
                    position++;
                }
            }
        }
    }

    /**
     * Récupère et renvois la librairie des sequences
     * @return
     *      Arraylist contenant les sequences récupéréss
     */
    public ArrayList<Sequence> restoreLibrairieSequences(ArrayList<Exercice> libExercices)
    {
        ArrayList<Sequence> tab = new ArrayList<>();

        //récupération de toutes les séquences de la table Sequence
        if (m_db != null) {
            Cursor c = m_db.rawQuery("SELECT * FROM " + DatabaseHelper.SEQUENCE + ";", null);
            while (c.moveToNext()) {
                int idSequence = c.getInt(c.getColumnIndex(DatabaseHelper.SEQUENCE_ID));
                String nomSequence = c.getString(c.getColumnIndex(DatabaseHelper.SEQUENCE_NOM));
                int nbreRepetition = c.getInt(c.getColumnIndex(DatabaseHelper.SEQUENCE_NOMBREREPETITON));
                int syntheseVocale = c.getInt(c.getColumnIndex(DatabaseHelper.SEQUENCE_SYNTHESEVOCALE));
                Sequence seq = new Sequence(idSequence, nomSequence, nbreRepetition, new SyntheseVocale(syntheseVocale));
                //Recherche des ElementSequence et Exercices pour la séquence en cours
                String requete = "SELECT * FROM " + DatabaseHelper.ELEMENTSEQUENCE + " INNER JOIN " + DatabaseHelper.EXERCICE + " ON " +
                        DatabaseHelper.ELEMENTSEQUENCE + "." + DatabaseHelper.ELEMENTSEQUENCE_ID_EXERCICE + " = " + DatabaseHelper.EXERCICE + "." +
                        DatabaseHelper.EXERCICE_ID + " WHERE " + DatabaseHelper.ELEMENTSEQUENCE + "." + DatabaseHelper.ELEMENTSEQUENCE_ID_SEQUENCE +
                        " = '" + String.valueOf(idSequence) + "';";
                Cursor cElement = m_db.rawQuery(requete, null);
                while (cElement.moveToNext()) {

                    String nomExercice = cElement.getString(cElement.getColumnIndex(DatabaseHelper.EXERCICE_NOM));
                    String descriptionExercice = cElement.getString(cElement.getColumnIndex(DatabaseHelper.EXERCICE_DESCRIPTION));
                    int idElement = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.ELEMENTSEQUENCE_ID));
                    int idExercice = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.EXERCICE_ID));
                    int positionElement = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.ELEMENTSEQUENCE_POSITION));
                    int syntheseVocaleElement = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.ELEMENTSEQUENCE_SYNTHESEVOCALE));
                    int dureeElement = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.ELEMENTSEQUENCE_DUREE));
                    int notificationElement = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.ELEMENTSEQUENCE_NOTIFICATION));
                    int jouerPlaylist = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.ELEMENTSEQUENCE_JOUERPLAYLIST));
                    int idMorceauSonnerie = cElement.getInt(cElement.getColumnIndex(DatabaseHelper.ELEMENTSEQUENCE_FICHIERAUDIONOTIFICATION));
                    Cursor cMorceauSonnerie = m_db.rawQuery("SELECT " + DatabaseHelper.MORCEAU_IDENTIFIANT + ", " + DatabaseHelper.MORCEAU_TITRE + "," +
                            DatabaseHelper.MORCEAU_ARTISTE + " FROM " + DatabaseHelper.MORCEAU + " WHERE " + DatabaseHelper.MORCEAU_ID + " = " +
                            String.valueOf(idMorceauSonnerie), null);
                    Morceau fichierAudio = null;
                    if (cMorceauSonnerie.getCount() == 1) {
                        cMorceauSonnerie.moveToFirst();
                        String titre = cMorceauSonnerie.getString(cMorceauSonnerie.getColumnIndex(DatabaseHelper.MORCEAU_TITRE));
                        String artiste = cMorceauSonnerie.getString(cMorceauSonnerie.getColumnIndex(DatabaseHelper.MORCEAU_ARTISTE));
                        int id = cMorceauSonnerie.getInt(cMorceauSonnerie.getColumnIndex(DatabaseHelper.MORCEAU_IDENTIFIANT));
                        fichierAudio = new Morceau(id, titre, artiste);
                    }
                    //recherche de l'exercice dans la librairie des exercices
                    Exercice exercice = null;
                    for (Exercice e : libExercices)
                    {
                        if (idExercice == e.getIdExercice()) {
                            exercice = e;
                            break;
                        }
                    }
                    if (exercice == null) {
                        Log.d("SQL", "Erreur l'exercice n'existe pas dans la librairie");
                    }
                    // récupération de la playlist de l'element
                    Playlist pl = new Playlist();
                    if (jouerPlaylist > 0)
                        pl.setJouerPlaylist(true);
                    else
                        pl.setJouerPlaylist(false);

                    requete = "SELECT " + DatabaseHelper.MORCEAU + "." + DatabaseHelper.MORCEAU_IDENTIFIANT + ", " + DatabaseHelper.MORCEAU + "." +
                            DatabaseHelper.MORCEAU_TITRE + "," + DatabaseHelper.MORCEAU + "." + DatabaseHelper.MORCEAU_ARTISTE + " FROM " + DatabaseHelper.MORCEAU +
                            " INNER JOIN " + DatabaseHelper.PLAYLIST + " ON " + DatabaseHelper.MORCEAU + "." + DatabaseHelper.MORCEAU_ID + " = " +
                            DatabaseHelper.PLAYLIST + "." + DatabaseHelper.PLAYLIST_ID_MORCEAU + " WHERE " + DatabaseHelper.PLAYLIST + "." +
                            DatabaseHelper.PLAYLIST_ID_ELEMENTSEQUENCE + " = " + String.valueOf(idElement) + " ORDER BY " + DatabaseHelper.PLAYLIST + "." +
                            DatabaseHelper.PLAYLIST_POSITION + ";";

                    Cursor cPlaylist = m_db.rawQuery(requete, null);
                    while (cPlaylist.moveToNext()) {
                        pl.ajouterMorceau(new Morceau(cPlaylist.getLong(cPlaylist.getColumnIndex(DatabaseHelper.MORCEAU_IDENTIFIANT)),
                                cPlaylist.getString(cPlaylist.getColumnIndex(DatabaseHelper.MORCEAU_TITRE)),
                                cPlaylist.getString(cPlaylist.getColumnIndex(DatabaseHelper.MORCEAU_ARTISTE))));
                    }

                    ElementSequence element = new ElementSequence(exercice.getIdExercice(), exercice.getNomExercice(), exercice.getDescriptionExercice(),
                            exercice.getDureeParDefaut(), exercice.getPlaylistParDefaut(), dureeElement, pl,
                            new NotificationExercice(notificationElement, fichierAudio), new SyntheseVocale(syntheseVocaleElement));
                    seq.ajouterElement(element);
                }
                tab.add(seq);
            }
        }
        return tab;
    }

}
