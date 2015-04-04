package com.stephane.rothen.rchrono.model;

/**
 * Classe métier héritant d'Exercice et stockant les caractéristiques spécifiques d'un exercice
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class ElementSequence extends Exercice implements Cloneable {

    /**
     * Identifiant de l'élément dans la base de données de l'application
     */
    protected long mIdElementSequence;
    /**
     * Duree de l'exercice
     */
    protected int mDureeExercice;

    /**
     * Playlist de l'exercice
     *
     * @see Playlist
     */
//    protected Playlist mPlaylistExercice;

    /**
     * Nofitications de l'exercice
     *
     * @see NotificationExercice
     */
    protected NotificationExercice mNotificationExercice;

    /**
     * Synthèse vocale de l'exercice
     *
     * @see SyntheseVocale
     */
    protected SyntheseVocale mSyntheseVocale;


    /**
     * Constructeur
     *
     * @param idExercice    identifiant de l'exercice
     * @param idElementSequence identifiant de l'élément
     * @param nomExercice   nom de l'exercice
     * @param descriptionExercice   description de l'exercice
     * @param dureeParDefaut    durée par défaut
     * @param playlistParDefaut playlist par défaut
     * @param dureeExercice     durée de l'exercice
     * @param playlistExercice  playlist de l'exercice
     * @param notificationExercice  notification de l'exercice
     * @param syntheseVocale    synthèse vocale de l'exercice
     */
    public ElementSequence(long idExercice, long idElementSequence, String nomExercice, String descriptionExercice, int dureeParDefaut, Playlist playlistParDefaut, int dureeExercice, Playlist playlistExercice, NotificationExercice notificationExercice, SyntheseVocale syntheseVocale) {
        super(idExercice, nomExercice, descriptionExercice, dureeParDefaut, playlistParDefaut);
        this.mIdElementSequence = idElementSequence;
        this.mDureeExercice = dureeExercice;
//        this.mPlaylistExercice = playlistExercice;
        this.mNotificationExercice = notificationExercice;
        this.mSyntheseVocale = syntheseVocale;
    }

    /**
     * Retourne l'id de l'élément
     * @return identifiant de l'élément
     */
    public long getIdElementSequence() {
        return mIdElementSequence;
    }

    /**
     * Définit l'id de l'élément
     *
     * @param id identifiant de l'élément
     */
    public void setIdElementSequence(long id) {
        mIdElementSequence = id;
    }

    /**
     * Renvois la durée de l'exercice
     *
     * @return durée de l'exercice
     */
    public int getDureeExercice() {
        return mDureeExercice;
    }

    /**
     * Définit la durée de l'exercice
     *
     * @param duree durée de l'exercice
     */
    public void setDureeExercice(int duree) {
        if (duree > 0) {
            mDureeExercice = duree;
        }
    }


//    public Playlist getPlaylistExercice() {
//        return mPlaylistExercice;
//    }
//
//
//    public void setPlaylistExercice(Playlist pl) {
//        mPlaylistExercice = pl;
//    }

    /**
     * Retourne la synthese vocale de l'exercice
     *
     * @return SyntheseVocale
     */
    public SyntheseVocale getSyntheseVocale() {
        return mSyntheseVocale;
    }

    /**
     * Définit la synthese vocale de l'exercice
     *
     * @param s SyntheseVocale de l'exercice
     */
    public void setSyntheseVocale(SyntheseVocale s) {
        mSyntheseVocale = s;
    }


    /**
     * Renvois la notification de l'exercice
     *
     * @return NotificationExercice de l'exercice
     */
    public NotificationExercice getNotificationExercice() {
        return mNotificationExercice;
    }

    /**
     * Définit la notification de l'exercice
     *
     * @param n NotificationExercice
     */
    public void setNotificationExercice(NotificationExercice n) {
        mNotificationExercice = n;
    }

    /**
     * Retourne l'exercice de l' ElementSequence
     *
     * @return Exercice
     */
    public Exercice getExercice() {
        Exercice e = new Exercice(getIdExercice(), getNomExercice(), getDescriptionExercice(), getDureeExercice(), getPlaylistParDefaut());
        return e;
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
    public Object clone() {
        return new ElementSequence(mIdExercice, mIdElementSequence, new String(mNomExercice), new String(mDescriptionExercice), mDureeParDefaut, (Playlist) mPlaylistParDefaut.clone(), mDureeExercice, /*(Playlist) mPlaylistExercice.clone()*/ null, (NotificationExercice) mNotificationExercice.clone(), (SyntheseVocale) mSyntheseVocale.clone());
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
        if (o instanceof ElementSequence) {
            ElementSequence e = (ElementSequence) o;
            return (mNomExercice.equals(e.mNomExercice) && mDescriptionExercice.equals(e.mDescriptionExercice) &&
                    mDureeParDefaut == e.mDureeParDefaut && mDureeExercice == e.mDureeExercice &&
                    mPlaylistParDefaut.equals(e.mPlaylistParDefaut) &&/* mPlaylistExercice.equals(e.mPlaylistExercice) &&*/
                    mNotificationExercice.equals(e.mNotificationExercice) && mSyntheseVocale.equals(e.mSyntheseVocale));
        }
        return false;
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * object. Subclasses are encouraged to override this method and provide an
     * implementation that takes into account the object's type and data. The
     * default implementation is equivalent to the following expression:
     * <pre>
     *   getClass().getName() + '@' + Integer.toHexString(hashCode())</pre>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_toString">Writing a useful
     * {@code toString} method</a>
     * if you intend implementing your own {@code toString} method.
     *
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return mNomExercice + " " + mDescriptionExercice + " " + mDureeExercice + " ";
    }


}
