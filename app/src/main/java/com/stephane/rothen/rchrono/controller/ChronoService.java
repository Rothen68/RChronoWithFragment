package com.stephane.rothen.rchrono.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.stephane.rothen.rchrono.Fonctions;
import com.stephane.rothen.rchrono.R;
import com.stephane.rothen.rchrono.model.ElementSequence;
import com.stephane.rothen.rchrono.model.NotificationExercice;
import com.stephane.rothen.rchrono.model.Playlist;
import com.stephane.rothen.rchrono.model.SyntheseVocale;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Classe gérant le Service en tache de fond
 */
public class ChronoService extends Service implements TextToSpeech.OnInitListener {

    public static final String SER_ACTION = "action";


    public static final String SER_TEMPS_RESTANT = "temps_restant";
    public static final String SER_UPDATE_LISTVIEW = "update_ListView";
    public static final String SER_FIN_LISTESEQUENCE = "fin_liste_sequence";
    public static final int IDNOTIFICATION = 1;
    private static Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    /**
     * Permet la communication depuis l'interface
     *
     * @see ChronometreActivity#mConnexion
     * @see ChronoService.MonBinder
     */
    private final IBinder mBinder = new MonBinder();
    /**
     * Notification builder pour l'affichage de la notification
     */
    private NotificationCompat.Builder mNotificationBuilder;
    /**
     * Instance de la classe AtomicReference<Chronometre> pour éviter les conflits d'acces entre le ChronoService et l'activity
     *
     * @see com.stephane.rothen.rchrono.controller.Chronometre
     * @see java.util.concurrent.atomic.AtomicReference
     */
    private AtomicReference<Chronometre> mChrono = null;
    /**
     * Stocke l'état actif ou pas du timer
     *
     * @see ChronoService#mTimer
     */
    private Boolean chronoStart = false;

    /**
     * Permet de savoir si le chronomètre redémarre après avoir été mis en pause
     */
    private Boolean mStartFromPause = false;
    /**
     * Stocke l'état de l'activity appelante pour détecter la fermeture de l'application ou la destruction/recréation de l'activity par le systeme
     */
    private Boolean mPersistance = false;
    /**
     * Instance de la classe CountDownTimer permettant de gérer le temps
     */
    private CountDownTimer mTimer;
    /**
     * Instance de la classe timer permettant de limiter la taille de la notification Sonnerie à 5s
     */
    private CountDownTimer mSonnerieTimer;
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
    private boolean mTextToSpeachReady = false;
    /**
     * Stocke la notification de l'exercice actif
     */
    private NotificationExercice mNotificationExercice;

    /**
     * Stocke le nom de l'ElementSequence actif
     */
    private String mNomElementSequenceActif;
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
    private int mIndexSequenceSyntheseVocaleEnnoncee = -1;
    /**
     * Spécifie si il faut énoncer le texte de la synthese vocale apres la sonnerie de notification
     */
    private boolean mEnoncerSyntheseVocale = false;
    /**
     * MediaPlayer pour les notifications ( sonnerie )
     */
    private MediaPlayer mMPNotif;
    /**
     * Etat de préparation de mMPNotif ( true : pret à jouer la sonnerie )
     */
    private boolean mEtatMPNotif = false;
    /**
     * MediaPlayer pour la playlist
     */
    private MediaPlayer mMPPlaylist;
    /**
     * Etat de la préparation de mMPPlaylist( true : pret à jouer la playlist )
     */
    private boolean mEtatMPPlaylist = false;


