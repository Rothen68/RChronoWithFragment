package com.stephane.rothen.rchrono.model;

import java.util.ArrayList;

/**
 * Classe métier permettant de stocker les données de la playlist
 *
 * Created by Stéphane on 14/02/2015.
 */
public class Playlist {
    /**
     * Tableau contenant la liste des liens vers les morceaux à jouer dans la playlist
     */
    protected ArrayList<Morceau> m_listeMorceaux;
    /**
     * La playlist est jouée pendant l'exercice
     */
    protected boolean m_jouerPlaylist;

    public Playlist(){
        m_listeMorceaux = new ArrayList<>();
    };



    public void setJouerPlaylist ( boolean b)
    {
        m_jouerPlaylist=b;
    }


    public int getJouerPlaylist()
    {
        if (m_jouerPlaylist)
            return 1;
        else
            return 0;
    }

    /**
     * Ajoute le morceau dont le chemin est passé en parametre
     *
     * @param m
     *  Morceau a ajouter
     */
    public void ajouterMorceau(Morceau m)
    {
        m_listeMorceaux.add(m);
    }



    /**
     * Renvois le morceau dont la position est passée en parametre
     * @param position
     *  Position du morceau dans la playlist
     * @return
     *  Uri du morceau
     */
    public Morceau getMorceauAt(int position)
    {
        if ( position>=0 && position < m_listeMorceaux.size())
            return m_listeMorceaux.get(position);
        else return null;
    }

    /**
     * Renvois le nombre de morceaux dans la playlist
     * @return
     * Nombre de morceaux
     */
    public int getNbreMorceaux()
    {
        return m_listeMorceaux.size();
    }
}
