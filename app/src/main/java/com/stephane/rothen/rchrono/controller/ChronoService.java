package com.stephane.rothen.rchrono.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.NotificationExercice;
import com.stephane.rothen.rchrono.model.SyntheseVocale;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Created by stéphane on 23/02/2015.
 */
public class ChronoService extends Service implements TextToSpeech.OnInitListener {

    public static final String SER_ACTION="action";



    public static final String SER_TEMPS_RESTANT="temps_restant";
    public static final String SER_UPDATE_LISTVIEW="update_ListView";
    public static final String SER_FIN_LISTESEQUENCE ="fin_liste_sequence";
    private static final int IDNOTIFICATION = 1 ;

    /**
     * Permet la communication depuis l'interface
     * @see ChronometreActivity#mConnexion
     * @see ChronoService.MonBinder
     */
    private final IBinder mBinder = new MonBinder();
    /**
     * Notification builder pour l'affichage de la notification
     */
    NotificationCompat.Builder mNotificationBuilder;
    /**
     * Instance de la classe AtomicReference<Chronometre> pour éviter les conflits d'acces entre le ChronoService et l'activity
     *
     * @see com.stephane.rothen.rchrono.controller.Chronometre
     * @see java.util.concurrent.atomic.AtomicReference
     */
    private AtomicReference<Chronometre> mChrono = null;
    /**
     * Stocke l'état actif ou pas du timer
     * @see ChronoService#mTimer
     */
    private Boolean chronoStart = false;
    /**
     * Stocke l'état de l'activity appelante pour détecter la fermeture de l'application ou la destruction/recréation de l'activity par le systeme
     */
    private Boolean mPersistance = false;
    /**
     * Instance de la classe CountDownTimer permettant de gérer le temps
     */
    private CountDownTimer mTimer;
    /**
     * Manager de notification
     */
    private NotificationManager mNotificationManager;
    /**
     * Instance de l'objet TextToSpeech pour la synthèse vocale
     */
    private TextToSpeech mTextToSpeach;
    /**
     * Variable permettant de controler l'état de la synthese vocale
     *
     * @see ChronoService#mTextToSpeach
     */
    private boolean mTextToSpeachReady=false;


    /**
     * Stocke la notification de l'exercice actif
     */
    private NotificationExercice mNotificationExercice;
    /**
     * Stocke la synthese vocale de l'exercice actif
     */
    private SyntheseVocale mSyntheseVocaleExercice;
    /**
     * Stocke la synthese vocale de la séquence active
     */
    private SyntheseVocale mSyntheseVocaleSequence;
    /**
     * Stocke l'index de la synthese vocale qui a été énnoncé
     */
    private int mIndexSequenceSyntheseVocaleEnnoncee=-1;


