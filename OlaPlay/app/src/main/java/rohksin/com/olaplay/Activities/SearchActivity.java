package rohksin.com.olaplay.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rohksin.com.olaplay.Adapters.HistoryAdapter;
import rohksin.com.olaplay.Adapters.ResultAdapter;
import rohksin.com.olaplay.Callbacks.AdapterItemListener;
import rohksin.com.olaplay.Callbacks.HistoryItemListener;
import rohksin.com.olaplay.Database.OlaPlayDatabaseHelper;
import rohksin.com.olaplay.POJO.Music;
import rohksin.com.olaplay.R;
import rohksin.com.olaplay.Utility.AppUtility;

/**
 * Created by Illuminati on 12/16/2017.
 */

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterItemListener, HistoryItemListener {


    private OlaPlayDatabaseHelper databaseHelper;

    private HistoryAdapter historyAdapter;
    private ResultAdapter musicAdapter;
    private List<String> history;
    private List<Music> musicList;

    @BindView(R.id.historyResult)
    RecyclerView historyRecyclerView;

    @BindView(R.id.searchResult)
    RecyclerView resultRecyclerView;

    @BindView(R.id.historyText)
    TextView searchHistory;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        ButterKnife.bind(this);
        setUpUi();
        setUpSQLiteDatabase();

    }


    //*********************************************************************
    //  PRIVATE METHODS
    //*********************************************************************

    private void setUpUi()
    {
        LinearLayoutManager llm = new LinearLayoutManager(SearchActivity.this);
        LinearLayoutManager llm2 = new LinearLayoutManager(SearchActivity.this);
        historyRecyclerView.setLayoutManager(llm);
        resultRecyclerView.setLayoutManager(llm2);
        musicList = (ArrayList<Music>)getIntent().getSerializableExtra(AppUtility.MUSIC_LIST);
        setUpMusicList();
    }

    private void setUpSQLiteDatabase()
    {
        databaseHelper = new OlaPlayDatabaseHelper(SearchActivity.this);
        setUpHistoryList();
        history = getHistory();
    }

    private List<String> getHistory()
    {
        return databaseHelper.getHistoryList();
    }

    private void setUpHistoryList()
    {
        historyAdapter = new HistoryAdapter(SearchActivity.this,getHistory());
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void setUpMusicList()
    {
        musicAdapter = new ResultAdapter(SearchActivity.this, musicList);
        resultRecyclerView.setAdapter(musicAdapter);
    }


    //*********************************************************************
    //  MENU RELATED
    //*********************************************************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

          //  SearchView implementation

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem mSearchmenuItem = menu.findItem(R.id.menu_toolbarsearch);
        SearchView searchView = (SearchView) mSearchmenuItem.getActionView();
        searchView.setQueryHint("Search Songs");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        return true;
    }


    //*********************************************************************
    //  SEARCHVIEW CALLBACKS
    //*********************************************************************


    @Override
    public boolean onQueryTextSubmit(String query) {

        // This can be used for SQLite Database

        history = getHistory();

        boolean alreadyExist = false;

        for(String data: history)
        {
            Log.d("ALAJKA",data);
            if(data.equals(query))
            {
                alreadyExist = true;
                break;
            }
        }

        if(!alreadyExist)
        databaseHelper.addHistory(query);

        return true;

    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.length()==0)
        {
            historyRecyclerView.setVisibility(View.VISIBLE);
            searchHistory.setVisibility(View.VISIBLE);
            resultRecyclerView.setVisibility(View.GONE);
        }
        else
        {
            historyRecyclerView.setVisibility(View.GONE);
            searchHistory.setVisibility(View.GONE);
            resultRecyclerView.setVisibility(View.VISIBLE);
            musicAdapter.filter(newText);
        }

        return true;
    }


    @Override
    public void itemTouch(int index) {

    }

    @Override
    public void download(int index) {
        Music music = musicList.get(index);
        AppUtility.downLoadSongToexternalStorage(SearchActivity.this,music.getUrl(),music.getSong());
    }

    @Override
    public void firebaseAuthentication() {

    }

    @Override
    public void firebaseSignOut() {

    }

    //*********************************************************************
    //  HELPER METHODS
    //*********************************************************************


    public void setAnimation()
    {
        Fade fade = new Fade();
        fade.setDuration(1000);
        if(Build.VERSION.SDK_INT>20) {
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
    }

    @Override
    public void deleteItemAt(int index) {

        Log.d("Delete click","Yae");
        databaseHelper.deleteHistory(history.get(index));
        history.remove(index);
        historyAdapter.notifyItemChanged(index);
    }
}
