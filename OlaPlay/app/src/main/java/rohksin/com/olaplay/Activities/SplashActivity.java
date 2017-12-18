package rohksin.com.olaplay.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseLongArray;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rohksin.com.olaplay.MusicListActivity;
import rohksin.com.olaplay.R;
import rohksin.com.olaplay.Services.DownloadService;
import rohksin.com.olaplay.Utility.AppUtility;

/**
 * Created by Illuminati on 12/16/2017.
 */


public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        AppUtility.initMap();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MusicListActivity.class));
                finish();
            }
        },2000);

    }


}
