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
 * Created by Illuminati on 12/16/2017.
 */

public class DownloadService extends IntentService {

    private String fileName = "";
    private String downLoadUrl = "";
    private static int FOREGROUND_ID = 1543;

    public DownloadService()
    {
        super("DownloadService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        fileName = intent.getStringExtra(AppUtility.DOWNLOAD_SERVICE_FILE_NAME);
        downLoadUrl = intent.getStringExtra(AppUtility.DOWNLOAD_SERVICE_URL);

        startForeground(FOREGROUND_ID,getNotification());

        download(downLoadUrl,fileName);
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
            File musicFile = new File(getFilesDir(), fileName);
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



}
