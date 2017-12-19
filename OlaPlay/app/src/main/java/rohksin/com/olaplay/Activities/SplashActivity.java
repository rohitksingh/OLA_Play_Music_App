package rohksin.com.olaplay.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import rohksin.com.olaplay.MusicListActivity;
import rohksin.com.olaplay.R;
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
