package com.stephane.rothen.rchrono;

import android.app.Notification;
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

import com.stephane.rothen.rchrono.controller.Chronometre;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.NotificationExercice;
import com.stephane.rothen.rchrono.model.SyntheseVocale;

import java.util.Locale;


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
     * @see com.stephane.rothen.rchrono.ChronometreActivity#mConnexion
     * @see com.stephane.rothen.rchrono.ChronoService.MonBinder
     */
    private final IBinder mBinder = new MonBinder();

    /**
     * Instance de la classe Chronometre
     * @see com.stephane.rothen.rchrono.controller.Chronometre
     */
    private Chronometre mChrono=null;
    /**
     * Stocke l'état actif ou pas du timer
     * @see com.stephane.rothen.rchrono.ChronoService#mTimer
     */
    private Boolean chronoStart=false;
    /**
     * Instance de la classe CountDownTimer permettant de gérer le temps
     */
    private CountDownTimer mTimer;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotificationBuilder;

    private TextToSpeech mTextToSpeach;
    private boolean mTextToSpeachReady=false;



    private NotificationExercice mNotificationExercice;
    private SyntheseVocale mSyntheseVocaleExercice;
    private SyntheseVocale mSyntheseVocaleSequence;
    private int mIndexSequenceSyntheseVocaleEnnoncee=-1;

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


    /**
     * Classe permettant la communication depuis l'interface
     *
     * @see com.stephane.rothen.rchrono.ChronoService#mBinder
     * @see com.stephane.rothen.rchrono.ChronometreActivity#mConnexion
     */
    public class MonBinder extends Binder  {
        ChronoService getService(){
            return ChronoService.this;
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setSmallIcon(R.drawable.pause);
        mNotificationBuilder.setContentTitle("RChrono");
        mNotificationBuilder.setContentText("Chronomètre arrêté");
        Intent i = new Intent(this,ChronometreActivity.class);
        PendingIntent nPi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(nPi);
        mNotificationManager.notify(IDNOTIFICATION,mNotificationBuilder.build());

        mTextToSpeach= new TextToSpeech(this,this);
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
        Log.d("Service"," dans Service onDestroy");
        if( mTimer!=null)
        {
            mTimer.cancel();
            mTimer=null;
        }
        if (mNotificationManager!=null)
        {
            mNotificationManager.cancel(IDNOTIFICATION);
            mNotificationManager=null;
            mNotificationBuilder=null;
        }
        if(mTextToSpeach!=null)
        {
            mTextToSpeach.stop();
            mTextToSpeach.shutdown();
        }
    }

    /**
     * Fonction gérant le lancement du chrono
     */
    public void startChrono()
    {
        if(!chronoStart)
        {
            chronoStart=true;

            updateNotificationSynthVocaleActives();
            gestionSyntheseVocale();


            lancerTimer();
            updateListView();
            mNotificationBuilder.setSmallIcon(R.drawable.fleche);
            mNotificationBuilder.setContentText("Chronomètre lancé");
            mNotificationManager.notify(IDNOTIFICATION,mNotificationBuilder.build());

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
        if(chronoStart)
        {
            chronoStart=false;
            if(mTimer!=null)
                mTimer.cancel();
        }
        mNotificationBuilder.setSmallIcon(R.drawable.pause);
        mNotificationBuilder.setContentText("Chronomètre arrêté");
        mNotificationManager.notify(IDNOTIFICATION,mNotificationBuilder.build());
    }

    /**
     * Remet le chronometre à zéro et transmet les demande d'actualisation de l'interface
     */
    public void resetChrono()
    {
        chronoStart=false;
        mChrono.resetChrono();
        if(mTimer!=null)
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
     * @see com.stephane.rothen.rchrono.ChronoService#mChrono
     * @see com.stephane.rothen.rchrono.controller.Chronometre#setChronoAt(int, int)
     */
    public void setChronoAt(int sequence, int exercice)
    {
        mChrono.setChronoAt(sequence,exercice);
    }

    /**
     * Permet d'affecter un chronometre au service
     * @param c
     *      instance de la classe Chronometre
     *@see com.stephane.rothen.rchrono.ChronoService#mChrono
     */
    public void setChronometre(Chronometre c)
    {
        mChrono=c;
    }
    public Chronometre getChronometre(){return mChrono;}


    /**
     * Envois une demande d'actualisation de la zone de texte txtChrono de l'interface

     *@see com.stephane.rothen.rchrono.ChronometreFragment#mtxtChrono
     * @see com.stephane.rothen.rchrono.ChronometreActivity#myReceiver
     */
    public void updateChrono()
    {
        Intent i = new Intent();
        int type = mChrono.getTypeAffichage();
        i.setAction(SER_TEMPS_RESTANT);
        switch (type)
        {
            case Chronometre.AFFICHAGE_TEMPS_EX:
                i.putExtra(SER_TEMPS_RESTANT,mChrono.getDureeRestanteExerciceActif());
                break;
            case Chronometre.AFFICHAGE_TEMPS_SEQ:
                i.putExtra(SER_TEMPS_RESTANT,mChrono.getDureeRestanteSequenceActive());
                break;
            case Chronometre.AFFICHAGE_TEMPS_TOTAL:
                i.putExtra(SER_TEMPS_RESTANT,mChrono.getDureeRestanteTotale());
                break;
            default :
                break;
        }
        sendBroadcast(i);
    }

    /**
     * Envois une demande d'actualisation de la ListView de l'interface
     *
     *@see com.stephane.rothen.rchrono.ChronometreFragment#mLv
     * @see com.stephane.rothen.rchrono.ChronometreActivity#myReceiver
     */
    public void updateListView()
    {
        Intent i = new Intent();
        i.setAction(SER_UPDATE_LISTVIEW);
        int exercice =mChrono.getIndexExerciceActif();
        int seq = mChrono.getIndexSequenceActive();
        if(exercice>=0) {
            int position = 1;
            for (int j = 0; j < seq; j++) {
                position++;
                for (ElementSequence e : mChrono.getListeSequence().get(j).getTabElement()) {
                    position++;
                }
            }
            position = position + exercice;
            i.putExtra(SER_UPDATE_LISTVIEW, position);
        }
        else
            i.putExtra(SER_UPDATE_LISTVIEW,0);
        sendBroadcast(i);

    }


    /**
     * Fonction appelée pour lancer le timer
     * @see com.stephane.rothen.rchrono.ChronoService#mTimer
     */
    private void lancerTimer()
    {
        //todo corriger le bug d'affichage de la durée des séquences
        int duree = mChrono.getDureeRestanteTotale();
        mTimer = new CountDownTimer(duree*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {


                if (!mChrono.tick()) {
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

    private void gestionSyntheseVocale() {
        if (mTextToSpeachReady) {
            if(mIndexSequenceSyntheseVocaleEnnoncee!=mChrono.getIndexSequenceActive()) {
                if (mSyntheseVocaleSequence.getNom()) {
                    mTextToSpeach.speak(mChrono.getListeSequence().get(mChrono.getIndexSequenceActive()).getNomSequence(), TextToSpeech.QUEUE_ADD, null);
                }
                if (mSyntheseVocaleSequence.getDuree()) {
                    mTextToSpeach.speak(String.valueOf(mChrono.getListeSequence().get(mChrono.getIndexSequenceActive()).getDureeSequence()) + " secondes", TextToSpeech.QUEUE_ADD, null);
                }
                mIndexSequenceSyntheseVocaleEnnoncee = mChrono.getIndexSequenceActive();
            }
            if(mSyntheseVocaleExercice.getNom())
            {
                mTextToSpeach.speak(mChrono.getListeSequence().get(mChrono.getIndexSequenceActive()).getTabElement().get(mChrono.getIndexExerciceActif()).getNomExercice(),TextToSpeech.QUEUE_ADD,null);
            }
            if(mSyntheseVocaleExercice.getDuree())
            {
                mTextToSpeach.speak(String.valueOf(mChrono.getListeSequence().get(mChrono.getIndexSequenceActive()).getTabElement().get(mChrono.getIndexExerciceActif()).getDureeExercice()) + " secondes",TextToSpeech.QUEUE_ADD,null);
            }

        }
    }

    private void gestionNotification() {
        if (mNotificationExercice.getVibreur())
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

    }

    private void updateNotificationSynthVocaleActives()
    {
        mNotificationExercice = mChrono.getListeSequence().get(mChrono.getIndexSequenceActive()).getTabElement().get(mChrono.getIndexExerciceActif()).getNotification();
        mSyntheseVocaleExercice =mChrono.getListeSequence().get(mChrono.getIndexSequenceActive()).getTabElement().get(mChrono.getIndexExerciceActif()).getSyntheseVocale();
        mSyntheseVocaleSequence=mChrono.getListeSequence().get(mChrono.getIndexSequenceActive()).getSyntheseVocale();
    }


}
