package com.stephane.rothen.rchrono.model;

/**
 * Classe métier permettant de stocker un morceau de musique
 * Created by stéphane on 18/02/2015.
 */
public class Morceau {
    protected long m_idMorceau;

    protected  String m_titre;

    protected String m_artiste;

    public Morceau(long id, String titre, String artiste)
    {
        m_idMorceau=id;
        m_titre = titre;
        m_artiste=artiste;
    }

    public void setTitre(String titre)
    {
        m_titre=titre;
    }

    public void setIdMorceau(long id)
    {
        m_idMorceau=id;
    }

    public long getIdMorceau()
    {
        return m_idMorceau;
    }

    public String getTitre()
    {
        return m_titre;
    }

    public String getArtiste()
    {
        return m_artiste;
    }


}
