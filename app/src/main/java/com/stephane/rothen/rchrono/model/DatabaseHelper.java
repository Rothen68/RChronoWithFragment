package com.stephane.rothen.rchrono.model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe controleur gérant la création, la mise à jour et la connexion à base de donnée interne
 *
 * Created by Stéphane on 14/02/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "RCHRONO.db";
    public static final int DB_VERSION = 4;

    /*
    Historique des versions de la base de donnée
    1: 14/02/15 : Stéphane : architecture de base
    2: 14/02/15 : Stéphane : Ajout d'une table playlist permettant d'allouer une playlist par défaut à un exercice en plus de celle d'un ElementSequence
    3: 17/02/15 : Stéphane : Ajout d'un element JouerPlaylist dans la table exercice
    4: 18/02/15 : Stéphane : Remplacement du champ Chemin de la table Morceau par le champs Identifiant et ajout de l'artiste et changement du type FichierAudioNotificaiton dans ElementSequence



     */

    /**
     * Table Exercice
     */
    public static final String EXERCICE_ID = "ID_EXERCICE";
    public static final String EXERCICE_NOM = "NOM";
    public static final String EXERCICE_DESCRIPTION = "DESCRIPTION";
    public static final String EXERCICE_DUREEPARDEFAUT = "DUREEPARDEFAUT";
    public static final String EXERCICE = "EXERCICE";
    public static final String EXERCICE_JOUERPLAYLISTPARDEFAUT="JOUERPLAYLISTPARDEFAUT";
    public static final String EXERCICE_TABLE_CREATE = "CREATE TABLE " + EXERCICE + " (" + EXERCICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                            EXERCICE_NOM + " TEXT, "+
                                                                                            EXERCICE_DESCRIPTION + " TEXT, "+
                                                                                            EXERCICE_DUREEPARDEFAUT + " INTEGER, " +
                                                                                            EXERCICE_JOUERPLAYLISTPARDEFAUT  + " INTEGER );";
    public static final String EXERCICE_TABLE_DROP="DROP TABLE IF EXISTS " + EXERCICE+";";

    /**
     * Table Sequence
     */
    public static final String SEQUENCE_ID = "ID_SEQUENCE";
    public static final String SEQUENCE_NOM = "NOM";
    public static final String SEQUENCE_NOMBREREPETITON = "NOMBREREPETITON";
    public static final String SEQUENCE_SYNTHESEVOCALE = "SYNTHESEVOCALE";
    public static final String SEQUENCE= "SEQUENCE";
    public static final String SEQUENCE_TABLE_CREATE = "CREATE TABLE " + SEQUENCE + "(" + SEQUENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                            SEQUENCE_NOM+ " TEXT, "+
                                                                                            SEQUENCE_NOMBREREPETITON + " INTEGER,"+
                                                                                            SEQUENCE_SYNTHESEVOCALE + " INTEGER);";
    public static final String SEQUENCE_TABLE_DROP="DROP TABLE IF EXISTS " + SEQUENCE+";";

    /**
     * Table ListeSequence
     */
    public static final String LSTSEQUENCES = "LISTESEQUENCE";
    public static final String LSTSEQUENCES_ID_LISTEQUENCES = "ID_LISTESEQUENCE";
    public static final String LSTSEQUENCES_ID_SEQUENCE = "ID_SEQUENCE";
    public static final String LSTSEQUENCES_TABLE_CREATE =  "CREATE TABLE " + LSTSEQUENCES + "(" + LSTSEQUENCES_ID_LISTEQUENCES + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                                    LSTSEQUENCES_ID_SEQUENCE + " INTEGER);";
    public static final String LSTSEQUENCES_TABLE_DROP="DROP TABLE IF EXISTS " + LSTSEQUENCES+";";

    /**
     * Table Morceau
     */
    public static final String MORCEAU = "MORCEAU";
    public static final String MORCEAU_ID = "ID_MORCEAU";
    public static final String MORCEAU_TITRE = "TITRE";
    public static final String MORCEAU_ARTISTE = "ARTISTE";
    public static final String MORCEAU_IDENTIFIANT = "IDENTIFIANT";
    public static final String MORCEAU_TABLE_CREATE = "CREATE TABLE "+ MORCEAU + "(" + MORCEAU_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                        MORCEAU_TITRE + " TEXT, "+
                                                                                        MORCEAU_ARTISTE + " TEXT, " +
                                                                                        MORCEAU_IDENTIFIANT + " INTEGER); ";
    public static final String MORCEAU_TABLE_DROP="DROP TABLE IF EXISTS " + MORCEAU+";";

    /**
     * Table Playlist
     * Fait le lien entre les morceaux et un exercice ou un ElementSequence.
     * Si une playlist existe pour un exercice et pour l'ElementSequence qui en hérite, c'est la playlist de l'ElementSequence qui est pris en compte
     */
    public static final String PLAYLIST = "PLAYLIST";
    public static final String PLAYLIST_ID = "ID_PLAYLIST";
    public static final String PLAYLIST_ID_MORCEAU="ID_MORCEAU";
    public static final String PLAYLIST_ID_EXERCICE="ID_EXERCICE";
    public static final String PLAYLIST_ID_ELEMENTSEQUENCE="ID_ELEMENTSEQUENCE";
    public static final String PLAYLIST_POSITION="POSITION";
    public static final String PLAYLIST_TABLE_CREATE="CREATE TABLE " + PLAYLIST + "(" + PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                        PLAYLIST_ID_MORCEAU+" INTEGER,"+
                                                                                        PLAYLIST_ID_EXERCICE+" INTEGER,"+
                                                                                        PLAYLIST_ID_ELEMENTSEQUENCE+" INTEGER,"+
                                                                                        PLAYLIST_POSITION + " INTEGER);";
    public static final String PLAYLIST_TABLE_DROP="DROP TABLE IF EXISTS " + PLAYLIST+";";

    /**
     * Table ElementSequence
     */

    public static final String ELEMENTSEQUENCE = "ELEMENTSEQUENCE";
    public static final String ELEMENTSEQUENCE_ID = "ID_ELEMENTSEQUENCE";
    public static final String ELEMENTSEQUENCE_ID_EXERCICE = "ID_EXERCICE";
    public static final String ELEMENTSEQUENCE_ID_SEQUENCE = "ID_SEQUENCE";
    public static final String ELEMENTSEQUENCE_POSITION = "POSITION";
    public static final String ELEMENTSEQUENCE_SYNTHESEVOCALE ="SYNTHESEVOCALE";
    public static final String ELEMENTSEQUENCE_DUREE ="DUREE";
    public static final String ELEMENTSEQUENCE_JOUERPLAYLIST="JOUERPLAYLIST";
    public static final String ELEMENTSEQUENCE_NOTIFICATION ="NOTIFICATION";
    public static final String ELEMENTSEQUENCE_FICHIERAUDIONOTIFICATION ="FICHIERAUDIONOTIFICATION";
    public static final String ELEMENTSEQUENCE_TABLE_CREATE ="CREATE TABLE " + ELEMENTSEQUENCE + "(" + ELEMENTSEQUENCE_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                                        ELEMENTSEQUENCE_ID_EXERCICE + " INTEGER,"+
                                                                                                        ELEMENTSEQUENCE_ID_SEQUENCE + " INTEGER,"+
                                                                                                        ELEMENTSEQUENCE_POSITION + " INTEGER,"+
                                                                                                        ELEMENTSEQUENCE_SYNTHESEVOCALE + " INTEGER,"+
                                                                                                        ELEMENTSEQUENCE_DUREE + " INTEGER,"+
                                                                                                        ELEMENTSEQUENCE_JOUERPLAYLIST + " INTEGER, " +
                                                                                                        ELEMENTSEQUENCE_NOTIFICATION + " INTEGER,"+
                                                                                                        ELEMENTSEQUENCE_FICHIERAUDIONOTIFICATION + " INT);";
    public static final String ELEMENTSEQUENCE_TABLE_DROP="DROP TABLE IF EXISTS " + ELEMENTSEQUENCE+";";









    public DatabaseHelper(Context context)
    {
        super (context,DB_NAME,null,DB_VERSION);

    }



    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(EXERCICE_TABLE_CREATE);
        }catch(SQLiteException e){
            Log.d("SQL", "Erreur exercice :" + e.toString());
        }
        try {
            db.execSQL(SEQUENCE_TABLE_CREATE);
        }catch(SQLiteException e){
            Log.d("SQL", "Erreur sequence :" + e.toString());
        }
        try {
            db.execSQL(LSTSEQUENCES_TABLE_CREATE);
        }catch(SQLiteException e){
            Log.d("SQL", "Erreur ListeSequence :" + e.toString());
        }
        try {
            db.execSQL(MORCEAU_TABLE_CREATE);
        }catch(SQLiteException e){
            Log.d("SQL", "Erreur morceau :" + e.toString());
        }
        try {
            db.execSQL(ELEMENTSEQUENCE_TABLE_CREATE);
        }catch(SQLiteException e){
            Log.d("SQL", "Erreur ElementSequence :" + e.toString());
        }
        try {
            db.execSQL(PLAYLIST_TABLE_CREATE);
        }catch(SQLiteException e){
            Log.d("SQL", "Erreur Playlist :" + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(EXERCICE_TABLE_DROP);
        db.execSQL(SEQUENCE_TABLE_DROP);
        db.execSQL(ELEMENTSEQUENCE_TABLE_DROP);
        db.execSQL(LSTSEQUENCES_TABLE_DROP);
        db.execSQL(MORCEAU_TABLE_DROP);
        db.execSQL(PLAYLIST_TABLE_DROP);
        onCreate(db);
    }
}
