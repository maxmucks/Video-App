package deafop.srhr.video.Activity.video;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import deafop.srhr.video.Activity.YoutubeActivity;
import deafop.srhr.video.DBHelper.DBHelper;
import deafop.srhr.video.EndlessRecycler.EndlessRecyclerViewScrollListener;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.adapter.AdapterVideo;
import deafop.srhr.video.asyncTask.LoadLatest;
import deafop.srhr.video.interfaces.InterAdListener;
import deafop.srhr.video.interfaces.LatestListener;
import deafop.srhr.video.interfaces.RecyclerViewClickListener;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.item.itemComment;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.adapter.Adapter_Comment;
import deafop.srhr.video.asyncTask.LoadComment;
import deafop.srhr.video.interfaces.CommentListener;



public class All_PlayerActivity extends AppCompatActivity {

    TextView titel, view;
    LikeButton button_fav;
    Methods methods;
    DBHelper dbHelper;
    int position;
    WebView webView;

    ImageView imageView_Send, image_video;
    EditText editText;

    View view_gradient;

    RecyclerView recyclerView;
    Adapter_Comment adapter;
    ArrayList<itemComment> arrayList;
    Boolean isOver = false, isScroll = false;
    int page = 1;
    GridLayoutManager grid;


    RecyclerView recyclerView_video;
    AdapterVideo adapter_video;
    ArrayList<itemVideo> arrayList_video, arrayListTemp_video;
    Boolean isOver_video = false, isScroll_video = false;
    GridLayoutManager grid_video;
    LoadLatest load_video;
    ProgressBar progressBar_video;
    private int nativeAdPos = 0;