    /**
     * Implémente l'interface TextToSpeech.OnInitListener, active lors de la fin de l'initialisation du TextToSpeech
     * @param status
     *      Status du TextToSpeech
     *
     *  @see ChronoService#mTextToSpeach
     */
    @Override
    public void onInit(int status) {
        if (status==TextToSpeech.SUCCESS)
        {
            int result = mTextToSpeach.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                mTextToSpeachReady=true;
            }
        }
    }


    public boolean getPersistance() {
        return mPersistance;
    }

    public void setPersistance(boolean p) {
        mPersistance = p;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setSmallIcon(R.drawable.pause);
        mNotificationBuilder.setContentTitle("RChrono");
        mNotificationBuilder.setContentText("Chronomètre arrêté");
        Intent i = new Intent(this, ChronometreActivity.class);
        PendingIntent nPi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(nPi);
        mNotificationManager.notify(IDNOTIFICATION, mNotificationBuilder.build());

        mTextToSpeach = new TextToSpeech(this, this);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mNotificationManager != null) {
            mNotificationManager.cancel(IDNOTIFICATION);
            mNotificationManager = null;
            mNotificationBuilder = null;
        }
        if (mTextToSpeach != null) {
            mTextToSpeach.stop();
            mTextToSpeach.shutdown();
        }
    }

    /**
     * Fonction gérant le lancement du chrono
     */
    public void startChrono() {
        if (!chronoStart) {
            chronoStart = true;

            updateNotificationSynthVocaleActives();
            gestionSyntheseVocale();


            lancerTimer();
            updateListView();
            mNotificationBuilder.setSmallIcon(R.drawable.fleche);
            mNotificationBuilder.setContentText("Chronomètre lancé");
            mNotificationManager.notify(IDNOTIFICATION, mNotificationBuilder.build());

        }
    }

    /**
     * Retourne l'état du chrono, actif ou inactif
     * @return
     *      true : actif
     *      false : inactif
     */
    public boolean getChronoStart()
    {
        return chronoStart;
    }

    /**
     * Permet d'arreter le chrono
     */
    public void stopChrono()
    {
        if (chronoStart) {
            chronoStart = false;
            if (mTimer != null)
                mTimer.cancel();
        }
        mNotificationBuilder.setSmallIcon(R.drawable.pause);
        mNotificationBuilder.setContentText("Chronomètre arrêté");
        mNotificationManager.notify(IDNOTIFICATION, mNotificationBuilder.build());
    }

    /**
     * Remet le chronometre à zéro et transmet les demande d'actualisation de l'interface
     */
    public void resetChrono()
    {
        chronoStart = false;
        mChrono.get().resetChrono();
        mIndexSequenceSyntheseVocaleEnnoncee = -1;
        if (mTimer != null)
            mTimer.cancel();
        updateChrono();
        updateListView();
        Intent i = new Intent();
        i.setAction(SER_FIN_LISTESEQUENCE);
        sendBroadcast(i);
        mNotificationBuilder.setSmallIcon(R.drawable.pause);
        mNotificationBuilder.setContentText("Chronomètre arrêté");
        mNotificationManager.notify(IDNOTIFICATION,mNotificationBuilder.build());

    }

    /**
     * Permet de positionner les curseurs du chronometre à une position définie
     * @param sequence
     *          index de la séquence active
     * @param exercice
     *          index de l'exercice actif
     *
     * @see ChronoService#mChrono
     * @see com.stephane.rothen.rchrono.controller.Chronometre#setChronoAt(int, int)
     */
    public void setChronoAt(int sequence, int exercice)
    {
        mChrono.get().setChronoAt(sequence, exercice);
    }
