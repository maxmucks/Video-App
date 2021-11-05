package deafop.srhr.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import deafop.srhr.video.item.itemCategory;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.interfaces.RecyclerViewClickListener;

/**
 * Created by thiivakaran
 */
public class AdapterCatHome extends RecyclerView.Adapter<AdapterCatHome.MyViewHolder> {

    private Context context;
    private ArrayList<itemCategory> arrayList;
    private Methods methods;
    private RecyclerViewClickListener recyclerViewClickListener;

    class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView news_image;
        TextView textView, title2;
        public LinearLayout clek;

        MyViewHolder(View view) {
            super(view);
            news_image = view.findViewById(R.id.cat_image);
            textView = view.findViewById(R.id.title);
            title2 = view.findViewById(R.id.title2);
            clek = view.findViewById(R.id.clek);
        }
    }

    public AdapterCatHome(Context context, ArrayList<itemCategory> arrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.recyclerViewClickListener = recyclerViewClickListener;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_cat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final itemCategory item = arrayList.get(position);
        holder.textView.setText(item.getCategory_name());
        holder.title2.setText(item.getCategory_name());

        int step = 1;
        int final_step = 1;
        for (int i = 1; i < position + 1; i++) {
            if (i == position + 1) {
                final_step = step;
            }
            step++;
            if (step > 5) {
                step = 1;
            }
        }

        switch (step) {
            case 1:
                Picasso.get()
                        .load(R.color.md_blue_400)
                        .placeholder(R.color.md_blue_400)
                        .into(holder.news_image);
                break;
            case 2:
                Picasso.get()
                        .load(R.color.md_green_400)
                        .placeholder(R.color.md_green_400)
                        .into(holder.news_image);
                break;
            case 3:
                Picasso.get()
                        .load(R.color.md_blue_grey_400)
                        .placeholder(R.color.md_blue_grey_400)
                        .into(holder.news_image);
                break;
            case 4:
                Picasso.get()
                        .load(R.color.md_deep_orange_400)
                        .placeholder(R.color.md_deep_orange_400)
                        .into(holder.news_image);
                break;
            case 5:
                Picasso.get()
                        .load(R.color.md_pink_400)
                        .placeholder(R.color.md_pink_400)
                        .into(holder.news_image);
                break;

        }

        holder.clek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onClick(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getID(int pos) {
        return arrayList.get(pos).getCid();
    }
}