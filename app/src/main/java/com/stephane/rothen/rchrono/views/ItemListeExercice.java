package com.stephane.rothen.rchrono.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stephane.rothen.rchrono.R;

/**
 * View permettant de stocker un item Exercice de la ListView
 * Created by stéphane on 09/03/2015.
 */
public class ItemListeExercice extends LinearLayout {

    protected ImageView m_Fleche;
    protected ImageButton m_btnSuppr;
    protected TextView m_Text;


    public ItemListeExercice(Context context) {
        super(context);
        init();
    }

    public ItemListeExercice(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemListeExercice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * Fonction qui permet d'initialiser l'objet, cette fonction est appelée dans les trois constructeurs
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.lv_exercice_focused_layout, this, true);
        setOrientation(HORIZONTAL);
        m_Text = (TextView) findViewById(R.id.txtLvExercice);
        m_Fleche = (ImageView) findViewById(R.id.imageView);
        m_btnSuppr = (ImageButton) findViewById(R.id.btnSuppr);
    }

    /**
     * Permet de mettre à jour les valeurs de l'objet
     *
     * @param txt              Texte à afficher dans la TextView
     * @param visibiliteFleche Affiche ou non la fleche de focus
     * @param visibiliteBouton Affiche ou non le bouton suppression
     */
    public void setUpView(String txt, boolean visibiliteFleche, boolean visibiliteBouton) {
        m_Text.setText(txt);
        m_Fleche.setVisibility((visibiliteFleche) ? VISIBLE : INVISIBLE);
        m_btnSuppr.setVisibility((visibiliteBouton) ? VISIBLE : INVISIBLE);

    }
}
