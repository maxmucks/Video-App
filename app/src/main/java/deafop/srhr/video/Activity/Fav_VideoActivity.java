package deafop.srhr.video.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import deafop.srhr.video.DBHelper.DBHelper;
import deafop.srhr.video.EndlessRecycler.EndlessRecyclerViewScrollListener;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.adapter.AdapterVideo;
import deafop.srhr.video.asyncTask.LoadLatest;
import deafop.srhr.video.interfaces.InterAdListener;
import deafop.srhr.video.interfaces.RecyclerViewClickListener;



public class Fav_VideoActivity extends AppCompatActivity {

    DBHelper dbHelper;
    Methods methods;
    RecyclerView recyclerView;
    AdapterVideo adapter;
    ArrayList<itemVideo> arrayList;
    Boolean isOver = false, isScroll = false;
    int page = 1;
    GridLayoutManager grid;
    LoadLatest load;
    FloatingActionButton fab;
    ProgressBar progressBar;
    private int nativeAdPos = 0;

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
        dbHelper = new DBHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.favourite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        arrayList = new ArrayList<>();

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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isOver = false;
                            try {
                                adapter.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0);
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


        arrayList.clear();
        arrayList.addAll(dbHelper.getVideo(DBHelper.TABLE_FAV_VIDEO));

        setAdapter();

        LinearLayout adView = findViewById(R.id.adView);
        methods.showBannerAd(adView);

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int real_pos = adapter.getRealPos(position, arrayList);
                Intent intent = new Intent(Fav_VideoActivity.this, All_PlayerActivity.class);
                intent.putExtra("pos", real_pos);
                Setting.arrayList.clear();
                Setting.arrayList.addAll(arrayList);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }



    private void setEmpty() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new AdapterVideo(Fav_VideoActivity.this,  arrayList,  new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position,"");
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
