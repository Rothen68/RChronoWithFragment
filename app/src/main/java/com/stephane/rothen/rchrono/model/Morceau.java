package com.stephane.rothen.rchrono.model;

/**
 * Classe métier permettant de stocker un morceau de musique
 * Created by stéphane on 18/02/2015.
 */
public class Morceau {
    protected long m_idMorceau;

    protected String m_titre;

    protected String m_artiste;

    public Morceau(long id, String titre, String artiste) {
        m_idMorceau = id;
        m_titre = titre;
        m_artiste = artiste;
    }

    public long getIdMorceau() {
        return m_idMorceau;
    }

    public void setIdMorceau(long id) {
        m_idMorceau = id;
    }

    public String getTitre() {
        return m_titre;
    }

    public void setTitre(String titre) {
        m_titre = titre;
    }

    public String getArtiste() {
        return m_artiste;
    }

    public Morceau getClone() {
        return new Morceau(m_idMorceau, m_titre, m_artiste);
    }

    public boolean egale(Morceau m) {
        if (m_artiste.equals(m.m_artiste) && m_idMorceau == m.m_idMorceau && m_titre.equals(m.m_titre))
            return true;
        else return false;
    }


}
