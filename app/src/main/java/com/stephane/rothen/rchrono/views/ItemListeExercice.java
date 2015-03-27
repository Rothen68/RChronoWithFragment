package com.stephane.rothen.rchrono.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

    protected ImageView mFleche;
    protected ImageButton mBtnSuppr;
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
        mBtnSuppr = (ImageButton) findViewById(R.id.btnSuppr);
        mPosition = -1;
    }

    /**
     * Permet de mettre à jour les valeurs de l'objet
     *
     * @param position         position de l'exercice dans la listView
     * @param txt              Texte à afficher dans la TextView
     * @param visibiliteFleche Affiche ou non la fleche de focus
     * @param visibiliteBouton Affiche ou non le bouton suppression
     */
    public void setUpView(int position, String txt, boolean visibiliteFleche, boolean visibiliteBouton, final Frag_Liste_Callback callback) {
        mText.setText(txt);
        mFleche.setVisibility((visibiliteFleche) ? VISIBLE : INVISIBLE);
        mBtnSuppr.setVisibility((visibiliteBouton) ? VISIBLE : INVISIBLE);
        if (callback != null && visibiliteBouton) {
            mBtnSuppr.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClickListener(v);
                }
            });
            mText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClickListener(v);
                }
            });
            mText.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return callback.onLongClickListener(v);
                }
            });

        }

        mPosition = position;

    }
}
