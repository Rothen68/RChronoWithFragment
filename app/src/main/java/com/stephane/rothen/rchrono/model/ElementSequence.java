package com.stephane.rothen.rchrono.model;

/**
 * Classe métier héritant d'Exercice et stockant les caractéristiques spécifiques d'un exercice
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class ElementSequence extends Exercice {

    /**
     * Duree de l'exercice
     */
    protected int m_dureeExercice;

    /**
     * Playlist de l'exercice
     *
     * @see Playlist
     */
    protected Playlist m_playlistExercice;

    /**
     * Nofitications de l'exercice
     *
     * @see NotificationExercice
     */
    protected NotificationExercice m_notificationExercice;

    /**
     * Synthèse vocale de l'exercice
     *
     * @see SyntheseVocale
     */
    protected SyntheseVocale m_syntheseVocale;


    /**
     * Constructeur
     *
     * @param nomExercice
     * @param descriptionExercice
     * @param dureeParDefaut
     * @param playlistParDefaut
     * @param dureeExercice
     * @param playlistExercice
     * @param notificationExercice
     * @param syntheseVocale
     */
    public ElementSequence(String nomExercice, String descriptionExercice, int dureeParDefaut, Playlist playlistParDefaut, int dureeExercice, Playlist playlistExercice, NotificationExercice notificationExercice, SyntheseVocale syntheseVocale) {
        super(nomExercice, descriptionExercice, dureeParDefaut, playlistParDefaut);
        this.m_dureeExercice = dureeExercice;
        this.m_playlistExercice = playlistExercice;
        this.m_notificationExercice = notificationExercice;
        this.m_syntheseVocale = syntheseVocale;
    }


    public int getDureeExercice() {
        return m_dureeExercice;
    }

    public void setDureeExercice(int duree) {
        if (duree > 0) {
            m_dureeExercice = duree;
        }
    }

    public SyntheseVocale getSyntheseVocale() {
        return m_syntheseVocale;
    }

    public NotificationExercice getNotification() {
        return m_notificationExercice;
    }

    public Exercice getExercice() {
        Exercice e = new Exercice(getNomExercice(), getDescriptionExercice(), getDureeParDefaut(), getPlaylistParDefaut());
        return e;
    }

    public ElementSequence getClone() {
        return new ElementSequence(m_nomExercice, m_descriptionExercice, m_dureeParDefaut, m_playlistParDefaut.getClone(), m_dureeExercice, m_playlistExercice.getClone(), m_notificationExercice.getClone(), m_syntheseVocale.getClone());
    }

    public boolean egale(ElementSequence e) {
        if (m_nomExercice.equals(e.m_nomExercice) && m_descriptionExercice.equals(e.m_descriptionExercice) &&
                m_dureeParDefaut == e.m_dureeParDefaut && m_dureeExercice == e.m_dureeExercice &&
                m_playlistParDefaut.egale(e.m_playlistParDefaut) && m_playlistExercice.egale(m_playlistExercice) &&
                m_notificationExercice.egale(e.m_notificationExercice) && m_syntheseVocale.egale(e.m_syntheseVocale))
            return true;
        else
            return false;

    }

}
