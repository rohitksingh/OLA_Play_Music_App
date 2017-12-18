package rohksin.com.olaplay.Utility;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.LinkedHashMap;
import java.util.Map;

import rohksin.com.olaplay.Services.DownloadService;

/**
 * Created by Illuminati on 12/15/2017.
 */

public class AppUtility {

    public static final String URL_END_POINT = "http://starlord.hackerearth.com/studio";
    public static final String MUSIC_LIST_RECEIVED ="rohksin.com.olaplay.Utility.AppUtility";
    public static final String MUSIC_LIST = "rohksin.com.olaplay.Utility.MUSIC_LIST";

    public static final String CURRENT_INDEX= "rohksin.com.olaplay.Utility.CURRENT_INDEX";
    public static final int MUSIC_ACTIVITY_REQ_CODE = 22;

    public static final String DOWNLOAD_SERVICE_FILE_NAME = "rohksin.com.olaplay.Utility.DOWNLOAD_SERVICE_FILE_NAME";
    public static final String DOWNLOAD_SERVICE_URL = "rohksin.com.olaplay.Utility.DOWNLOAD_SERVICE_FILE_NAME.";
    public static final String MUSIC_PLAYING = "rohksin.com.olaplay.Utility.MUSIC_PLAYING";


    public static Map<String,String> redirectLinks;


    public static void downLoadSong(Context context, String url, String fileName)
    {
        Intent intent  = new Intent(context, DownloadService.class);
        String redirectink = getRediredLink(url);
        if(redirectink!=null)
        {
            url = redirectink;
        }
        intent.putExtra(DOWNLOAD_SERVICE_URL, url);
        intent.putExtra(DOWNLOAD_SERVICE_FILE_NAME, fileName+".mp3");
        context.startService(intent);

    }


    //*********************************************************************
    //  Returns all downloaded files
    //*********************************************************************


    public static String[] getDowmLoadedFiles(Context context)
    {
        return context.getFilesDir().list();
    }


    public static MediaPlayer getMediaPlayer(Context context, String url)
    {
        MediaPlayer player = MediaPlayer.create(context, Uri.parse(getRediredLink(url)));
        return player;
    }


    //*********************************************************************
    //  Url redirection issue , tried with Volley custom Htpp stack
    //  seems not to work for audio stream
    //  image loads with Glide with shortened link
    //*********************************************************************

    public static void initMap()
    {
        redirectLinks = new LinkedHashMap<String, String>();

        redirectLinks.put("http://hck.re/Rh8KTk","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Afreen%20Afreen%20(DjRaag.Net)2cc6f8b.mp3");
        redirectLinks.put("http://hck.re/ZeSJFd","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Aik%20-%20Alif-(Mr-Jatt.com)8ae5316.mp3");
        redirectLinks.put("http://hck.re/wxlUcX","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Tajdar%20E%20Haram-(Mr-Jatt.com)7f311d4.mp3");
        redirectLinks.put("http://hck.re/H5nMm3","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Aaj%20Rung%20-(Mr-Jatt.com)140b9e4.mp3");
        redirectLinks.put("http://hck.re/2nCncK","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Ae%20Dil-(Mr-Jatt.com)bd4f44a.mp3");
        redirectLinks.put("http://hck.re/epOzj9","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Man%20Aamadeh%20Am%20(RaagJatt.com)ae36c8d.mp3");
        redirectLinks.put("http://hck.re/YkbDDP","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Bewaja%20%20Coke%20Studio%208%20-(Mr-Jatt.com)c4f2b1b.mp3");
        redirectLinks.put("http://hck.re/dMquYY","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Dinae%20Dinae-(Mr-Jatt.com)53f3f1f.mp3");
        redirectLinks.put("http://hck.re/64Tzod","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Tera%20Woh%20Pyar-(Mr-Jatt.com)16d7c13.mp3");
        redirectLinks.put("http://hck.re/VhtQGh","https://s3-ap-southeast-1.amazonaws.com/he-public-data/Shamaan%20Pai%20Gaiyaan-(Mr-Jatt.com)149889c.mp3");
    }


    public static String getRediredLink(String key)
    {
        return redirectLinks.get(key);
    }




}
