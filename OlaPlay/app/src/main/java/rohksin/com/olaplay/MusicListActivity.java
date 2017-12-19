package rohksin.com.olaplay;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.transition.Fade;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rohksin.com.olaplay.Activities.MusicActivity;
import rohksin.com.olaplay.AsyncTasks.MusicTask;
import rohksin.com.olaplay.Activities.SearchActivity;
import rohksin.com.olaplay.Adapters.MusicAdapter;
import rohksin.com.olaplay.Callbacks.AdapterItemListener;
import rohksin.com.olaplay.POJO.Music;
import rohksin.com.olaplay.Services.MediaPlayerService;
import rohksin.com.olaplay.Utility.AppUtility;

public class MusicListActivity extends AppCompatActivity implements AdapterItemListener,GoogleApiClient.OnConnectionFailedListener {

    private MusicAdapter musicAdapter;
    private List<Music> musicList;
    private int currentPalingIndex =-1;

    private RequestQueue requestQueue;

    //private MusicTask musicTask;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private int GOOGLE_SIGN_IN_REQ_CODE= 121;

    private boolean isSongPlaying;
    private int playButtonBackgroung = R.drawable.play_big;
    private int pauseButtonBackgroung = R.drawable.pause_big;


   //  Music Service Related *****************************
    private MediaPlayerService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
   //  *****************************************************


    @BindView(R.id.musicRecycletView)
    RecyclerView musicRecyclerView;

    @BindView(R.id.currentlyPlayingImage)
    ImageView currentSongImage;

    @BindView(R.id.currentlyPlayingSong)
    TextView currentSongName;

    @BindView(R.id.playCurrentSong)
    Button playCurrentSong;

    @BindView(R.id.playNext)
    Button playNext;

    @BindView(R.id.currentlyPlayingArtists)
    TextView currentlyPlayingArtist;

