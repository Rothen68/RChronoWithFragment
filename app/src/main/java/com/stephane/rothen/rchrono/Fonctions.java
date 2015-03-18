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

    public static String convertSversHMSSansZeros(int s) {
        int heure = (int) s / 3600;
        int minute = (int) (s - 3600 * heure) / 60;
        int seconde = (int) s - 3600 * heure - 60 * minute;
        String valeur = new String();
        valeur = "";
        if (heure != 0) {
            valeur = valeur + String.valueOf(heure) + " : ";
        }
        if ((minute == 0 && heure > 0) || (minute != 0)) {
            if (minute < 10 && heure != 0)
                valeur = valeur + "0";
            valeur = valeur + String.valueOf(minute) + " : ";
        }
        if ((seconde == 0 && (minute != 0 || heure != 0) || (seconde != 0))) {
            if (seconde < 10 && (heure != 0 || minute != 0))
                valeur = valeur + "0";
            valeur = valeur + String.valueOf(seconde) + " s";
        }
        return valeur;
    }

    public static String convertSversVocale(int s) {
        int heure = (int) s / 3600;
        int minute = (int) (s - 3600 * heure) / 60;
        int seconde = (int) s - 3600 * heure - 60 * minute;
        String valeur = new String();
        valeur = "";
        if (heure != 0) {
            valeur = valeur + String.valueOf(heure) + " heures ";
        }
        if (minute != 0) {
            valeur = valeur + String.valueOf(minute) + " minutes ";
        }
        if (seconde != 0) {

            valeur = valeur + String.valueOf(seconde) + " secondes";
        }
        return valeur;
    }


    public static int[] ajouterDansTabInt(int[] tableau, int taille, int element) {
        int[] tabTemp = new int[taille + 1];
        for (int i = 0; i < taille; i++) {
            tabTemp[i] = tableau[i];
        }
        tabTemp[taille] = element;
        return tabTemp;
    }
}
