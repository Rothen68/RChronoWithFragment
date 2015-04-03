package com.stephane.rothen.rchrono.model;

/**
 * Classe métier permettant de stocker les caractéristiques de base d'un exercice
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class Exercice implements Cloneable {

    protected int mIdExercice;
    /**
     * Nom de l'exercice
     */
    protected String mNomExercice;
    /**
     * Description de l'exercice
     */
    protected String mDescriptionExercice;
    /**
     * Durée par défaut de l'exercice
     */
    protected int mDureeParDefaut;

    /**
     * Playlist par défaut de l'exercice
     *
     * @see Playlist
     */
    protected Playlist mPlaylistParDefaut;

    /**
     * Constructeur
     *
     * @param nomExercice
     * @param descriptionExercice
     * @param dureeParDefaut
     * @param playlistParDefaut
     */
    public Exercice(int idExercice, String nomExercice, String descriptionExercice, int dureeParDefaut, Playlist playlistParDefaut) {
        this.mIdExercice = idExercice;
        this.mNomExercice = nomExercice;
        this.mDescriptionExercice = descriptionExercice;
        this.mDureeParDefaut = dureeParDefaut;
        this.mPlaylistParDefaut = playlistParDefaut;

    }

    public int getIdExercice() {
        return mIdExercice;
    }

    public void setIdExercice(int id) {
        mIdExercice = id;
    }

    /**
     * Renvois le nom de l'exercice
     *
     * @return nom de l'exercice
     */
    public String getNomExercice() {
        return mNomExercice;
    }

    /**
     * Définit le nom de l'exercice
     *
     * @param nom nom de l'exercice
     */
    public void setNomExercice(String nom) {
        mNomExercice = nom;
    }

    /**
     * Renvois la description de l'exercice
     *
     * @return description de l'exercice
     */
    public String getDescriptionExercice() {
        return mDescriptionExercice;
    }

    /**
     * Définit la description de l'exercice
     *
     * @param description description de l'exercice
     */
    public void setDescriptionExercice(String description) {
        mDescriptionExercice = description;
    }

    /**
     * Renvois la durée par défaut de l'exercice
     *
     * @return durée par défaut de l'exercice
     */
    public int getDureeParDefaut() {
        return mDureeParDefaut;
    }

    /**
     * Définit la durée par défaut de l'exercice
     *
     * @param dureeParDefaut durée par défaut de l'exercice
     */
    public void setDureeParDefaut(int dureeParDefaut) {
        mDureeParDefaut = dureeParDefaut;
    }


    /**
     * Renvois la playlist de l'exercice
     *
     * @return playlist de l'exercice
     */
    public Playlist getPlaylistParDefaut() {
        return mPlaylistParDefaut;
    }

    /**
     * Définit la playlist par défaut
     *
     * @param playlistParDefaut playlist par défaut
     */
    public void setPlaylistParDefaut(Playlist playlistParDefaut) {
        mPlaylistParDefaut = playlistParDefaut;
    }


    /**
     * Creates and returns a copy of this {@code Object}. The default
     * implementation returns a so-called "shallow" copy: It creates a new
     * instance of the same class and then copies the field values (including
     * object references) from this instance to the new instance. A "deep" copy,
     * in contrast, would also recursively clone nested objects. A subclass that
     * needs to implement this kind of cloning should call {@code super.clone()}
     * to create the new instance and then create deep copies of the nested,
     * mutable objects.
     *
     * @return a copy of this object.
     */
    @Override
    protected Object clone() {
        return new Exercice(mIdExercice, new String(mNomExercice), new String(mDescriptionExercice), mDureeParDefaut, (Playlist) mPlaylistParDefaut.clone());
    }

    /**
     * Compares this instance with the specified object and indicates if they
     * are equal. In order to be equal, {@code o} must represent the same object
     * as this instance using a class-specific comparison. The general contract
     * is that this comparison should be reflexive, symmetric, and transitive.
     * Also, no object reference other than null is equal to null.
     * <p/>
     * <p>The default implementation returns {@code true} only if {@code this ==
     * o}. See <a href="{@docRoot}reference/java/lang/Object.html#writing_equals">Writing a correct
     * {@code equals} method</a>
     * if you intend implementing your own {@code equals} method.
     * <p/>
     * <p>The general contract for the {@code equals} and {@link
     * #hashCode()} methods is that if {@code equals} returns {@code true} for
     * any two objects, then {@code hashCode()} must return the same value for
     * these objects. This means that subclasses of {@code Object} usually
     * override either both methods or neither of them.
     *
     * @param o the object to compare this instance with.
     * @return {@code true} if the specified object is equal to this {@code
     * Object}; {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Exercice) {
            Exercice e = (Exercice) o;
            return (mIdExercice == e.mIdExercice && mNomExercice.equals(e.mNomExercice) && mDescriptionExercice.equals(e.mDescriptionExercice) && mDureeParDefaut == e.mDureeParDefaut && mPlaylistParDefaut.equals(e.mPlaylistParDefaut));
        }
        return false;
    }
}