    itemVideo item;

    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode) {
            setAppTheme(R.style.AppTheme4);
        } else {
            setAppTheme(R.style.AppTheme3);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_player);

        if(Setting.isAdmobNativeAd) {
            nativeAdPos = Setting.admobNativeShow;
        } else if(Setting.isFBNativeAd) {
            nativeAdPos = Setting.fbNativeShow;
        }

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        dbHelper = new DBHelper(this);

        position = getIntent().getIntExtra("pos", 0);
        item = (Setting.arrayList.get(position));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(Setting.arrayList.get(position).getVideo_title());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayout adView = findViewById(R.id.adView);
        methods.showBannerAd(adView);

        image_video = findViewById(R.id.image_video);
        webView = findViewById(R.id.webView_description);
        view_gradient = findViewById(R.id.view_v);
        image_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (item.getVideo_type()) {
                    case "fb":
                        intent = new Intent(All_PlayerActivity.this, FbActivity.class);
                        break;
                    case "local":
                        intent = new Intent(All_PlayerActivity.this, PlayerActivity.class);
                        break;
                    case "server_url":
                        intent = new Intent(All_PlayerActivity.this, PlayerActivity.class);
                        break;
                    case "youtube":
                        intent = new Intent(All_PlayerActivity.this, YoutubeActivity.class);
                        break;
                    case "dailymotion":
                        intent = new Intent(All_PlayerActivity.this, DailymotionActivity.class);
                        break;
                    case "vimeo":
                        intent = new Intent(All_PlayerActivity.this, VimeoActivity.class);
                        break;
                    default:
                        intent = new Intent(All_PlayerActivity.this, VimeoActivity.class);
                        break;
                }
                intent.putExtra("pos2", position);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        titel = (TextView) findViewById(R.id.titel);
        view = (TextView) findViewById(R.id.view);
        button_fav = findViewById(R.id.button_wall_fav);

        button_fav.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                dbHelper.addtoFavorite(Setting.arrayList.get(position));
                methods.showSnackBar(getString(R.string.added_to_fav));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                dbHelper.removeFav(Setting.arrayList.get(position).getId());
                methods.showSnackBar(getString(R.string.removed_from_fav));
            }
        });

        editText = findViewById(R.id.editText_comment);
        imageView_Send = findViewById(R.id.imageView_comment_send);
        imageView_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.length() == 0) {
                    showToast(getString(R.string.comment_require));
                } else {
                    if (Setting.isLogged) {
                        new MyTaskComment().execute();
                    }else{
                        showToast("Login User");
                    }
                }
            }
        });

        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.comment_recyclerview);
        recyclerView.setHasFixedSize(true);
        grid = new GridLayoutManager(All_PlayerActivity.this, 1);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if(!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getData2();
                        }
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });


        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position2, String type) {
                int real_pos = adapter_video.getRealPos(position2, arrayListTemp_video);
                position = real_pos;
                Setting.arrayList.clear();
                Setting.arrayList.addAll(arrayListTemp_video);

                Load();
            }
        });


        progressBar_video = findViewById(R.id.load_video);
        progressBar_video.setVisibility(View.GONE);
        recyclerView_video = findViewById(R.id.latest_video);

        arrayList_video = new ArrayList<>();
        arrayListTemp_video = new ArrayList<>();
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
                            isOver_video = true;
                            try {
                                adapter_video.hideHeader();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0);
                } else {
                    adapter_video.hideHeader();
                }
            }
        });
        getData();
        Load();
    }

    private void Load() {
        item = (Setting.arrayList.get(position));
        titel.setText(Setting.arrayList.get(position).getVideo_title());
        view.setText(methods.format(Double.parseDouble((String)Setting.arrayList.get(position).getTotal_views()))+"Views");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBlockNetworkImage (false);

        String text;
        if (Setting.Dark_Mode){
            text = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/myfonts/Poppins-Regular_0.ttf\")}body{background: #000000;color: #ededed;},* {font-family: MyFont; color:#ededed; font-size: 13px;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        }else{
            text = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/myfonts/Poppins-Regular_0.ttf\")}body{background: #FDFDFE;color: #6D6D6D;},* {font-family: MyFont; color:#6D6D6D; font-size: 13px;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        }
        webView.loadDataWithBaseURL("", text + "<div>" + Setting.arrayList.get(position).getVideo_description() + "</div>", "text/html", "utf-8", null);

        arrayList.clear();
        isOver = false;
        isScroll = false;
        page = 1;


        getData2();
        checkFav();

        switch (item.getVideo_type()) {
            case "fb":
                Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.fb).into(image_video);
                new LoadColor(view_gradient, titel).execute(item.getVideo_thumbnail());
                break;
            case "local":
                Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.local).into(image_video);
                new LoadColor(view_gradient, titel).execute(item.getVideo_thumbnail());
                break;
            case "server_url":
                Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.url_im).into(image_video);
                new LoadColor(view_gradient, titel).execute(item.getVideo_thumbnail());
                break;
            case "youtube":
                Picasso.get().load(Setting.YOUTUBE_IMAGE_FRONT + item.getVideo_id() + Setting.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.youtube).into(image_video);
                new LoadColor(view_gradient, titel).execute(Setting.YOUTUBE_IMAGE_FRONT + item.getVideo_id() + Setting.YOUTUBE_SMALL_IMAGE_BACK);
                break;
            case "dailymotion":
                Picasso.get().load(Setting.DAILYMOTION_IMAGE_PATH + item.getVideo_id()).placeholder(R.drawable.dailymotion).into(image_video);
                new LoadColor(view_gradient, titel).execute(Setting.DAILYMOTION_IMAGE_PATH + item.getVideo_id());
                break;
            case "vimeo":
                Picasso.get().load(item.getVideo_thumbnail()).placeholder(R.drawable.vimeo).into(image_video);
                new LoadColor(view_gradient, titel).execute(item.getVideo_thumbnail());
                break;
        }
    }

    public void checkFav() {
        button_fav.setLiked(dbHelper.isFav(Setting.arrayList.get(position).getId()));
    }

    private void setAppTheme(@StyleRes int style) {
        setTheme(style);
    }



    @SuppressLint("StaticFieldLeak")
    private class MyTaskComment extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            return JSONParser.okhttpPost(Setting.SERVER_URL, methods.getAPIRequest(Setting.METHOD_COMMENT, 0, "", Setting.arrayList.get(position).getId(), "", "", "", "", editText.getText().toString(), "","","","",Setting.itemUser.getName(),"","","", null));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            editText.setText("");
            showToast("Comment Successful");
            arrayList.clear();
            isOver = false;
            isScroll = false;
            page = 1;
            getData2();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(All_PlayerActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void getData2() {
        LoadComment load = new LoadComment(new CommentListener() {
            @Override
            public void onStart() {
                if (arrayList.size() == 0) {
                    arrayList.clear();
                }
            }

            @Override
            public void onEnd(String success, ArrayList<itemComment> arrayListWall) {
                if(arrayListWall.size() == 0) {
                    isOver = true;
                    try {
                        adapter.hideHeader();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    page = page + 1;
                    arrayList.addAll(arrayListWall);
                    setAdapter();
                }
            }
        },methods.getAPIRequest(Setting.METHOD_COMMENT_ID, page, "", Setting.arrayList.get(position).getId(), "", "", "", "", "", "","","","","","","","", null));
        load.execute();
    }

    public void setAdapter() {
        if(!isScroll) {
            adapter = new Adapter_Comment(All_PlayerActivity.this, arrayList , new Adapter_Comment.RecyclerItemClickListener(){
                @Override
                public void onClickListener(itemComment listltem, int position) {
                    Toast.makeText(All_PlayerActivity.this, "", Toast.LENGTH_SHORT).show();
                }

            });
            recyclerView.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
        }
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

                            progressBar_video.setVisibility(View.INVISIBLE);
                            setAdapter_video();
                        }
                    } else {
                        methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                    }
                }
                progressBar_video.setVisibility(View.GONE);
            }

        },methods.getAPIRequest(Setting.METHOD_VIDEO_BY_CAT_NEW, 0, "", "", "", "", Setting.arrayList.get(position).getCat_id(), "", "", "","","","","","","","", null));
        load_video.execute();
    }

    private void setEmpty() {
        progressBar_video.setVisibility(View.INVISIBLE);
    }

    public void setAdapter_video() {
        if (!isScroll_video) {
            adapter_video = new AdapterVideo(All_PlayerActivity.this,  arrayList_video,  new RecyclerViewClickListener() {
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
}
