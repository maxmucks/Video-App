package deafop.srhr.video.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import deafop.srhr.video.Activity.VideoActivity;
import deafop.srhr.video.item.itemCategory;
import deafop.srhr.video.R;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<itemCategory> listltems;
    private Context context;

    public CategoryAdapter(List<itemCategory> listltems, Context context) {
        this.listltems = listltems;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_category_new,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final itemCategory listltem = listltems.get(position);

        holder.name.setText(listltem.getCategory_name());

        //load album cover using picasso
        Picasso.get()
                .load(listltem.getCategory_image())
                .placeholder(R.color.colorAccent_Light)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listltems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);

            //on item click
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(context, VideoActivity.class);
                        intent.putExtra("name", listltems.get(pos).getCategory_name());
                        intent.putExtra("cid", listltems.get(pos).getCid());
                        intent.putExtra("image", listltems.get(pos).getCategory_image());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

}
