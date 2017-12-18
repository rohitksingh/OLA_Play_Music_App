package rohksin.com.olaplay.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rohksin.com.olaplay.R;

/**
 * Created by Illuminati on 12/16/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<String> list;

    public HistoryAdapter(Context context, List<String> list)
    {
        this.context = context;
        this.list = list;
    }


    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.history_item,parent,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        String history = list.get(position);
        holder.history.setText(history);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder
    {

        private TextView history;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            history = (TextView)itemView.findViewById(R.id.history);
        }
    }
}
