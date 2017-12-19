package rohksin.com.olaplay.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rohksin.com.olaplay.Activities.SplashActivity;
import rohksin.com.olaplay.R;
import rohksin.com.olaplay.Utility.AppUtility;

/**
 * Created by Illuminati on 12/19/2017.
 */

public class DownloadToExtStrService extends IntentService {


    private String fileName = "";
    private String downLoadUrl = "";
    private static int FOREGROUND_ID = 4332;

    public DownloadToExtStrService()
    {
        super("DownloadToExtStrService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        fileName = intent.getStringExtra(AppUtility.DOWNLOAD_SERVICE_FILE_NAME);
        downLoadUrl = intent.getStringExtra(AppUtility.DOWNLOAD_SERVICE_URL);
        startForeground(FOREGROUND_ID,getNotification());
        AppUtility.downloadFile(downLoadUrl,fileName);
        stopForeground(true);
    }


    public Notification getNotification()
    {

        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder foregroundNotification = new NotificationCompat.Builder(this);

        foregroundNotification.setOngoing(true);
        foregroundNotification.setContentTitle("DownLoading...")
                .setContentText(fileName)
                .setSmallIcon(R.drawable.download_small)
                .setContentIntent(pendingIntent);

        return foregroundNotification.build();
    }

}
