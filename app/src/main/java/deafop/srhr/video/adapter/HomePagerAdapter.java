package deafop.srhr.video.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;

import java.util.ArrayList;

import deafop.srhr.video.Activity.Banner_Activity;
import deafop.srhr.video.item.ItemHomeBanner;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;


public class HomePagerAdapter extends EnchantedViewPagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<ItemHomeBanner> arrayList;
    private Methods methods;

    public HomePagerAdapter(Context context, ArrayList<ItemHomeBanner> arrayList) {
        super(arrayList);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View mCurrentView = inflater.inflate(R.layout.layout_home_banner, container, false);

        TextView tv_title, tv_desc, tv_total;
        ImageView iv_banner, iv_forward;
        View view_gradient;

        tv_title = mCurrentView.findViewById(R.id.tv_home_banner);
        tv_desc = mCurrentView.findViewById(R.id.tv_home_banner_desc);
        tv_total = mCurrentView.findViewById(R.id.tv_home_banner_total);
        iv_banner = mCurrentView.findViewById(R.id.iv_home_banner);
        iv_forward = mCurrentView.findViewById(R.id.iv3);
        view_gradient = mCurrentView.findViewById(R.id.view_home_banner);

        iv_forward.setColorFilter(Color.WHITE);
        tv_title.setText(arrayList.get(position).getTitle());
        tv_desc.setText(arrayList.get(position).getDesc());
        tv_total.setText(arrayList.get(position).getTotalSong() + " " + "Videos");
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.color.md_cyan_50)
                .into(iv_banner);

        new LoadColor(view_gradient, tv_title).execute(arrayList.get(position).getImage());

        iv_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Banner_Activity.class);
                intent.putExtra("pos", position);
                Setting.arrayList2.clear();
                Setting.arrayList2.addAll(arrayList);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        mCurrentView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(mCurrentView);

        return mCurrentView;
    }

    public class LoadColor extends AsyncTask<String, String, String> {

        Bitmap bitmap;
        TextView textView;
        View view;

        LoadColor(View view, TextView textView) {
            this.view = view;
            this.textView = textView;
        }

        @Override
        protected String doInBackground(String... strings) {
            bitmap = methods.getBitmapFromURL(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        int defaultValue = 0x000000;
                        int vibrant = palette.getVibrantColor(defaultValue);
                        view.setBackground(methods.getGradientDrawable(vibrant, Color.parseColor("#00000000")));
                    }
                });
                super.onPostExecute(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

}