//todo rendre mChrono threadSafe multithread

    public AtomicReference<Chronometre> getAtomicChronometre() {
        return mChrono;
    }

    /**
     * Permet d'affecter un chronometre au service
     * @param c
     *      instance de la classe Chronometre
     *@see ChronoService#mChrono
     */
    public void setAtomicChronometre(AtomicReference<Chronometre> c)
    {
        mChrono=c;
    }

    /**
     * Envois une demande d'actualisation de la zone de texte txtChrono de l'interface
     *
     * @see ChronometreActivity#myReceiver
     */
    public void updateChrono() {
        Intent i = new Intent();
        int type = mChrono.get().getTypeAffichage();
        i.setAction(SER_TEMPS_RESTANT);
        switch (type) {
            case Chronometre.AFFICHAGE_TEMPS_EX:
                i.putExtra(SER_TEMPS_RESTANT, mChrono.get().getDureeRestanteExerciceActif());
                break;
            case Chronometre.AFFICHAGE_TEMPS_SEQ:
                i.putExtra(SER_TEMPS_RESTANT, mChrono.get().getDureeRestanteSequenceActive());
                break;
            case Chronometre.AFFICHAGE_TEMPS_TOTAL:
                i.putExtra(SER_TEMPS_RESTANT, mChrono.get().getDureeRestanteTotale());
                break;
            default:
                break;
        }
        sendBroadcast(i);
    }

    /**
     * Envois une demande d'actualisation de la ListView de l'interface
     * @see ChronometreActivity#myReceiver
     */
    public void updateListView()
    {
        Intent i = new Intent();
        i.setAction(SER_UPDATE_LISTVIEW);
        int exercice = mChrono.get().getIndexExerciceActif();
        int seq = mChrono.get().getIndexSequenceActive();
        if (exercice >= 0) {
            int position = 1;
            for (int j = 0; j < seq; j++) {
                position++;
                for (ElementSequence e : mChrono.get().getListeSequence().get(j).getTabElement()) {
                    position++;
                }
            }
            position = position + exercice;
            i.putExtra(SER_UPDATE_LISTVIEW, position);
        } else
            i.putExtra(SER_UPDATE_LISTVIEW,0);
        sendBroadcast(i);

    }

    /**
     * Fonction appelée pour lancer le timer
     * @see ChronoService#mTimer
     */
    private void lancerTimer() {
        //todo corriger le bug d'affichage de la durée des séquences
        int duree = mChrono.get().getDureeRestanteTotale();
        mTimer = new CountDownTimer(duree * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {


                if (!mChrono.get().tick()) {
                    updateListView();
                    gestionNotification();
                    updateNotificationSynthVocaleActives();
                    gestionSyntheseVocale();
                }
                updateChrono();
            }

            @Override
            public void onFinish() {
                resetChrono();

            }
        }.start();

    }

    /**
     * Gestion de la synthèse vocale pour la séquence et l'exercice en cours
     */
    private void gestionSyntheseVocale() {
        if (mTextToSpeachReady) {
            if (mIndexSequenceSyntheseVocaleEnnoncee != mChrono.get().getIndexSequenceActive()) {
                if (mSyntheseVocaleSequence.getNom()) {
                    mTextToSpeach.speak(mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getNomSequence(), TextToSpeech.QUEUE_ADD, null);
                }
                if (mSyntheseVocaleSequence.getDuree()) {
                    mTextToSpeach.speak(String.valueOf(mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getDureeSequence()) + " secondes", TextToSpeech.QUEUE_ADD, null);
                }
                mIndexSequenceSyntheseVocaleEnnoncee = mChrono.get().getIndexSequenceActive();
            }
            if (mSyntheseVocaleExercice.getNom()) {
                mTextToSpeach.speak(mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getTabElement().get(mChrono.get().getIndexExerciceActif()).getNomExercice(), TextToSpeech.QUEUE_ADD, null);
            }
            if (mSyntheseVocaleExercice.getDuree()) {
                mTextToSpeach.speak(String.valueOf(mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getTabElement().get(mChrono.get().getIndexExerciceActif()).getDureeExercice()) + " secondes", TextToSpeech.QUEUE_ADD, null);
            }

        }
    }

    /**
     * Gestion des notifications de l'exercice actif
     */
    private void gestionNotification() {
        if (mNotificationExercice.getVibreur()) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

    }

    /**
     * Mise à jours des notifications et syntheses vocales des exercices et sequences actifs
     *
     * @see ChronoService#mNotificationExercice
     * @see ChronoService#mSyntheseVocaleExercice
     * @see ChronoService#mSyntheseVocaleSequence
     */
    private void updateNotificationSynthVocaleActives() {
        mNotificationExercice = mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getTabElement().get(mChrono.get().getIndexExerciceActif()).getNotification();
        mSyntheseVocaleExercice = mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getTabElement().get(mChrono.get().getIndexExerciceActif()).getSyntheseVocale();
        mSyntheseVocaleSequence = mChrono.get().getListeSequence().get(mChrono.get().getIndexSequenceActive()).getSyntheseVocale();
    }

    /**
     * Classe permettant la communication depuis l'interface
     *
     * @see ChronoService#mBinder
     * @see ChronometreActivity#mConnexion
     */
    public class MonBinder extends Binder {
        ChronoService getService() {
            return ChronoService.this;
        }
    }


}
