package rohksin.com.olaplay.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rohksin.com.olaplay.Callbacks.MusicServiceCallbacks;
import rohksin.com.olaplay.R;
import rohksin.com.olaplay.Services.MediaPlayerService;
import rohksin.com.olaplay.Utility.AppUtility;

/**
 * Created by Illuminati on 12/18/2017.
 */

public class MusicTestActivity extends AppCompatActivity implements MusicServiceCallbacks{


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

        registerReceiver(new MusicProgressUpdateReceiver(),new IntentFilter(AppUtility.MUSIC_PROGRESS_UPDATE_BROADCASTRECEIVER));

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                musicSrv.processSong("https://s3-ap-southeast-1.amazonaws.com/he-public-data/Afreen%20Afreen%20(DjRaag.Net)2cc6f8b.mp3");

                song.setText(musicSrv.getFullLength()+"");
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //musicSrv.playAt(musicSrv.getFullLength()-4000);
               // startActivity(new Intent(MusicTestActivity.this,MusicTest2.class));
                startActivityForResult(new Intent(MusicTestActivity.this,MusicTest2.class),99);
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
    public void onActivityResult(int req, int res ,Intent data)
    {
        //super.onActivityResult(req);
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MediaPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
//

        }
    }


    public void updateProgess()
    {
        song.setText("");
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



    @Override
    public void updateProgerss() {
        song.setText(musicSrv.getCurrentPosition()+"");
    }


    class MusicProgressUpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AppUtility.MUSIC_PROGRESS_UPDATE_BROADCASTRECEIVER))
            {
                updateProgerss();
            }
        }
    }





}