    /**
     * Implémente l'interface TextToSpeech.OnInitListener, active lors de la fin de l'initialisation du TextToSpeech
     *
     * @param status Status du TextToSpeech
     * @see ChronoService#mTextToSpeach
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeach.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                mTextToSpeachReady = true;
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

        //Création de la notification du service
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setSmallIcon(R.drawable.pause);
        mNotificationBuilder.setContentTitle("RChrono");
        mNotificationBuilder.setContentText("Chronomètre arrêté");
        mNotificationBuilder.setCategory(NOTIFICATION_SERVICE);
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setOngoing(true);
        Intent i = new Intent(this, ChronometreActivity.class);
        PendingIntent nPi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(nPi);

        //Définition du service en foregroundService pour qu'il tourne même en veille
        startForeground(IDNOTIFICATION, mNotificationBuilder.build());

        mTextToSpeach = new TextToSpeech(this, this);
        mTextToSpeach.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                prepareMPPlaylist();
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        mMPNotif = new MediaPlayer();
        mMPNotif.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMPNotif.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mEtatMPNotif = true;
            }
        });
        mMPNotif.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                prepareMPNotif();
                gestionSyntheseVocale();
            }
        });
        mMPNotif.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("AUDIO", " Erreur media player playlist" + extra);
                return false;
            }
        });

        mMPPlaylist = new MediaPlayer();
        mMPPlaylist.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMPPlaylist.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mEtatMPPlaylist = true;
                mMPPlaylist.seekTo(mChrono.get().getElementSequenceActif().getPlaylistParDefaut().getPositionDansMorceauActif());
                mMPPlaylist.start();


            }
        });
        mMPPlaylist.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onCompletionMPPlaylist(mp);

            }
        });
        mMPPlaylist.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("AUDIO", " Erreur media player playlist " + extra);
                return false;
            }
        });



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
        if (mMPNotif != null) {
            mMPNotif.release();
            mMPNotif = null;
        }
    }

    /**
     * Fonction gérant le lancement du chrono
     */
    public void startChrono() {
        if (!chronoStart) {
            chronoStart = true;

            // Evite la répétition de la synthèse vocale à la reprise du chrono
            if (!mStartFromPause) {
                mEnoncerSyntheseVocale = true;
                gestionSyntheseVocale();
            } else {
                mStartFromPause = false;
                if (mChrono.get().getElementSequenceActif().getPlaylistParDefaut().getJouerPlaylist())
                    prepareMPPlaylist();
            }

            lancerTimer();
            prepareMPNotif();
            updateListView();
            mNotificationBuilder.setSmallIcon(R.drawable.fleche);
            mNotificationBuilder.setContentText("Chronomètre lancé");
            mNotificationManager.notify(IDNOTIFICATION, mNotificationBuilder.build());



        }
    }

    /**
     * Retourne l'état du chrono, actif ou inactif
     *
     * @return true : actif
     * false : inactif
     */
    public boolean getChronoStart() {
        return chronoStart;
    }

    /**
     * Permet d'arreter le chrono
     */
    public void stopChrono() {
        if (chronoStart) {
            chronoStart = false;
            if (mTimer != null)
                mTimer.cancel();
            mStartFromPause = true;
        }
        mNotificationBuilder.setSmallIcon(R.drawable.pause);
        mNotificationBuilder.setContentText("Chronomètre arrêté");
        mNotificationManager.notify(IDNOTIFICATION, mNotificationBuilder.build());
        if (mEtatMPPlaylist) {
            mChrono.get().getElementSequenceActif().getPlaylistParDefaut().setPositionDansMorceauActif(mMPPlaylist.getCurrentPosition());
            mMPPlaylist.reset();
            mEtatMPPlaylist = false;
        }

    }

    /**
     * Remet le chronometre à zéro et transmet les demande d'actualisation de l'interface
     */
    public void resetChrono() {
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
        mNotificationManager.notify(IDNOTIFICATION, mNotificationBuilder.build());
        mStartFromPause = false;
        if (mEtatMPPlaylist) {
            mMPPlaylist.reset();
            mEtatMPPlaylist = false;
        }


    }

