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
import deafop.srhr.video.EndlessRecycler.EndlessRecyclerViewScrollListener;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.adapter.AdapterVideo;
import deafop.srhr.video.asyncTask.LoadLatest;
import deafop.srhr.video.interfaces.InterAdListener;
import deafop.srhr.video.interfaces.LatestListener;
import deafop.srhr.video.interfaces.RecyclerViewClickListener;



public class LatestActivity extends AppCompatActivity {

    Methods methods;
    RecyclerView recyclerView;
    AdapterVideo adapter;
    ArrayList<itemVideo> arrayList, arrayListTemp;
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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.latest));
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getData();
                        }
                    }, 0);
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

                Intent intent = new Intent(LatestActivity.this, All_PlayerActivity.class);
                intent.putExtra("pos", real_pos);
                Setting.arrayList.clear();
                Setting.arrayList.addAll(arrayListTemp);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    private void getData() {
        load = new LoadLatest(new LatestListener() {
            @Override
            public void onStart() {
                if(arrayList.size() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<itemVideo> arrayListVideo) {
                if (success.equals("1")) {
                    if (!verifyStatus.equals("-1")) {
                        if (arrayListVideo.size() == 0) {
                            isOver = true;
                            try {
                                adapter.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            setEmpty();
                        } else {
                            arrayListTemp.addAll(arrayListVideo);
                            for (int i = 0; i < arrayListVideo.size(); i++) {
                                arrayList.add(arrayListVideo.get(i));

                                if(Setting.isAdmobNativeAd || Setting.isFBNativeAd) {
                                    int abc = arrayList.lastIndexOf(null);
                                    if (((arrayList.size() - (abc + 1)) % nativeAdPos == 0) && (arrayListVideo.size()-1 != i)) {
                                        arrayList.add(null);
                                    }
                                }
                            }

                            page = page + 1;
                            setAdapter();
                        }
                    } else {
                        methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                    }
                } else {
                    setEmpty();
                }
                progressBar.setVisibility(View.GONE);

            }

        },methods.getAPIRequest(Setting.METHOD_LATEST, page, "", "", "", "", "", "", "", "","","","","","","","", null));
        load.execute();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new AdapterVideo(LatestActivity.this,  arrayList,  new RecyclerViewClickListener() {
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
