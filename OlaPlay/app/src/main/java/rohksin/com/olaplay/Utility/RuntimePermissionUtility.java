package rohksin.com.olaplay.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rohksin.com.olaplay.Activities.MusicTestActivity;

/**
 * Created by Illuminati on 12/19/2017.
 */

public class RuntimePermissionUtility {


    private static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 878;


    public static void checkExternalStoragePermission(Context context)
    {
        if(!ifHasPermission(context))
        {
            requestPermission(context);
        }

    }


    private static boolean ifHasPermission(Context context)
    {

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return permissionCheck == PackageManager.PERMISSION_GRANTED;

    }

    private static void requestPermission(Context context)
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity)context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


            Toast.makeText(context, "Hey provide the permission",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


        }
    }


}
