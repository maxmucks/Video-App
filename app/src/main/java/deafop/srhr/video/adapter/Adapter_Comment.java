package deafop.srhr.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import deafop.srhr.video.item.itemComment;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;


public class Adapter_Comment extends RecyclerView.Adapter {
    private Methods methods;
    private ArrayList<itemComment> arrayList;
    private Context context;

    private RecyclerItemClickListener listener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int columnWidth = 0;

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title, textView_comment_adapter;

        ImageView imageView_comment;

        private MyViewHolder(View view) {
            super(view);
            txt_title = (TextView) view.findViewById(R.id.txt_title);
            textView_comment_adapter= (TextView) view.findViewById(R.id.textView_comment_adapter);
            imageView_comment = (ImageView) view.findViewById(R.id.imageView_comment);
        }

    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public Adapter_Comment(Context context, ArrayList<itemComment> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
        methods = new Methods(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            final itemComment item = arrayList.get(position);
            ((MyViewHolder) holder).txt_title.setText(item.getUser_name());
            ((MyViewHolder) holder).textView_comment_adapter.setText(item.getComment_text());

            int step = 1;
            int final_step = 1;
            for (int i = 1; i < position + 1; i++) {
                if (i == position + 1) {
                    final_step = step;
                }
                step++;
                if (step > 6) {
                    step = 1;
                }
            }

            switch (step) {
                case 1:
                    Picasso.get()
                            .load(R.drawable.ic_person_flat)
                            .placeholder(R.drawable.ic_person_flat)
                            .into(((MyViewHolder) holder).imageView_comment);
                    break;
                case 2:
                    Picasso.get()
                            .load(R.drawable.ic_person_flat2)
                            .placeholder(R.drawable.ic_person_flat2)
                            .into(((MyViewHolder) holder).imageView_comment);
                    break;
                case 3:
                    Picasso.get()
                            .load(R.drawable.ic_person_flat3)
                            .placeholder(R.drawable.ic_person_flat3)
                            .into(((MyViewHolder) holder).imageView_comment);
                    break;
                case 4:
                    Picasso.get()
                            .load(R.drawable.ic_person_flat4)
                            .placeholder(R.drawable.ic_person_flat4)
                            .into(((MyViewHolder) holder).imageView_comment);
                     break;
                case 5:
                    Picasso.get()
                            .load(R.drawable.ic_person_flat5)
                            .placeholder(R.drawable.ic_person_flat5)
                            .into(((MyViewHolder) holder).imageView_comment);
                    break;
                case 6:
                    Picasso.get()
                            .load(R.drawable.ic_person_flat6)
                            .placeholder(R.drawable.ic_person_flat6)
                            .into(((MyViewHolder) holder).imageView_comment);
                    break;

            }

        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }


    public interface RecyclerItemClickListener{
        void onClickListener(itemComment listltem, int position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
    }
}