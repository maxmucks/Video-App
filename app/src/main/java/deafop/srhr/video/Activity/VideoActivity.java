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



public class VideoActivity extends AppCompatActivity {

    Methods methods;
    RecyclerView recyclerView_video;
    AdapterVideo adapter_video;
    ArrayList<itemVideo> arrayList_video, arrayListTemp_video;
    Boolean isOver_video = false, isScroll_video = false;
    int page_video = 1;
    GridLayoutManager grid_video;
    LoadLatest load_video;
    FloatingActionButton fab;
    ProgressBar progressBar_video;
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
        setTitle(getIntent().getExtras().getString("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        arrayList_video = new ArrayList<>();
        arrayListTemp_video = new ArrayList<>();
        progressBar_video = findViewById(R.id.load_video);

        fab = findViewById(R.id.fab);
        recyclerView_video = findViewById(R.id.tv);
        recyclerView_video.setHasFixedSize(true);

        grid_video = new GridLayoutManager(this, 1);
        grid_video.setSpanCount(1);

        grid_video.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter_video.getItemViewType(position) >= 1000 || adapter_video.isHeader(position)) ? grid_video.getSpanCount() : 1;
            }
        });
        recyclerView_video.setLayoutManager(grid_video);

        recyclerView_video.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid_video) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver_video) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll_video = true;
                            getData();
                        }
                    }, 0);
                } else {
                    adapter_video.hideHeader();
                }
            }
        });

        recyclerView_video.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = grid_video.findFirstVisibleItemPosition();

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
                recyclerView_video.smoothScrollToPosition(0);
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
                int real_pos = adapter_video.getRealPos(position, arrayListTemp_video);

                Intent intent = new Intent(VideoActivity.this, All_PlayerActivity.class);
                intent.putExtra("pos", real_pos);
                Setting.arrayList.clear();
                Setting.arrayList.addAll(arrayListTemp_video);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    private void getData() {
        load_video = new LoadLatest(new LatestListener() {
            @Override
            public void onStart() {
                if(arrayList_video.size() == 0) {
                    progressBar_video.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<itemVideo> arrayListVideo) {
                if (success.equals("1")) {
                    if (!verifyStatus.equals("-1")) {
                        if (arrayListVideo.size() == 0) {
                            isOver_video = true;
                            try {
                                adapter_video.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            arrayListTemp_video.addAll(arrayListVideo);
                            for (int i = 0; i < arrayListVideo.size(); i++) {
                                arrayList_video.add(arrayListVideo.get(i));

                                if(Setting.isAdmobNativeAd || Setting.isFBNativeAd) {
                                    int abc = arrayList_video.lastIndexOf(null);
                                    if (((arrayList_video.size() - (abc + 1)) % nativeAdPos == 0) && (arrayListVideo.size()-1 != i)) {
                                        arrayList_video.add(null);
                                    }
                                }
                            }

                            page_video = page_video + 1;
                            progressBar_video.setVisibility(View.INVISIBLE);
                            setAdapter();
                        }
                    } else {
                        methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                    }
                }
                progressBar_video.setVisibility(View.GONE);
            }

        },methods.getAPIRequest(Setting.METHOD_VIDEO_BY_CAT, page_video, "", "", "", "", getIntent().getExtras().getString("cid"), "", "", "","","","","","","","", null));
        load_video.execute();
    }

    private void setEmpty() {
        progressBar_video.setVisibility(View.INVISIBLE);
    }

    public void setAdapter() {
        if (!isScroll_video) {
            adapter_video = new AdapterVideo(VideoActivity.this,  arrayList_video,  new RecyclerViewClickListener() {
                @Override
                public void onClick(int position) {
                    methods.showInter(position,"");
                }
            });

            recyclerView_video.setAdapter(adapter_video);
            setEmpty();
        } else {
            adapter_video.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        if(adapter_video != null) {
            adapter_video.destroyNativeAds();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