    /**
     * Permet de positionner les curseurs du chronometre à une position définie
     *
     * @param sequence index de la séquence active
     * @param exercice index de l'exercice actif
     * @see ChronoService#mChrono
     * @see com.stephane.rothen.rchrono.controller.Chronometre#setChronoAt(int, int)
     */
    public void setChronoAt(int sequence, int exercice) {
        mChrono.get().setChronoAt(sequence, exercice);
    }


    public AtomicReference<Chronometre> getAtomicChronometre() {
        return mChrono;
    }

    /**
     * Permet d'affecter un chronometre au service
     *
     * @param c instance de la classe AtomicReference<Chronometre>
     * @see ChronoService#mChrono
     */
    public void setAtomicChronometre(AtomicReference<Chronometre> c) {
        mChrono = c;
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
     *
     * @see ChronometreActivity#myReceiver
     */
    public void updateListView() {
        Intent i = new Intent();
        i.setAction(SER_UPDATE_LISTVIEW);
        int exercice = mChrono.get().getIndexExerciceActif();
        int seq = mChrono.get().getIndexSequenceActive();
        if (exercice >= 0) {
            int position = 1;
            for (int j = 0; j < seq; j++) {
                position++;
                for (ElementSequence e : mChrono.get().getSeqFromLstSequenceAt(j).getTabElement()) {
                    position++;
                }
            }
            position = position + exercice;
            i.putExtra(SER_UPDATE_LISTVIEW, position);
        } else
            i.putExtra(SER_UPDATE_LISTVIEW, 0);
        sendBroadcast(i);

    }

    /**
     * Fonction appelée pour lancer le timer
     *
     * @see ChronoService#mTimer
     */
    private void lancerTimer() {
        //todo corriger le bug d'affichage de la durée des séquences
        updateListView();
        int duree = mChrono.get().getDureeRestanteTotale();

        //Met à jour les données de notification de l'exercice en cours
        mNotificationExercice = mChrono.get().getElementSequenceActif().getNotificationExercice();
        mNomElementSequenceActif = mChrono.get().getElementSequenceActif().getNomExercice();

        mTimer = new CountDownTimer(duree * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (mChrono.get().isNextComing()) {
                    mChrono.get().getElementSequenceActif().getPlaylistParDefaut().setPositionDansMorceauActif(mMPPlaylist.getCurrentPosition());
                    mMPPlaylist.pause();

                }
                if (!mChrono.get().tick()) {
                    mEnoncerSyntheseVocale = true;
                    updateListView();
                    gestionNotification();

                    //si il n'y avait pas de sonnerie sur cet exercice alors préparer la sonnerie de l'exercice suivant
                    boolean preparerMPNotif = false;
                    if(mNotificationExercice.getSonnerie()==false)
                        preparerMPNotif = true;

                    //met à jour les données pour les notifications de l'exercice en cours
                    mNotificationExercice = mChrono.get().getElementSequenceActif().getNotificationExercice();
                    mNomElementSequenceActif = mChrono.get().getElementSequenceActif().getNomExercice();
                    if(preparerMPNotif)
                        prepareMPNotif();

                }
                updateChrono();
            }

            @Override
            public void onFinish() {
                mMPPlaylist.pause();
                mEnoncerSyntheseVocale = false;
                gestionNotification();
                resetChrono();

            }
        }.start();

    }

