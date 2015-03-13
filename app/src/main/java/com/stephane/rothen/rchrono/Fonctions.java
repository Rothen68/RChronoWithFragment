package com.stephane.rothen.rchrono;

/**
 * Classe contenant les fonctions statiques utilisées dans le projet
 * <p/>
 * Created by stéphane on 13/03/2015.
 */
public class Fonctions {

    /**
     * Fonction permettant de convertir une durée en seconde stocké dans un entier en une chaine de caractere au format HH : MM : SS
     *
     * @param s durée en seconde à convertir
     * @return chaine de caractere contenant la durée convertie
     */
    public static String convertSversHMS(int s) {
        int heure = (int) s / 3600;
        int minute = (int) (s - 3600 * heure) / 60;
        int seconde = (int) s - 3600 * heure - 60 * minute;
        String valeur = new String();
        if (heure < 10)
            valeur = valeur + "0";
        valeur = valeur + String.valueOf(heure) + " : ";

        if (minute < 10)
            valeur = valeur + "0";
        valeur = valeur + String.valueOf(minute) + " : ";

        if (seconde < 10)
            valeur = valeur + "0";
        valeur = valeur + String.valueOf(seconde);
        return valeur;
    }
}
