package com.stephane.rothen.rchrono.model;

import java.util.ArrayList;

/**
 * Classe métier permettant de stocker les données de la playlist
 * <p/>
 * Created by Stéphane on 14/02/2015.
 */
public class Playlist implements Cloneable {
    /**
     * Tableau contenant la liste des index des morceaux à jouer dans la playlist
     */
    protected ArrayList<Long> mListeMorceaux;
    /**
     * La playlist est jouée pendant l'exercice
     */
    protected boolean mJouerPlaylist;


    /**
     * Constructeur
     */
    public Playlist() {
        mListeMorceaux = new ArrayList<>();
    }


    /**
     * Retourne l'état de la donnée membre jouerPlaylist sous la forme d'un entier
     *
     * @return état de jouerPlaylist
     * @see com.stephane.rothen.rchrono.model.Playlist#mJouerPlaylist
     */
    public boolean getJouerPlaylist() {
        return mJouerPlaylist;
    }


    /**
     * Définit si la Playlist doit être jouée ou pas
     *
     * @param b Etat de la playlist
     */
    public void setJouerPlaylist(boolean b) {
        mJouerPlaylist = b;
    }

    /**
     * Ajoute le morceau dont le chemin est passé en paramètre
     *
     * @param indexMorceau index du morceau a ajouter
     */
    public void ajouterMorceau(long indexMorceau) {
        mListeMorceaux.add(indexMorceau);
    }


    /**
     * Supprime le morceau dont l'index est passé en paramètre
     *
     * @param index index du morceau à supprimer
     * @return true si le morceau a été supprimé
     * false si le morceau n'a pas été supprimé
     */
    public boolean supprimerMorceau(int index) {
        if (index >= 0 && index < mListeMorceaux.size()) {
            mListeMorceaux.remove(index);
            return true;
        }
        return false;
    }


    /**
     * Renvois le morceau dont la position est passée en paramètre
     *
     * @param position Position du morceau dans la playlist
     * @return Uri du morceau
     */
    public long getMorceauAt(int position) {
        if (position >= 0 && position < mListeMorceaux.size())
            return mListeMorceaux.get(position);
        else return -1;
    }

    /**
     * Renvois le nombre de morceaux dans la playlist
     *
     * @return Nombre de morceaux
     */
    public int getNbreMorceaux() {
        return mListeMorceaux.size();
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
        Playlist pl = new Playlist();
        pl.setJouerPlaylist(mJouerPlaylist);
        pl.mListeMorceaux = (ArrayList<Long>) mListeMorceaux.clone();
        return pl;
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
        if (o instanceof Playlist) {
            Playlist p = (Playlist) o;
            boolean egale = true;
            int nbreMorceaux = ((Playlist) o).getNbreMorceaux();
            if (nbreMorceaux != mListeMorceaux.size()) {
                return false;
            } else {
                for (int i = 0; i < nbreMorceaux; i++) {
                    if (!(((Playlist) o).getMorceauAt(i) == mListeMorceaux.get(i)))
                        egale = false;
                }
            }

            return (mJouerPlaylist == p.mJouerPlaylist) && egale;
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
        return mJouerPlaylist + mListeMorceaux.toString();
    }
}
