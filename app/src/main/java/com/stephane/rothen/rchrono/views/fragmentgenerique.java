//package com.stephane.rothen.rchrono.views;
//
///**
// * Created by stéphane on 11/03/2015.
// */
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.stephane.rothen.rchrono.controller.CustomAdapter;
//import com.stephane.rothen.rchrono.R;
//import com.stephane.rothen.rchrono.controller.Chronometre;
//import com.stephane.rothen.rchrono.model.Sequence;
//
///**
// * A placeholder fragment containing a simple view.
// */
//public class * extends Fragment {
//
//    public static final String * = "*";
//
//    /**
//     * Instance de l'interface OnClickListener
//     */
//    private * mCallback;
//
//    /**
//     * interface OnClickListener
//     * <p>Cette interface permet d'envoyer l'évenement OnClick d'un Button vers la classe activité qui a lancé le fragment</p>
//     */
//    public interface *{
//        /**
//         * Evenement OnClick sur un button
//         * @param v
//         *      View sur laquelle l'utilisateur aa cliqué
//         */
//        public void onClickListener(View v);
//    }
//
//
//
//    /**
//     * Fonction appelée quand le fragment est attaché à son Activity
//     * @param activity
//     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
//     */
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mCallback = (*) activity;
//        }catch (ClassCastException e)
//        {
//            throw new ClassCastException(activity.toString() + " must implements *");
//        }
//
//
//    }
//
//    public ChronometreFragment() {
//    }
//
//
//    /**
//     * Initialisation de l'interface du fragment
//     * @param inflater
//     * @param container
//     * @param savedInstanceState
//     * @return
//     *
//     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_chronometre, container, false);
//
//        return rootView;
//    }
//
//
//
//@Override
//public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        }
//
//@Override
//public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
//        }
//
//@Override
//public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        }
//
//@Override
//public void onResume() {
//        super.onResume();
//        }
//
//@Override
//public void onPause() {
//        super.onPause();
//        }
//
//@Override
//public void onStop() {
//        super.onStop();
//        }
//
//@Override
//public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        }
//
//
//
//
//
//
//
//}
