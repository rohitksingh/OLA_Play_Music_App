package rohksin.com.olaplay.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import rohksin.com.olaplay.Adapters.DownloadAdapter;
import rohksin.com.olaplay.Callbacks.DownloadTemListener;
import rohksin.com.olaplay.R;
import rohksin.com.olaplay.Utility.AppUtility;

/**
 * Created by Illuminati on 12/17/2017.
 */

public class DownloadedActivity extends AppCompatActivity implements DownloadTemListener {

    @BindView(R.id.downloadedView)
    RecyclerView downLoadedView;

    @BindView(R.id.seeAllFiles)
    Button seeAllFiles;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloaded_activity);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Downloaded Files");

        String[] files =  AppUtility.getMainExternalFolder().list();
        LinearLayoutManager llm = new LinearLayoutManager(DownloadedActivity.this);
        downLoadedView.setLayoutManager(llm);
        DownloadAdapter adapter = new DownloadAdapter(DownloadedActivity.this, Arrays.asList(files));
        downLoadedView.setAdapter(adapter);

        seeAllFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtility.viewAllFilesInFileManger(DownloadedActivity.this);
            }
        });

    }

    @Override
    public void shareItem(String url) {
        AppUtility.shareDownloadedSong(DownloadedActivity.this,url);
    }
}
