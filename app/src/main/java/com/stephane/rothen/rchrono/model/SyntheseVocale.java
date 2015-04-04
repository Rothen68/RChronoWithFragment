package com.stephane.rothen.rchrono.model;

/**
 * SyntheseVocale est la classe métier gérant la synthese vocale
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class SyntheseVocale implements Cloneable {
    /**
     * Valeur à stocker dans la base de données pour représenter la synthese vocale du nom
     */
    private static final int NOM = 0x01;
    /**
     * Valeur à stocker dans la base de données pour représenter la synthese vocale de la durée
     */
    private static final int DUREE = 0x02;

    /**
     * Le nom est dit par la synthèse vocale
     */
    protected boolean mNom = false;
    /**
     * La durée est ennoncée par la synthèse vocale
     */
    protected boolean mDuree = false;


    /**
     * initialise l'objet depuis un entier contenu dans la base de données
     *
     * @param syntheseVocaleFromBdd : valeur lut dans la base de données
     */
    public SyntheseVocale(int syntheseVocaleFromBdd) {
        if ((syntheseVocaleFromBdd & NOM) != 0) {
            mNom = true;
        } else {
            mNom = false;
        }
        if ((syntheseVocaleFromBdd & DUREE) != 0) {
            mDuree = true;
        } else {
            mDuree = false;
        }
    }


    /**
     * Constructeur
     *
     * @param nom   Ennonce ou non le nom
     * @param duree Ennonce ou non la duree
     */
    public SyntheseVocale(boolean nom, boolean duree) {
        mDuree = duree;
        mNom = nom;
    }

    /**
     * Renvois un entier pour stocker l'information de synthese vocale dans la base de données
     *
     * @return valeur à stocker dans la base de données
     */
    public int getSyntheseVocaleForBdd() {
        int r = ((mNom) ? (NOM) : (0));
        r = r + ((mDuree) ? (DUREE) : (0));
        return r;
    }

    /**
     * Renvois l'utilisation du nom
     *
     * @return utilisation du nom
     */
    public boolean getNom() {
        return mNom;
    }

    /**
     * Définit l'utilisation du nom
     *
     * @param nom utilisation du nom
     */
    public void setNom(boolean nom) {
        mNom = nom;
    }

    /**
     * Renvois l'utilisation du duree
     *
     * @return utilisation de la duree
     */
    public boolean getDuree() {
        return mDuree;
    }

    /**
     * Définit l'utilisation du duree
     *
     * @param duree utilisation de la duree
     */
    public void setDuree(boolean duree) {
        mDuree = duree;
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
        return new SyntheseVocale(mNom, mDuree);
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
        if (o instanceof SyntheseVocale) {
            SyntheseVocale s = (SyntheseVocale) o;
            return (mNom == s.mNom && mDuree == s.mDuree);
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
        return mNom + " " + mDuree;
    }
}
