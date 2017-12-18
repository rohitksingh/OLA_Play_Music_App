package rohksin.com.olaplay.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import rohksin.com.olaplay.POJO.Music;

/**
 * Created by Illuminati on 12/18/2017.
 */

public class MediaPlayerService extends Service  implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {


    private MediaPlayer player;

    private String currentSong ="noSong";
    private int  pauseAt;
    private boolean songPaused;

    private String TAG = "ROHIT";
    private final IBinder musicBind = new MusicBinder();


    //***************************************************************************
    // Servide Callback methdos
    //***************************************************************************

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG,"OnBind");
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.d(TAG,"Unbid");
        player.stop();
        player.release();
        return false;
    }



    @Override
    public void onCreate(){


        super.onCreate();
        player = new MediaPlayer();
        initMusicPlayer();

    }




    public class MusicBinder extends Binder {

        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }



    //*********************************************************
    //       Public Methods to utilsed by other Components
    //**********************************************************



    public void processSong(String url)
    {

        if(!isSameSongRequest(url))
        {
            Log.d("Rahul","not sameSongRequst");
            playSong(url);
        }
        else
        {
            Log.d("Rahul",",Same Request");
            if(songPaused)
            {
                Log.d("Rahul",",Song was paused");
                resumeSong();
            }
            else
            {
                Log.d("Rahul",",Song was running");
                pauseSong();
                //playSong(url);
            }

        }




    }

    public boolean isSongPlaying()
    {
        return player.isPlaying();
    }




    //*****************************************************
    //   My Player state methods
    //  Not exposed to Activity
    //******************************************************


    private void initMusicPlayer(){

        Log.d(TAG,"initMusicPlayer");

        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }

    private void playSong(String url){

        Log.d(TAG,"PlaySong");


        currentSong = url;

            Uri trackUri = Uri.parse(url);
            player.reset();

            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.d("MUSIC SERVICE", "Error setting data source", e);
            }

            player.prepareAsync();



    }


    private void pauseSong()
    {
        Log.d(TAG,"Pause");

            songPaused = true;
            player.pause();
            pauseAt=player.getCurrentPosition();
            Log.d(TAG,"Stopped at "+pauseAt);

    }

    private void resumeSong()
    {
        songPaused = false;
        player.seekTo(pauseAt);
        player.start();
    }

    private boolean isSameSongRequest(String url)
    {
        Log.d("Current",url+" ??? "+currentSong);
        return url.equals(currentSong);
    }



    //****************************************************************
    //          MusicPlayer API Callback methods
    //*****************************************************************

    @Override
    public void onCompletion(MediaPlayer mp) {

        Log.d(TAG,"onCompletion Called");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG,"eror");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG,"onPrepared");
        mp.start();
    }






}
