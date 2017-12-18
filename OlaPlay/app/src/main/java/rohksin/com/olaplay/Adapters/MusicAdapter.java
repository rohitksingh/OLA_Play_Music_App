package rohksin.com.olaplay.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rohksin.com.olaplay.Activities.DevelperProfileActivity;
import rohksin.com.olaplay.Activities.DownloadedActivity;
import rohksin.com.olaplay.Callbacks.AdapterItemListener;
import rohksin.com.olaplay.POJO.Music;
import rohksin.com.olaplay.R;

/**
 * Created by Illuminati on 12/15/2017.
 */

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int USER_ACTIVITY_LAYOUT= 0;
    private static final int MUSIC_ITEM_LAYOUT= 1;

    private List<Music> musicList;
    private List<Music> copyMusicList;
    private Context context;

    AdapterItemListener listener;

    public MusicAdapter(Context context, List<Music> musicList)
    {
        this.context = context;
        this.musicList = musicList;
        copyMusicList = new ArrayList<Music>();
        copyMusicList.addAll(musicList);
        listener = (AdapterItemListener)context;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position==0)
            return USER_ACTIVITY_LAYOUT;
        else
            return MUSIC_ITEM_LAYOUT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =null;
        RecyclerView.ViewHolder viewHolder = null;

        if(viewType==USER_ACTIVITY_LAYOUT)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_past_activity_item,parent,false);
            viewHolder = new UserActivityViewHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item,parent,false);
            viewHolder= new MusicViewHolder(view);
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if(holder.getItemViewType()== USER_ACTIVITY_LAYOUT)
        {
            UserActivityViewHolder userActivityViewHolder = (UserActivityViewHolder)holder;
            updateLoginSuccess(userActivityViewHolder);
        }
        else {

            MusicViewHolder musicViewHolder = (MusicViewHolder)holder;

            final Music music = musicList.get(position-1);             ///// Check what if list is empty
            musicViewHolder.songName.setText(music.getSong());
            musicViewHolder.artist.setText(music.getArtists());

            Glide.with(context)
                    .load(music.getCover_image())
                    .centerCrop()
                    .into(musicViewHolder.cover_image);

            musicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemTouch(position-1);
                }
            });

            musicViewHolder.downLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.download(position-1);
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return musicList.size()+1;
    }


    //*********************************************************************
    //  FILTER LOGIC
    //*********************************************************************

    public void filter(String text)
    {

        musicList.clear();

        if(text.isEmpty())
        {
            musicList.addAll(copyMusicList);
        }
        else
        {
            for (Music music: copyMusicList)
            {
                if(music.getSong().toLowerCase().contains(text.toLowerCase())) {
                    musicList.add(music);
                }
            }
        }
        notifyDataSetChanged();
    }

    class UserActivityViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImage;
        private TextView downloads;
        private TextView devProfile;
        private TextView userName;
        private TextView signOut;

        public UserActivityViewHolder(View itemView) {
            super(itemView);

            userImage = (CircleImageView) itemView.findViewById(R.id.userProfile);
            downloads = (TextView)itemView.findViewById(R.id.downloadFiles);
            devProfile = (TextView)itemView.findViewById(R.id.devProfile);
            userName = (TextView)itemView.findViewById(R.id.userName);
            signOut = (TextView)itemView.findViewById(R.id.signOut);

            devProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, DevelperProfileActivity.class));
                }
            });

            downloads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, DownloadedActivity.class));
                }
            });

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.firebaseAuthentication();
                }
            });

            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.firebaseSignOut();
                }
            });
        }
    }

    class MusicViewHolder extends RecyclerView.ViewHolder{

        private ImageView cover_image;
        private TextView songName;
        private ImageView downLoad;
        protected TextView artist;


        public MusicViewHolder(View itemView) {
            super(itemView);
            cover_image = (ImageView)itemView.findViewById(R.id.cover_image);
            songName = (TextView)itemView.findViewById(R.id.song_name);
            artist = (TextView)itemView.findViewById(R.id.artists);
            downLoad = (ImageView)itemView.findViewById(R.id.download);

        }

    }

    //*********************************************************************
    //  HELPER METHOD
    //  Updates user image and detail when user logs in
    //*********************************************************************


    public void updateLoginSuccess(UserActivityViewHolder viewHolder)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            Glide.with(context)
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .into(viewHolder.userImage);
            viewHolder.userName.setText(user.getDisplayName()+"\n"+user.getEmail());
            viewHolder.signOut.setVisibility(View.VISIBLE);

        }
        else {
            viewHolder.userImage.setImageResource(R.drawable.user_placeholder);
            viewHolder.signOut.setVisibility(View.GONE);
            viewHolder.userName.setText("Not signed in");

        }

    }

}
