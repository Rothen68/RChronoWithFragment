package com.stephane.rothen.rchrono.model;

import java.util.ArrayList;

/**
 * Classe permettant de stocker la liste des exercices générique
 *
 * Created by Stéphane on 14/02/2015.
 */
public class LibrairieExercices {
    /**
     * Tableau contenant les exercices
     */
    protected ArrayList<Exercice> m_tabExercice;


    public LibrairieExercices(ArrayList<Exercice> e)
    {
        setLibrairieExercice(e);
    }

    public void setLibrairieExercice(ArrayList<Exercice> e)
    {
        m_tabExercice=e;
    }

    public ArrayList<Exercice> getLibrairie()
    {
        return m_tabExercice;
    }

    public void ajoutExercice(Exercice e)
    {
        if (e!=null)
            m_tabExercice.add(e);
    }
}
