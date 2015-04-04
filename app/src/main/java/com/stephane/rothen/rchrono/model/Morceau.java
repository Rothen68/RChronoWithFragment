package com.stephane.rothen.rchrono.model;

/**
 * Classe métier permettant de stocker un morceau de musique
 * Created by stéphane on 18/02/2015.
 */
public class Morceau implements Cloneable {

    /**
     * Identifiant du morceau dans la base de donnée du programme
     */
    protected long mIdMorceau;
    /**
     * Stocke l'identifiant de la musique de la base de donnée globale du téléphone
     */
    protected long mIdMorceauDansTelephone;
    /**
     * Stocke le titre de la musique
     */
    protected String mTitre;
    /**
     * Stocke l'artiste de la musique si il y en a un
     */
    protected String mArtiste;

    /**
     * Stocke le nombre d'utilisations du morceau dans l'application
     */
    protected int mNbreUtilisation;

    /**
     * Constructeur
     *
     * @param idMorceau     id du morceau dans la base de données de l'application
     * @param idMorceauDansTelephone    id du morceau dans la base de données du téléphone
     * @param titre     titre du morceau
     * @param artiste   artiste du morceau
     */
    public Morceau(long idMorceau, long idMorceauDansTelephone, String titre, String artiste) {
        mIdMorceau = idMorceau;
        mIdMorceauDansTelephone = idMorceauDansTelephone;
        mTitre = titre;
        mArtiste = artiste;
        mNbreUtilisation = 0;
    }

    /**
     * Retourne l'id du morceau dans la base de données de l'application
     * @return id du morceau
     */
    public long getIdMorceau() {
        return mIdMorceau;
    }

    /**
     * Définit l'id du morceau dans la base de données de l'application
     *
     * @param id id du morceau
     */
    public void setIdMorceau(long id) {
        mIdMorceau = id;
    }


    /**
     * Ajoute une utilisation au morceau
     */
    public void ajouteUtilisation() {
        mNbreUtilisation++;
    }

    /**
     * Enlève une utilisation au morceau et renvois le nombre d'utilisations restantes
     *
     * @return nombre d'utilisations restantes
     */
    public int enleveUtilisation() {
        mNbreUtilisation--;
        return mNbreUtilisation;
    }

    /**
     * Retourne l'Id du morceau
     *
     * @return Id du morceau
     * @see com.stephane.rothen.rchrono.model.Morceau#mIdMorceauDansTelephone
     */
    public long getIdMorceauDansTelephone() {
        return mIdMorceauDansTelephone;
    }

    /**
     * Définit l'Id du morceau
     *
     * @param id Id du morceau
     * @see com.stephane.rothen.rchrono.model.Morceau#mIdMorceauDansTelephone
     */
    public void setIdMorceauDansTelephone(long id) {
        mIdMorceauDansTelephone = id;
    }

    /**
     * Retourne le titre du morceau
     *
     * @return titre du morceau
     * @see com.stephane.rothen.rchrono.model.Morceau#mTitre
     */
    public String getTitre() {
        return mTitre;
    }

    /**
     * Définit le titre du morceau
     *
     * @param titre titre du morceau
     * @see com.stephane.rothen.rchrono.model.Morceau#mTitre
     */
    public void setTitre(String titre) {
        mTitre = titre;
    }

    /**
     * Retourne l'artiste
     *
     * @return artiste
     * @see com.stephane.rothen.rchrono.model.Morceau#mArtiste
     */
    public String getArtiste() {
        return mArtiste;
    }

    /**
     * Définit l'artiste
     *
     * @param artiste artiste
     * @see com.stephane.rothen.rchrono.model.Morceau#mArtiste
     */
    public void setArtiste(String artiste) {
        mArtiste = artiste;
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
        return new Morceau(mIdMorceau, mIdMorceauDansTelephone, mTitre, mArtiste);
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
        if (o instanceof Morceau) {
            Morceau m = (Morceau) o;
            return (mIdMorceau == m.mIdMorceau && mArtiste.equals(m.mArtiste) && mIdMorceauDansTelephone == m.mIdMorceauDansTelephone && mTitre.equals(m.mTitre));
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
        return mTitre + " " + mArtiste + " " + mIdMorceauDansTelephone + ";";
    }
}
