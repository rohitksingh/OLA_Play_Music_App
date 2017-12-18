package rohksin.com.olaplay.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import rohksin.com.olaplay.Callbacks.AdapterItemListener;
import rohksin.com.olaplay.POJO.Music;
import rohksin.com.olaplay.R;

/**
 * Created by Illuminati on 12/16/2017.
 */

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultHolder> {

    private Context context;
    private List<Music> list;
    private List<Music> copyMusicList;
    private AdapterItemListener listener;

    public ResultAdapter(Context context, List<Music> list)
    {
        this.context = context;
        this.list = list;
        copyMusicList = new ArrayList<Music>();
        copyMusicList.addAll(list);

        listener = (AdapterItemListener)context;
    }


    @Override
    public ResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.music_list_item,parent,false);
        return new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultHolder holder, final int position) {

        Music music = list.get(position);
        holder.songName.setText(music.getSong());
        Glide.with(context)
                .load(music.getCover_image())
                .centerCrop()
                .into(holder.cover_image);
        holder.downLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.download(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //*********************************************************************
    //  FILTER LOGIC
    //*********************************************************************


    public void filter(String text)
    {

        list.clear();

        if(text.isEmpty())
        {
            list.addAll(copyMusicList);
        }
        else
        {
            for (Music music: copyMusicList)
            {
                if(music.getSong().toLowerCase().contains(text.toLowerCase())) {
                    list.add(music);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ResultHolder extends RecyclerView.ViewHolder{

        private ImageView cover_image;
        private TextView songName;
        private ImageView downLoad;

        public ResultHolder(View itemView) {
            super(itemView);
            cover_image = (ImageView)itemView.findViewById(R.id.cover_image);
            songName = (TextView)itemView.findViewById(R.id.song_name);
            downLoad = (ImageView)itemView.findViewById(R.id.download);
        }
    }
}
