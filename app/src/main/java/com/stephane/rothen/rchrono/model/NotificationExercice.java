package com.stephane.rothen.rchrono.model;

/**
 * Notification est la classe métier gérant les notifications pour un exercice
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class NotificationExercice implements Cloneable {
    private static final int VIBREUR = 0x01;
    private static final int POPUP = 0x02;
    private static final int SONNERIE = 0x04;

    /**
     * La notification est du type vibreur
     */
    protected boolean mVibreur;
    /**
     * La notification est du type Popup
     */
    protected boolean mPopup;
    /**
     * La notification est du type sonnerie
     */
    protected boolean mSonnerie;
    /**
     * Chemin vers le fichier de sonnerie si la notification est du type sonnerie
     *
     * @see NotificationExercice#mSonnerie
     */
    protected Morceau mFichierSonnerie;

    /**
     * Constructeur
     *
     * @param notificationFromBdd entier stockant l'état de la notification stocké dans la base de donnée
     * @param fichierSonnerie     entier
     */
    public NotificationExercice(int notificationFromBdd, Morceau fichierSonnerie) {


        if ((notificationFromBdd & VIBREUR) > 0) {
            mVibreur = true;
        } else {
            mVibreur = false;
        }

        if ((notificationFromBdd & POPUP) > 0) {
            mPopup = true;
        } else {
            mPopup = false;
        }

        if ((notificationFromBdd & SONNERIE) > 0) {
            mSonnerie = true;
        } else {
            mSonnerie = false;
        }

        mFichierSonnerie = fichierSonnerie;
    }

    /**
     * Constructeur
     *
     * @param vibreur
     * @param popup
     * @param sonnerie
     * @param fichierSonnerie
     */
    public NotificationExercice(boolean vibreur, boolean popup, boolean sonnerie, Morceau fichierSonnerie) {
        mVibreur = vibreur;
        mPopup = popup;
        mSonnerie = sonnerie;
        mFichierSonnerie = fichierSonnerie;
    }

    /**
     * Renvois la valeur à stocker dans la base de données sous forme d'entier
     *
     * @return valeur à stocker dans la base de données
     */
    public int getNotificationForBdd() {
        int r = 0;
        r = r + ((mVibreur) ? (VIBREUR) : (0));
        r = r + ((mPopup) ? (POPUP) : (0));
        r = r + ((mSonnerie) ? (SONNERIE) : (0));
        return r;
    }

    /**
     * Renvois l'utilisation du vibreur
     *
     * @return utilisation du vibreur
     */
    public boolean getVibreur() {
        return mVibreur;
    }

    /**
     * Renvois l'utilisation de la sonnerie
     *
     * @return utilisation de la sonnerie
     */
    public boolean getSonnerie() {
        return mSonnerie;
    }

    /**
     * Renvois l'utilisation d'une popup
     *
     * @return utilisation d'une popup
     */
    public boolean getPopup() {
        return mPopup;
    }

    /**
     * Renvois la sonnerie à jouer
     *
     * @return morceau à jouer comme sonnerie
     */
    public Morceau getFichierSonnerie() {
        return mFichierSonnerie;
    }

    public void setFichierSonnerie(Morceau m) {
        mFichierSonnerie = m;
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
        if (mFichierSonnerie != null)
            return new NotificationExercice(mVibreur, mPopup, mSonnerie, (Morceau) mFichierSonnerie.clone());
        else
            return new NotificationExercice(mVibreur, mPopup, mSonnerie, null);
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
        if (o instanceof NotificationExercice) {
            NotificationExercice n = (NotificationExercice) o;
            if (mFichierSonnerie != null && n.mFichierSonnerie != null)
                return (mPopup == n.mPopup && mFichierSonnerie.equals(n.mFichierSonnerie) && mSonnerie == n.mSonnerie && mVibreur == n.mVibreur);
            else
                return (mPopup == n.mPopup && mFichierSonnerie == null && n.mFichierSonnerie == null && mSonnerie == n.mSonnerie && mVibreur == n.mVibreur);
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
        String retour = new String();
        retour = mPopup + " " + mVibreur + " " + mSonnerie + " " + mFichierSonnerie.toString();
        return retour;
    }
}