    private boolean doubleTapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.music_list_activity);
        ButterKnife.bind(this);
        registerReceiver();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        LinearLayoutManager llm = new LinearLayoutManager(MusicListActivity.this);
        musicRecyclerView.setLayoutManager(llm);
        setUpList();
        setUpCurrentPlaying();

        if(savedInstanceState!=null)
            currentPalingIndex = savedInstanceState.getInt("CURRENT_PLAYING_INDEX");

    }


    //*********************************************************************
    //  MENU RELATED
    //*********************************************************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_music_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.search_toolbar_button) {

            Intent intent = new Intent(MusicListActivity.this, SearchActivity.class);
            intent.putExtra(AppUtility.MUSIC_LIST,new ArrayList<Music>(musicList));

            if (Build.VERSION.SDK_INT > 20) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MusicListActivity.this);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //********************************************************************************
    //    ADAPTER CALLBACK
    //    Notifies which item is selected to update the bottom current plaing song
    //********************************************************************************


    public void processMusic()
    {

    }



    @Override
    public void itemTouch(int index) {

        currentPalingIndex = index;

        if(index==-1)
        {
            currentPalingIndex =0;
        }


        setUpCurentPlayingSectionView(index);
        /*
        final Music music = musicList.get(currentPalingIndex);
        currentSongName.setText(music.getSong());
        currentlyPlayingArtist.setText(music.getArtists());

        Glide.with(MusicListActivity.this)
                .load(music.getCover_image())
                .centerCrop()
                .into(currentSongImage);
         */

        final Music music = musicList.get(currentPalingIndex);

        if(index==-1)
        {
            currentPalingIndex =0;
        }
        else
        {
            isSongPlaying = true;
            musicSrv.processSong(music.getUrl());
            updateButton();
        }

    }


    @Override
    public void download(int index) {
        Music music = musicList.get(index);
        Toast.makeText(MusicListActivity.this,"Downloading "+music.getSong(),Toast.LENGTH_SHORT).show();
        AppUtility.downLoadSong(this, music.getUrl(),music.getSong());
    }

    @Override
    public void firebaseAuthentication() {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null)
        googleSignUp();

    }

    @Override
    public void firebaseSignOut() {
        googleSignOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //*********************************************************************
    //     BROADCAST RECEIVER
    //     Notified when parsing is done
    //*********************************************************************
    class ResponseReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AppUtility.MUSIC_LIST_RECEIVED))
            {
                String response = intent.getStringExtra(AppUtility.MUSIC_LIST);
                musicList = parseWithMoshi(response);
                setUpList();
                //itemTouch(currentPalingIndex);

                setUpCurentPlayingSectionView(currentPalingIndex);

            }
        }
    }


    //*********************************************************************
    //    SYSTEM CALLBACKS
    //*********************************************************************

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MediaPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
         bundle.putInt("CURRENT_PLAYING_INDEX",currentPalingIndex);
    }


    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        if(reqCode==AppUtility.MUSIC_ACTIVITY_REQ_CODE)
        {
            currentPalingIndex = data.getIntExtra(AppUtility.CURRENT_INDEX,0);
            //itemTouch(currentPalingIndex);
            setUpCurentPlayingSectionView(currentPalingIndex);
        }

        if(reqCode == GOOGLE_SIGN_IN_REQ_CODE)
        {

            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(signInResult.isSuccess())
            {

                GoogleSignInAccount googleSignInAccount = signInResult.getSignInAccount();
                fireBaseAuthWithProviderAccount(googleSignInAccount);

            }
            else
            {
                Log.d("Auth", "in resultActivity fail");
            }

        }

    }

    //*********************************************************************
    //      PRIVATE HELPER METHODS
    //*********************************************************************


    private void makeMusicRequest()
    {
        requestQueue = Volley.newRequestQueue(MusicListActivity.this);

        JsonArrayRequest request = new JsonArrayRequest( AppUtility.URL_END_POINT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Intent intent = new Intent(AppUtility.MUSIC_LIST_RECEIVED);
                        intent.putExtra(AppUtility.MUSIC_LIST, response.toString());
                        sendBroadcast(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }


    private List<Music> getMusicList()
    {
        if(musicList==null) {
            musicList = new ArrayList<Music>();
        }
        return musicList;
    }


    private List<Music> parseWithMoshi(String response)
    {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, Music.class);
        JsonAdapter<List<Music>> adapter = moshi.adapter(type);
        List<Music>  songs = null;
        try {
            songs = adapter.fromJson(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return songs;
    }


    private void setUpList()
    {
        Log.d("ListSize",getMusicList().size()+"");
        musicAdapter = new MusicAdapter(MusicListActivity.this,getMusicList());
        musicRecyclerView.setAdapter(musicAdapter);
    }


    public void setUpCurentPlayingSectionView(int index)
    {
        if(index==-1)
            index =0;

        final Music music = musicList.get(index);
        currentSongName.setText(music.getSong());
        currentlyPlayingArtist.setText(music.getArtists());

        Glide.with(MusicListActivity.this)
                .load(music.getCover_image())
                .centerCrop()
                .into(currentSongImage);
    }

    private void setUpCurrentPlaying()
    {

       // Log.d("WHAT IS STATUS", musicSrv.isSongPlaying()+"");

       // updateButton();

        playCurrentSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentPalingIndex==-1)
                    currentPalingIndex=0;
                itemTouch(currentPalingIndex);
                //updateButton();

            }
        });


        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = currentPalingIndex+1;
                if(index<musicList.size())
                    itemTouch(index);
            }
        });


        currentSongImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MusicListActivity.this, MusicActivity.class);
                intent.putExtra(AppUtility.MUSIC_LIST,new ArrayList<Music>(musicList));
                intent.putExtra(AppUtility.CURRENT_INDEX,currentPalingIndex);
                intent.putExtra(AppUtility.MUSIC_PLAYING,isSongPlaying);
                startActivityForResult(intent, AppUtility.MUSIC_ACTIVITY_REQ_CODE);
            }
        });
    }


    public void updateButton()
    {

        if(musicSrv==null)
        {
            Log.d("PUTTON STATUS","NULL");
            playCurrentSong.setBackgroundResource(playButtonBackgroung);

        }
        else {

            Log.d("PUTTON STATUS","NOT NULL");

            if(musicSrv.isTrackPlaying())
            {
                Log.d("PUTTON STATUS","Track Playing");
                playCurrentSong.setBackgroundResource(pauseButtonBackgroung);
            }
            else {
                Log.d("PUTTON STATUS","Track Not Playing");
                playCurrentSong.setBackgroundResource(playButtonBackgroung);
            }
        }
    }



    // Double Tap to Exit
    @Override
    public void onBackPressed()
    {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleTapped = false;

            }
        }, 2000);

        if(doubleTapped)
        {
            super.onBackPressed();
        }
        else {
            Toast.makeText(MusicListActivity.this,"Press again to exit",Toast.LENGTH_SHORT).show();
            doubleTapped = true;
        }
    }


    //*********************************************************************
    //  OAUTH AUTHENTICATION
    //*********************************************************************


    public void googleSignOut()
    {
        firebaseAuth.signOut();
        currentUser=null;
        musicAdapter.notifyDataSetChanged();
    }

    public void googleSignUp()
    {
        GoogleSignInOptions gso  = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQ_CODE);
    }


    //******************************************************************************
    //  Could be made generic in case of mre OAUTH Providers eg facebook, twitter
    //*******************************************************************************

    private void fireBaseAuthWithProviderAccount(GoogleSignInAccount googleSignInAccount)
    {

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful())
                        {
                            Log.d("GMAIL LOGIN SUCCESS","Scuess");
                            musicAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Log.d("Auth","Firebase Auth not success "+task.getException() );
                        }

                    }
                });

    }


    //*********************************************************************
    //  ENABLES SHARED TRANSITION BETWEEN ACTIVITIES
    //*********************************************************************

    private void setAnimation()
    {
        Fade fade = new Fade();
        fade.setDuration(1000);
        if(Build.VERSION.SDK_INT>20) {
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
    }






    //***************************************************************
    // Music Service part
    //***************************************************************


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




    //*****************************************************
    // EXTRA
    //****************************************************


    private void registerReceiver()
    {
        IntentFilter filter = new IntentFilter(AppUtility.MUSIC_LIST_RECEIVED);
        registerReceiver(new ResponseReceiver(),filter);
        makeMusicRequest();
    }


}
