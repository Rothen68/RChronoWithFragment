package com.stephane.rothen.rchrono.model;

import java.util.ArrayList;

/**
 * Classe métier permettant de stocker les données d'une séquence
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class Sequence implements Cloneable {

    /**
     * Identifiant de la séquence dans la base de données
     */
    protected long mIdSequence;
    /**
     * Nom de la séquence
     */
    protected String mNomSequence;

    /**
     * Nombre de répétitions de la séquence :  1 = la séquence est lu une fois
     */
    protected int mNombreRepetition;

    /**
     * Synthèse vocale de la séquence
     *
     * @see SyntheseVocale
     */
    protected SyntheseVocale mSyntheseVocale;

    /**
     * tableau contenant les exercices à executer durant la séquence
     *
     * @see ElementSequence
     */
    protected ArrayList<ElementSequence> mTabElement;

    /**
     * Constructeur
     *
     * @param idSequence       identifiant de la séquence dans la base de données
     * @param nomSequence      nom de la séquence
     * @param nombreRepetition nombre de répétitions
     * @param syntheseVocale   synthèse vocale de la séquence
     */
    public Sequence(long idSequence, String nomSequence, int nombreRepetition, SyntheseVocale syntheseVocale) {
        mIdSequence = idSequence;
        this.mNomSequence = nomSequence;
        this.mNombreRepetition = nombreRepetition;
        this.mSyntheseVocale = syntheseVocale;
        mTabElement = new ArrayList<>();
    }

    /**
     * Retourne l'id de la séquence dans la base de données
     *
     * @return id de la séquence dans la base de données
     */
    public long getIdSequence() {
        return mIdSequence;
    }

    /**
     * Définit l'id de la séquence dans la base de données
     *
     * @param id id de la séquence dans la base de données
     */
    public void setIdSequence(long id) {
        mIdSequence = id;
    }

    /**
     * Retourne le nom de la séquence
     *
     * @return nom de la séquence
     */
    public String getNomSequence() {
        return mNomSequence;
    }

    /**
     * Définit le nom de la séquence
     *
     * @param nomSequence nom de la séquence
     */

    public void setNomSequence(String nomSequence) {
        this.mNomSequence = nomSequence;
    }

    /**
     * Retourne le nombre de répétitions
     *
     * @return nombre de répétitions
     */
    public int getNombreRepetition() {
        return mNombreRepetition;
    }

    /**
     * Définit le nombre de répétitions
     *
     * @param nombreRepetition nombre de répétitions
     */

    public void setNombreRepetition(int nombreRepetition) {
        this.mNombreRepetition = nombreRepetition;
    }

    /**
     * Retourne l'utilisation de la synthese vocale dans la séquence
     *
     * @return SyntheseVocale
     * @see com.stephane.rothen.rchrono.model.SyntheseVocale
     */
    public SyntheseVocale getSyntheseVocale() {
        return mSyntheseVocale;
    }


    /**
     * Définit la synthese vocale de la séquence
     *
     * @param syntheseVocale SyntheseVocale
     * @see com.stephane.rothen.rchrono.model.SyntheseVocale
     */
    public void setSyntheseVocale(SyntheseVocale syntheseVocale) {
        this.mSyntheseVocale = syntheseVocale;
    }

    /**
     * Retourne la liste des exercices de la séquence sous forme d'ElementSequence
     *
     * @return tableau d'ElementSequence
     */
    public ArrayList<ElementSequence> getTabElement() {
        return mTabElement;
    }


    /**
     * Ajoute un ElementSequence à la séquence
     *
     * @param e ElementSequence à ajouter
     * @see ElementSequence
     * @see Sequence#mTabElement
     */
    public void ajouterElement(ElementSequence e) {
        mTabElement.add(e);
    }


    /**
     * Créé un nouvel ElementSequence à partir de l'Exercice passé en paramètre et l'ajoute à la séquence
     *
     * @param e Exercice source
     */
    public void ajouterExercice(Exercice e) {
        ElementSequence el = new ElementSequence(e.getIdExercice(), -1, e.getNomExercice(), e.getDescriptionExercice(), e.getDureeParDefaut(), e.getPlaylistParDefaut(), e.getDureeParDefaut(), e.getPlaylistParDefaut(), new NotificationExercice(false, false, false, -1), new SyntheseVocale(false, false));
        mTabElement.add(el);
    }


    /**
     * Fonction qui retourne la durée d'une séquence incluant les répétitions
     *
     * @return durée de la séquence
     */
    public int getDureeSequence() {
        int duree = 0;
        for (ElementSequence e : mTabElement) {
            duree = duree + e.getDureeExercice();
        }
        duree = duree * mNombreRepetition;
        return duree;
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
        Sequence s = new Sequence(mIdSequence, new String(mNomSequence), mNombreRepetition, (SyntheseVocale) mSyntheseVocale.clone());
        s.mTabElement = (ArrayList<ElementSequence>) mTabElement.clone();
        return s;

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
        if (o instanceof Sequence) {
            Sequence s = (Sequence) o;
            boolean egale = true;
            int nbreElement = s.getTabElement().size();
            if (nbreElement != mTabElement.size()) {
                return false;
            } else {
                for (int i = 0; i < nbreElement; i++) {
                    if (!s.getTabElement().get(i).equals(mTabElement.get(i)))
                        egale = false;
                }
            }

            return (mIdSequence == s.mIdSequence && mNomSequence.equals(s.mNomSequence) && mNombreRepetition == s.mNombreRepetition && mSyntheseVocale.equals(s.mSyntheseVocale) && egale);

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
        return mNomSequence + " " + mNombreRepetition + " " + mTabElement.toString();
    }


}
