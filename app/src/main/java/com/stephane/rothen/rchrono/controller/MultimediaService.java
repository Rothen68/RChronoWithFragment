package com.stephane.rothen.rchrono.controller;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.stephane.rothen.rchrono.Fonctions;
import com.stephane.rothen.rchrono.model.Morceau;
import com.stephane.rothen.rchrono.model.NotificationExercice;
import com.stephane.rothen.rchrono.model.SyntheseVocale;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by stéphane on 05/04/2015.
 */
public class MultimediaService extends Service implements TextToSpeech.OnInitListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    IBinder mBinder;

    /**
     * Instance de la classe AtomicReference<Chronometre> pour éviter les conflits d'acces entre le ChronoService et l'activity
     *
     * @see com.stephane.rothen.rchrono.controller.Chronometre
     * @see java.util.concurrent.atomic.AtomicReference
     */
    private AtomicReference<Chronometre> mChrono = null;
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
     * Stocke la notification de l'exercice actif
     */
    private NotificationExercice mNotificationExercice;


    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mTextToSpeach = new TextToSpeech(this, this);
        mMPNotif = new MediaPlayer();
        mMPNotif.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        mMPNotif.setOnPreparedListener(this);
        mMPNotif.setOnCompletionListener(this);
        mMPNotif.setOnErrorListener(this);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
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
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     *
     * @param status {@link android.speech.tts.TextToSpeech#SUCCESS} or {@link android.speech.tts.TextToSpeech#ERROR}.
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

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mMPNotif.reset();
        gestionSyntheseVocale();
        prepareMPNotif();
    }

    /**
     * Called to indicate an error.
     *
     * @param mp    the MediaPlayer the error pertains to
     * @param what  the type of error that has occurred:
     * @param extra an extra code, specific to the error. Typically
     *              implementation dependent.
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    /**
     * Prepare le MediaPlayer jouant la sonnerie de l'exercice
     */
    private void prepareMPNotif() {
        Morceau m = mChrono.get().getMorceauFromLibMorceau(mNotificationExercice.getFichierSonnerie());
        if (m != null) {

            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri sonnerie = ContentUris.withAppendedId(musicUri, m.getIdMorceauDansTelephone());
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

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mEtatMPNotif = true;
    }

    /**
     * Gestion de la synthèse vocale pour la séquence et l'exercice en cours
     */
    private void gestionSyntheseVocale() {

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
            if (mSyntheseVocaleExercice.getNom()) {
                mTextToSpeach.speak(mChrono.get().getElementSequenceActif().getNomExercice(), TextToSpeech.QUEUE_ADD, null);
            }
            if (mSyntheseVocaleExercice.getDuree()) {
                int duree = mChrono.get().getElementSequenceActif().getDureeExercice();
                String texte = Fonctions.convertSversVocale(duree);
                mTextToSpeach.speak(texte, TextToSpeech.QUEUE_ADD, null);
            }
            mEnoncerSyntheseVocale = false;
        }
    }
}
