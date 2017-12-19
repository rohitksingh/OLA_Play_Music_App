package rohksin.com.olaplay.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 888;


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
                //startActivityForResult(new Intent(MusicTestActivity.this,MusicTest2.class),99);

                final String url = "https://s3-ap-southeast-1.amazonaws.com/he-public-data/Afreen%20Afreen%20(DjRaag.Net)2cc6f8b.mp3";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download(url,"Olaplay.mp3");
                    }
                }).start();

            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //musicSrv.processSong("https://s3-ap-southeast-1.amazonaws.com/he-public-data/Tera%20Woh%20Pyar-(Mr-Jatt.com)16d7c13.mp3");

               readFile();

            }
        });

    }





    public void download(String sUrl, String fileName)
    {




        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("Server returned HTTP",connection.getResponseCode()
                        + " " + connection.getResponseMessage());
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            File musicFile = new File(getAlbumStorageDir("Ola New"), fileName);
            output = new FileOutputStream(musicFile);

            byte data[] = new byte[4096];
            long total = 0;
            int count;

            Log.d("TOTAL SIZE",sUrl);

            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                /*
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                */
                total += count;

                Log.d("File size",total+"");
                // publishing the progress....
                //  if (fileLength > 0) // only if total length is known
                // publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
    }




    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.


        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (!file.mkdirs()) {
            Log.e("Directory not created", "Directory not created");
        }
        return file;
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
        }

        checkPermission();
    }


    public void checkPermission()
    {
        if(!ifHasPermission())
        {
            requestPermission();
        }
    }


    public boolean ifHasPermission()
    {

        int permissionCheck = ContextCompat.checkSelfPermission(MusicTestActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return permissionCheck == PackageManager.PERMISSION_GRANTED;

    }

    public void requestPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MusicTestActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


            Toast.makeText(MusicTestActivity.this, "Hey provide the permission",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MusicTestActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(MusicTestActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case  888: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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



    public void readFile()
    {
        File file = getAlbumStorageDir("Ola New");

        //String[] files = file.list();

        File[] files = file.listFiles();

        File downlodedFile = files[0];


        /*
        musicSrv.processSong(downlodedFile.getPath());

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(downlodedFile.getPath());
        sharingIntent.setType("audio/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        */

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(file.getPath());
        intent.setDataAndType(uri, "text/csv");
        startActivity(Intent.createChooser(intent, "See Downloaded files"));
        //startActivity(Intent.createChooser(sharingIntent, "Share image using"));

        Log.d("RREAD FILE ", files.length+" ");

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
                //updateProgerss();
            }
        }
    }





}

