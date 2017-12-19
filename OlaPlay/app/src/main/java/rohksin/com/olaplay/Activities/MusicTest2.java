package rohksin.com.olaplay.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rohksin.com.olaplay.R;
import rohksin.com.olaplay.Services.MediaPlayerService;

/**
 * Created by Illuminati on 12/18/2017.
 */


/**
 * Created by Illuminati on 12/18/2017.
 */

public class MusicTest2 extends AppCompatActivity {

    @BindView(R.id.play)
    Button play;

    @BindView(R.id.pause)
    Button pause;

    @BindView(R.id.next)
    Button next;

    @BindView(R.id.song)
    TextView song;

    private MediaPlayerService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_text);

        ButterKnife.bind(this);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                musicSrv.processSong("https://s3-ap-southeast-1.amazonaws.com/he-public-data/Afreen%20Afreen%20(DjRaag.Net)2cc6f8b.mp3");

            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.processSong("https://s3-ap-southeast-1.amazonaws.com/he-public-data/Tera%20Woh%20Pyar-(Mr-Jatt.com)16d7c13.mp3");
            }
        });


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


    public void stopService()
    {
        stopService(playIntent);
        musicSrv=null;
        System.exit(0);
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


    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

}

