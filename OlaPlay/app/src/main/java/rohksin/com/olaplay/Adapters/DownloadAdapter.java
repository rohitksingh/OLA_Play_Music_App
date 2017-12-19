package rohksin.com.olaplay.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rohksin.com.olaplay.Callbacks.DownloadTemListener;
import rohksin.com.olaplay.R;

/**
 * Created by Illuminati on 12/19/2017.
 */



public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownLoadViewHolder> {

    private Context context;
    private List<String> list;
    private DownloadTemListener listener;

    public DownloadAdapter(Context context, List<String> list)
    {
        this.context = context;
        this.list = list;
        listener = (DownloadTemListener)context;
    }


    @Override
    public DownLoadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.downloaded_item,parent,false);
        return new DownLoadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DownLoadViewHolder holder, final int position) {

        final String fileName = list.get(position);
        holder.downLoededFile.setText(fileName);
        holder.shareSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.shareItem(fileName);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DownLoadViewHolder extends RecyclerView.ViewHolder
    {
        private TextView downLoededFile;
        private ImageView shareSong;

        public DownLoadViewHolder(View itemView) {
            super(itemView);
            downLoededFile = (TextView)itemView.findViewById(R.id.history);
            shareSong = (ImageView)itemView.findViewById(R.id.deleteHistory);
        }
    }
}
