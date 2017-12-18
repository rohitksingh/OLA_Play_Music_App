package rohksin.com.olaplay.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
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
        isMusicPlaying = (Boolean)intent.getBooleanExtra(AppUtility.MUSIC_PLAYING,false);

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
                setUpButton();
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
        intent.putExtra(AppUtility.CURRENT_INDEX,currentIndex);
        setResult(Activity.RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }

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
        if(isMusicPlaying)
        {
            isMusicPlaying = false;
            setPlayButtonBackground(pauseBackgound);
        }
        else
        {
            isMusicPlaying = true;
            setPlayButtonBackground(playBackground);
        }
    }

    private void setPlayButtonBackground(int drawableId)
    {
        current.setBackgroundResource(drawableId);
    }

}




