package com.stephane.rothen.rchrono.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Classe permettant de faire la liaison entre une instance de ListView et une instance de Chronometre
 * <p/>
 * <p/>
 * Created by stéphane on 23/02/2015.
 */
public class CustomAdapter extends BaseAdapter {
    public static final int TYPE_SEPARATOR = 0;
    public static final int TYPE_ITEM = 1;
    /**
     * Tableau contenant les données à afficher
     */
    protected ArrayList<SourceItem> m_Data = new ArrayList<>();
    /**
     * TreeSet contenant les positions des séquences dans le tableau de donnée
     *
     * @see CustomAdapter#m_Data
     */
    protected TreeSet<Integer> sectionHeader = new TreeSet<>();
    /**
     * Permet de creer les deux View à afficher dans la ListView selon que ce soit une séquence ou un exercice
     */
    protected LayoutInflater m_inflater;
    /**
     * Stocke l'index de l'item qui à le focus
     */
    protected int mfocusPosition = 0;
    /**
     * Permet de spécifier si le bouton suppr doit s'afficher sur chaque sequence
     */
    protected boolean mAfficheBtnSupprSequence = false;

    /**
     * Permet de spécifier si le bouton suppr doit s'afficher sur chaque exercice
     */
    protected boolean mAfficheBtnSupprExercice = false;
    /**
     * Permet de spécifier si le curseur doit s'afficher sur la ligne active
     */
    protected boolean mAfficheCurseur = false;


    /**
     * Constructeur
     * <p>Initialise le LayoutInflater avec le context de l'application</p>
     *
     * @param context Context de l'application
     * @see CustomAdapter#m_inflater
     */
    public CustomAdapter(Context context) {
        m_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setAfficheBtnSupprSequence(boolean etat) {
        mAfficheBtnSupprSequence = etat;
    }

    public void setAfficheBtnSupprExercice(boolean etat) {
        mAfficheBtnSupprExercice = etat;
    }

    public void setAfficheCurseur(boolean etat) {
        mAfficheCurseur = etat;
    }


    /**
     * renvois le nombre d'éléments à afficher
     *
     * @return nombre d'éléments à afficher
     * @see CustomAdapter#m_Data
     */
    @Override
    public int getCount() {
        return m_Data.size();
    }

    /**
     * Ajoute un item dans le tableau de donnée
     *
     * @param nom nom de l'exercice
     *            @param  duree  durée de l'exercice
     * @see CustomAdapter#m_Data
     * @see com.stephane.rothen.rchrono.model.Exercice
     */
    public void addItem(final String nom, final String duree) {
        m_Data.add(new SourceItem(nom, duree));
        notifyDataSetChanged();
    }

    /**
     * Ajoute un item de section dans le tableau de donnée
     *
     * @param nom nom d'une Sequence
     * @param nbreRepetitions  nombre de répétitions d'une séquence
     * @see CustomAdapter#m_Data
     * @see com.stephane.rothen.rchrono.model.Sequence
     */
    public void addSectionHeaderItem(final String nom, final String nbreRepetitions) {
        m_Data.add(new SourceItem(nom, nbreRepetitions));
        sectionHeader.add(m_Data.size() - 1);
        notifyDataSetChanged();

    }

    /**
     * Définit l'index de l'item qui a le focus
     *
     * @param focus index de l'item
     */
    public void setFocusPosition(int focus) {
        mfocusPosition = focus;
    }

    /**
     * Vide le tableau de donnée
     *
     * @see CustomAdapter#m_Data
     */
    public void deleteAll() {
        m_Data.clear();
        sectionHeader.clear();
    }

    /**
     * retourne le nombre de type de View
     *
     * @return 2
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Renvois le type de View en fonction de la position
     *
     * @param position position dans m_Data
     * @return type de View
     * @see CustomAdapter#TYPE_ITEM
     * @see CustomAdapter#TYPE_SEPARATOR
     */
    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    /**
     * Renvois la valeur de l'item à la position donnée
     *
     * @param position position de l'item dans m_Data
     * @return String contenant l'item récupéré
     */
    @Override
    public Object getItem(int position) {
        return m_Data.get(position);
    }


    /**
     * Renvois l'id de l'item à la position donnée
     *
     * @param position position dans m_Data
     * @return id de l'item
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Renvois la View à ajouter à la ListView
     *
     * @param position    position dans m_Data
     * @param convertView View
     * @param parent
     * @return View initialisée
     */


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int rowType = getItemViewType(position);


        switch (rowType) {

            case TYPE_ITEM:
                if (!(convertView instanceof ItemListeExercice)) {
                    //Si la View à afficher est de type Exercice et que la vue présente dans convertView ne l'est pas alors créer une nouvelle ItemListeExercice
                    convertView = new ItemListeExercice(m_inflater.getContext());
                }
                // met à jour les valeurs de la vue
                if (position == mfocusPosition && mAfficheCurseur) {

                    ((ItemListeExercice) convertView).setUpView(m_Data.get(position).nom, m_Data.get(position).donnee, true, mAfficheBtnSupprExercice);
                } else {
                    ((ItemListeExercice) convertView).setUpView(m_Data.get(position).nom, m_Data.get(position).donnee, false, mAfficheBtnSupprExercice);

                }
                break;
            case TYPE_SEPARATOR:
                if (!(convertView instanceof ItemListeSequence)) {
                    convertView = new ItemListeSequence(m_inflater.getContext());
                }
                ((ItemListeSequence) convertView).setUpView(m_Data.get(position).nom, m_Data.get(position).donnee, mAfficheBtnSupprSequence);
                break;
        }

        return convertView;
    }


    /**
     * Classe contenant les données à afficher pour un item de la listView
     */
    private class SourceItem {
        public String nom;
        public String donnee;

        public SourceItem(String nom, String donnee) {
            this.nom = nom;
            this.donnee = donnee;
        }

        public SourceItem create(String nom, String donnee) {
            return new SourceItem(nom, donnee);
        }
    }
}
