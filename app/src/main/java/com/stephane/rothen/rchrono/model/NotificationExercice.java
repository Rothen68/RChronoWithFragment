package com.stephane.rothen.rchrono.model;

/**
 * Notification est la classe métier gérant les notifications pour un exercice
 *
 * Created by Stéphane on 14/02/2015.
 */
public class NotificationExercice {
    private static final int VIBREUR = 0x01;
    private static final int POPUP = 0x02;
    private static final int SONNERIE = 0x04;

    /**
     * La notification est du type vibreur
     */
    protected boolean m_vibreur;
    /**
     * La notification est du type Popup
     */
    protected boolean m_popup;
    /**
     * La notification est du type sonnerie
     */
    protected boolean m_sonnerie;
    /**
     * Chemin vers le fichier de sonnerie si la notification est du type sonnerie
     * @see NotificationExercice#m_sonnerie
     */
    protected long m_idFichierSonnerie;

    public NotificationExercice(int notificationFromBdd, long idFichierSonnerie) {


        if((notificationFromBdd & VIBREUR)>0)
        {
            m_vibreur=true;
        }
        else
        {
            m_vibreur=false;
        }

        if((notificationFromBdd & POPUP)>0)
        {
            m_popup=true;
        }
        else
        {
            m_popup=false;
        }

        if((notificationFromBdd & SONNERIE)>0)
        {
            m_sonnerie=true;
        }
        else
        {
            m_sonnerie=false;
        }

        m_idFichierSonnerie=idFichierSonnerie;
    }

    public int getNotificationForBdd()
    {
        int r =0;
        r=r+ ((m_vibreur)?(VIBREUR):(0));
        r=r+ ((m_popup)?(POPUP):(0));
        r=r+ ((m_sonnerie)?(SONNERIE):(0));
        return r;
    }

    public boolean getVibreur(){return m_vibreur;}
    public boolean getSonnerie(){return m_sonnerie;}
    public boolean getPopup(){return m_popup;}


    public long getFichierSonnerie()
    {
        return m_idFichierSonnerie;
    }
}
