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

import java.text.DecimalFormat;
import java.util.List;

import deafop.srhr.video.Activity.video.All_PlayerActivity;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.interfaces.InterAdListener;


public class Home_VideoAdapter extends RecyclerView.Adapter<Home_VideoAdapter.ViewHolder> {

    private List<itemVideo> itemVideo_video;
    private Context context;
    Methods methods;

    public Home_VideoAdapter(List<itemVideo> itemVideo_video, Context context) {
        this.itemVideo_video = itemVideo_video;
        this.context = context;
        methods = new Methods(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_home_video,parent, false);
        return new ViewHolder(v);
    }

    public String format(Number number) {
        char[] arrc = new char[]{' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long l = number.longValue();
        double d = l;
        int n = (int)Math.floor((double)Math.log10((double)d));
        int n2 = n / 3;
        if (n >= 3 && n2 < arrc.length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new DecimalFormat("#0.0").format(d / Math.pow((double)10.0, (double)(n2 * 3))));
            stringBuilder.append(arrc[n2]);
            return stringBuilder.toString();
        }
        return new DecimalFormat("#,##0").format(l);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final itemVideo itemVideo_videos = itemVideo_video.get(position);

        holder.name.setText(itemVideo_videos.getVideo_title());
        holder.category_name.setText(itemVideo_videos.getCategory_name());
        holder.textView_views.setText(format(Double.parseDouble((String) itemVideo_videos.getTotal_views())));

        switch (itemVideo_videos.getVideo_type()) {
            case "fb":
                Picasso.get().load(itemVideo_videos.getVideo_thumbnail()).placeholder(R.drawable.fb).into(holder.imageView);
                break;
            case "local":
                Picasso.get().load(itemVideo_videos.getVideo_thumbnail()).placeholder(R.drawable.local).into(holder.imageView);
                break;
            case "server_url":
                Picasso.get().load(itemVideo_videos.getVideo_thumbnail()).placeholder(R.drawable.url_im).into(holder.imageView);
                break;
            case "youtube":
                Picasso.get().load(Setting.YOUTUBE_IMAGE_FRONT + itemVideo_videos.getVideo_id() + Setting.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.youtube).into(holder.imageView);
                break;
            case "dailymotion":
                Picasso.get().load(Setting.DAILYMOTION_IMAGE_PATH + itemVideo_videos.getVideo_id()).placeholder(R.drawable.dailymotion).into(holder.imageView);
                break;
            case "vimeo":
                Picasso.get().load(itemVideo_videos.getVideo_thumbnail()).placeholder(R.drawable.vimeo).into(holder.imageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemVideo_video.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name, category_name;
        public ImageView imageView;
        public LinearLayout linearLayout;
        public TextView textView_views;


        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            category_name = (TextView) itemView.findViewById(R.id.category_name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView_views = (TextView)itemView.findViewById(R.id.view);


            methods = new Methods(context, new InterAdListener() {
                @Override
                public void onClick(int position, String type) {
                    Intent intent = new Intent(context, All_PlayerActivity.class);
                    intent.putExtra("pos", position);
                    Setting.arrayList.clear();
                    Setting.arrayList.addAll(itemVideo_video);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        methods.showInter(pos, "");
                    }
                }
            });

        }
    }
}
