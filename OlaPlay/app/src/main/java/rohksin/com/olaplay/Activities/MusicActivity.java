package rohksin.com.olaplay.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rohksin.com.olaplay.POJO.Music;
import rohksin.com.olaplay.R;
import rohksin.com.olaplay.Services.MediaPlayerService;
import rohksin.com.olaplay.Utility.AppUtility;

/**
 * Created by Illuminati on 12/16/2017.
 */

public class MusicActivity extends AppCompatActivity{

    private int currentIndex;
    private ArrayList<Music> musicList;
    private boolean isMusicPlaying;
    private int playBackground = R.drawable.play_big;
    private int pauseBackgound = R.drawable.pause_big;

    @BindView(R.id.cover_image)
    ImageView songImage;

    @BindView(R.id.song_name)
    TextView songName;

    @BindView(R.id.artistName)
    TextView artists;

    @BindView(R.id.playCurrent)
    Button current;

    @BindView(R.id.playPrev)
    Button prev;

    @BindView(R.id.playNext)
    Button next;


    private MediaPlayerService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.music_activity);
        ButterKnife.bind(this);
        setUpUI();
    }

    //*********************************************************************
    //  PRIVATE HELPER METHODS
    //*********************************************************************

    private void setUpUI()
    {
        Intent intent = getIntent();
        musicList = (ArrayList<Music>)(intent.getSerializableExtra(AppUtility.MUSIC_LIST));
        currentIndex = (Integer)intent.getIntExtra(AppUtility.CURRENT_INDEX,0);
        //isMusicPlaying = (Boolean)intent.getBooleanExtra(AppUtility.MUSIC_PLAYING,false);
        Log.d("BUTTON Status","Current");
        setUpButton();


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(getPrevIndex(currentIndex));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(getNextIndex(currentIndex));
            }
        });

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(currentIndex);
            }
        });

        loadData(currentIndex);
    }

    private void loadData(int index)
    {
        Music currentMusic = getCurrentMusic(index);

        Glide.with(MusicActivity.this)
                .load(currentMusic.getCover_image())
                .centerCrop()
                .into(songImage);

        songName.setText(currentMusic.getSong());
        artists.setText(currentMusic.getArtists());

        if(musicSrv!=null)
        {
            Log.d("SERVICE NOT NULL","True");
            musicSrv.processSong(currentMusic.getUrl());
        }

        setUpButton();

    }


    private Music getCurrentMusic(int index)
    {
        return musicList.get(index);
    }

    private int getNextIndex(int currentIndex)
    {
        if(currentIndex==musicList.size()-1)
            return currentIndex;
        else
            return ++this.currentIndex;
    }

    private int getPrevIndex(int currentIndex)
    {
        if(currentIndex== 0)
            return currentIndex;
        else
            return --this.currentIndex;
    }


    //*********************************************************************
    //  SYSTEM CALLBACKS
    //*********************************************************************


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        Log.d("CURRENT_INDEXAT ",currentIndex+"");
        intent.putExtra(AppUtility.CURRENT_INDEX,currentIndex);
        setResult(Activity.RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MediaPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }





    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            //musicSrv.setList(new ArrayList<Music>());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };



    //*********************************************************************
    //  HELPER METHODS
    //*********************************************************************


    public void setAnimation()
    {

        if(Build.VERSION.SDK_INT>20) {
            Slide slide = new Slide();
            slide.setDuration(1000);
            slide.setSlideEdge(Gravity.BOTTOM);
            getWindow().setEnterTransition(slide);
            getWindow().setExitTransition(slide);
        }
    }




    private void setUpButton()
    {

        if(musicSrv==null)
        {
            Log.d("BUTTON STATUS","NULL");
            setPlayButtonBackground(playBackground);

        }
        else {

            Log.d("BUTTON STATUS","NOT NULL");

            if(musicSrv.isTrackPlaying())
            {
                Log.d("BUTTON STATUS","Track Playing");
                setPlayButtonBackground(playBackground);
            }
            else {
                Log.d("BUTTON STATUS","Track Not Playing");
                setPlayButtonBackground(pauseBackgound);
            }
        }


    }


    private void setPlayButtonBackground(int drawableId)
    {
        current.setBackgroundResource(drawableId);
    }



    public void stopService()
    {
        stopService(playIntent);
        musicSrv=null;
        System.exit(0);
    }



}




