package com.stephane.rothen.rchrono.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stephane.rothen.rchrono.R;

/**
 * View permettant de stocker un item Exercice de la ListView
 * Created by stéphane on 09/03/2015.
 */
public class ItemListeExercice extends LinearLayout {

    protected ImageView mFleche;
    protected ImageView mImgSuppr;
    protected TextView mText;
    protected int mPosition;


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

    public int getPosition() {
        return mPosition;
    }

    /**
     * Fonction qui permet d'initialiser l'objet, cette fonction est appelée dans les trois constructeurs
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.lv_exercice_layout, this, true);
        setOrientation(HORIZONTAL);
        mText = (TextView) findViewById(R.id.txtLvExercice);
        mFleche = (ImageView) findViewById(R.id.imageView);
        mImgSuppr = (ImageView) findViewById(R.id.imgSuppr);
        mPosition = -1;
    }

    /**
     * Permet de mettre à jour les valeurs de l'objet
     *
     * @param txt              Texte à afficher dans la TextView
     * @param visibiliteFleche Affiche ou non la fleche de focus
     * @param visibiliteBouton Affiche ou non le bouton suppression
     */
    public void setUpView(String txt, boolean visibiliteFleche, boolean visibiliteBouton) {
        mText.setText(txt);
        mFleche.setVisibility((visibiliteFleche) ? VISIBLE : INVISIBLE);
        mImgSuppr.setVisibility((visibiliteBouton) ? VISIBLE : INVISIBLE);

    }
}