    /**
     * Gestion de la synthèse vocale pour la séquence et l'exercice en cours
     */
    private void gestionSyntheseVocale() {

        mSyntheseVocaleSequence = mChrono.get().getSequenceActive().getSyntheseVocale();
        if (mTextToSpeachReady && mEnoncerSyntheseVocale) {
            if (mIndexSequenceSyntheseVocaleEnnoncee != mChrono.get().getIndexSequenceActive()) {
                if (mSyntheseVocaleSequence.getNom()) {
                    mTextToSpeach.speak(mChrono.get().getSequenceActive().getNomSequence(), TextToSpeech.QUEUE_ADD, null);
                }
                if (mSyntheseVocaleSequence.getDuree()) {
                    int duree = mChrono.get().getSequenceActive().getDureeSequence();
                    for (int i = 0; i < mChrono.get().getIndexExerciceActif(); i++)
                        duree -= mChrono.get().getSequenceActive().getTabElement().get(i).getDureeExercice();
                    String texte = Fonctions.convertSversVocale(duree);
                    mTextToSpeach.speak(texte, TextToSpeech.QUEUE_ADD, null);
                }
                mIndexSequenceSyntheseVocaleEnnoncee = mChrono.get().getIndexSequenceActive();
            }
            mSyntheseVocaleExercice = mChrono.get().getElementSequenceActif().getSyntheseVocale();
            if (mSyntheseVocaleExercice.getNom()) {
                mTextToSpeach.speak(mChrono.get().getElementSequenceActif().getNomExercice(), TextToSpeech.QUEUE_ADD, null);
            }
            if (mSyntheseVocaleExercice.getDuree()) {
                int duree = mChrono.get().getElementSequenceActif().getDureeExercice();
                String texte = Fonctions.convertSversVocale(duree);
                mTextToSpeach.speak(texte, TextToSpeech.QUEUE_ADD, null);
            }
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
            mTextToSpeach.speak("", TextToSpeech.QUEUE_ADD, map);
            mEnoncerSyntheseVocale = false;

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
        if (mNotificationExercice.getPopup()) {

            String texte =  mNomElementSequenceActif+ " terminé";
            Toast.makeText(this,texte,Toast.LENGTH_LONG).show();
        }
        if (mNotificationExercice.getSonnerie()) {
            if (mEtatMPNotif) {
                mMPNotif.start();
                //Démarre le compte à rebours pour limiter la durée de la sonnerie à 5 secondes
                mSonnerieTimer = new CountDownTimer(5000,5000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if(mMPNotif.isPlaying())
                        {
                            mMPNotif.stop();
                            prepareMPNotif();
                            gestionSyntheseVocale();
                        }
                    }
                }.start();
            }
        } else {
            gestionSyntheseVocale();
        }

    }



    /**
     * Prepare le MediaPlayer jouant la sonnerie de l'exercice
     */
    private void prepareMPNotif() {
        if (mNotificationExercice.getSonnerie()) {
            long idSonnerie = mNotificationExercice.getFichierSonnerie();
            if (idSonnerie != -1) {

                Uri sonnerie = ContentUris.withAppendedId(musicUri, idSonnerie);
                mMPNotif.reset();
                try {
                    mMPNotif.setDataSource(getApplicationContext(), sonnerie);

                } catch (Exception e) {
                    Log.e("AUDIO", "Erreur mMpNotif setDataSource " + e.toString());
                }
                mEtatMPNotif = false;
                mMPNotif.prepareAsync();
            }
        }
    }


    private void prepareMPPlaylist() {
        Playlist pl = mChrono.get().getElementSequenceActif().getPlaylistParDefaut();
        if (pl.getNbreMorceaux() > 0) {
            long idMorceau = pl.getMorceauActif();
            if (idMorceau > 0 && mChrono.get().getElementSequenceActif().getPlaylistParDefaut().getJouerPlaylist()) {
                Uri morceau = ContentUris.withAppendedId(musicUri, idMorceau);
                mMPPlaylist.reset();
                try {
                    mMPPlaylist.setDataSource(getApplicationContext(), morceau);
                } catch (Exception e) {
                    Log.e("AUDIO", "Erreur mMPPlaylist setDataSource " + e.toString());
                }
                mEtatMPPlaylist = false;
                mMPPlaylist.prepareAsync();
            }
        }
    }

    private void onCompletionMPPlaylist(MediaPlayer mp) {
        mChrono.get().getElementSequenceActif().getPlaylistParDefaut().nextMorceau();
        prepareMPPlaylist();
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
