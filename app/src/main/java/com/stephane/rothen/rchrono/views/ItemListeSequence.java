package com.stephane.rothen.rchrono.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stephane.rothen.rchrono.R;

/**
 * View permettant de stocker un item Sequence de la ListView
 * <p/>
 * Created by stéphane on 09/03/2015.
 */
public class ItemListeSequence extends LinearLayout {

    protected ImageView mImgSuppr;
    protected TextView mText;
    protected int mPosition;

    public ItemListeSequence(Context context) {
        super(context);
        init();
    }

    public ItemListeSequence(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemListeSequence(Context context, AttributeSet attrs, int defStyleAttr) {
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
        LayoutInflater.from(getContext()).inflate(R.layout.lv_seq_layout, this, true);
        setOrientation(HORIZONTAL);
        mText = (TextView) findViewById(R.id.txtLvSequence);
        mImgSuppr = (ImageView) findViewById(R.id.imgSuppr);
    }


    /**
     * permet de modifier les valeurs de la view
     *
     * @param txt              valeur à affecter à la zone de texte
     * @param visibiliteBouton visibilité du bouton
     */
    public void setUpView(String txt, boolean visibiliteBouton) {
        mText.setText(txt);
        mImgSuppr.setVisibility((visibiliteBouton) ? VISIBLE : INVISIBLE);
    }
}
