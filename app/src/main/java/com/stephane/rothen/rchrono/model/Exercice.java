package com.stephane.rothen.rchrono.model;

/**
 * Classe métier permettant de stocker les caractéristiques de base d'un exercice
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class Exercice {
    /**
     * Nom de l'exercice
     */
    protected String m_nomExercice;
    /**
     * Description de l'exercice
     */
    protected String m_descriptionExercice;
    /**
     * Durée par défaut de l'exercice
     */
    protected int m_dureeParDefaut;

    /**
     * Playlist par défaut de l'exercice
     *
     * @see Playlist
     */
    protected Playlist m_playlistParDefaut;


    public Exercice(String nomExercice, String descriptionExercice, int dureeParDefaut, Playlist playlistParDefaut) {
        this.m_nomExercice = nomExercice;
        this.m_descriptionExercice = descriptionExercice;
        this.m_dureeParDefaut = dureeParDefaut;
        this.m_playlistParDefaut = playlistParDefaut;

    }

    public String getNomExercice() {
        return m_nomExercice;
    }

    public String getDescriptionExercice() {
        return m_descriptionExercice;
    }

    public int getDureeParDefaut() {
        return m_dureeParDefaut;
    }

    public Playlist getPlaylistParDefaut() {
        return m_playlistParDefaut;
    }

    public Exercice getClone() {
        return new Exercice(m_nomExercice, m_descriptionExercice, m_dureeParDefaut, m_playlistParDefaut.getClone());
    }

    public boolean egale(Exercice e) {
        if (m_nomExercice.equals(e.m_nomExercice) && m_descriptionExercice.equals(e.m_descriptionExercice) && m_dureeParDefaut == e.m_dureeParDefaut && m_playlistParDefaut.egale(e.m_playlistParDefaut))
            return true;
        else
            return false;
    }
}
