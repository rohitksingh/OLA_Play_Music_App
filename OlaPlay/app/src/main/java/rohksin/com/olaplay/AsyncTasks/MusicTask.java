package rohksin.com.olaplay.AsyncTasks;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import rohksin.com.olaplay.Utility.AppUtility;

/**
 * Created by Illuminati on 12/17/2017.
 */

public class MusicTask extends AsyncTask<String,Void,Void>{

    private Context context;

    public MusicTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        MediaPlayer player = AppUtility.getMediaPlayer(context,params[0]);
        player.start();
        return null;
    }
}
