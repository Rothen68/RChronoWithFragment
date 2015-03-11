package com.stephane.rothen.rchrono.model;

/**
 * SyntheseVocale est la classe métier gérant la synthese vocale
 *
 * Created by Stéphane on 14/02/2015.
 */
public class SyntheseVocale {
    private static final int NOM = 0x01;
    private static final int DUREE = 0x02;
    /**
     * Le nom est dit par la synthèse vocale
     */
    protected boolean m_nom=false;
    /**
     * La durée est dite par la synthèse vocale
     */
    protected boolean m_duree=false;

    /**
     * initialise l'objet depuis un entier contenu dans la base de donnée
     * @param syntheseVocaleFromBdd : valeur lut dans la base de donnée
     */
    public SyntheseVocale(int syntheseVocaleFromBdd)
    {
        if((syntheseVocaleFromBdd&NOM)!=0)
        {
            m_nom=true;
        }
        else
        {
            m_nom=false;
        }
        if((syntheseVocaleFromBdd&DUREE)!=0)
        {
            m_duree=true;
        }
        else
        {
            m_duree=false;
        }
    }

    /**
     * Renvois un entier pour stocker l'information de synthese vocale dans la base de donnée
     * @return valeur à stocker dans la base de donnée
     */
    public int getSyntheseVocaleForBdd()
    {
        int r = ((m_nom)?(NOM):(0));
        r=r+ ((m_duree)?(DUREE):(0));
        return r;
    }

}
