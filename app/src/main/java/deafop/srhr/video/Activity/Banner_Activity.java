package deafop.srhr.video.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import deafop.srhr.video.Activity.video.All_PlayerActivity;
import deafop.srhr.video.EndlessRecycler.EndlessRecyclerViewScrollListener;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.adapter.AdapterVideo;
import deafop.srhr.video.interfaces.InterAdListener;
import deafop.srhr.video.interfaces.RecyclerViewClickListener;


public class Banner_Activity extends AppCompatActivity {

    Methods methods;
    RecyclerView recyclerView;
    AdapterVideo adapter;
    ArrayList<itemVideo> arrayList, arrayListTemp;
    Boolean isOver = false, isScroll = false;
    GridLayoutManager grid;
    FloatingActionButton fab;
    ProgressBar progressBar;
    private int nativeAdPos = 0;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode ) {
            setTheme(R.style.AppTheme2);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);


        if(Setting.isAdmobNativeAd) {
            nativeAdPos = Setting.admobNativeShow;
        } else if(Setting.isFBNativeAd) {
            nativeAdPos = Setting.fbNativeShow;
        }

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        position = getIntent().getIntExtra("pos", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("All Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();
        progressBar = findViewById(R.id.load_video);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.tv);
        recyclerView.setHasFixedSize(true);

        grid = new GridLayoutManager(this, 1);
        grid.setSpanCount(1);

        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter.getItemViewType(position) >= 1000 || adapter.isHeader(position)) ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    isOver = true;
                    try {
                        adapter.hideHeader();
                    }catch (Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        e.printStackTrace();
                    }
                } else {
                    adapter.hideHeader();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = grid.findFirstVisibleItemPosition();

                if (firstVisibleItem > 6) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });


        if (Setting.purchase_code.equals(Setting.itemAbout.getPurchase_code())){
            getData();
        }

        LinearLayout adView = findViewById(R.id.adView);
        methods.showBannerAd(adView);

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int real_pos = adapter.getRealPos(position, arrayListTemp);

                Intent intent = new Intent(Banner_Activity.this, All_PlayerActivity.class);
                intent.putExtra("pos", real_pos);
                Setting.arrayList.clear();
                Setting.arrayList.addAll(arrayListTemp);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        arrayList = (ArrayList<itemVideo>)Setting.arrayList2.get(position).getArrayListSongs();
        arrayListTemp = (ArrayList<itemVideo>)Setting.arrayList2.get(position).getArrayListSongs();
        setAdapter();
        progressBar.setVisibility(View.GONE);
    }


    private void setEmpty() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new AdapterVideo(Banner_Activity.this,  arrayList,  new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                }
            });

            recyclerView.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        if(adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
