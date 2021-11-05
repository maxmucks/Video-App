package deafop.srhr.video.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.deafop.library.Nemosofts;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;

import java.util.ArrayList;
import java.util.List;

import deafop.srhr.video.Constant.Constant;
import deafop.srhr.video.item.ItemHomeBanner;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.item.itemCategory;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.adapter.AdapterCatHome;
import deafop.srhr.video.adapter.HomePagerAdapter;
import deafop.srhr.video.adapter.Home_VideoAdapter;
import deafop.srhr.video.adapter.Home_Video_All_Adapter;
import deafop.srhr.video.asyncTask.LoadHome;
import deafop.srhr.video.interfaces.HomeListener;
import deafop.srhr.video.interfaces.RecyclerViewClickListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    MenuItem menu_login;

    Methods methods;
    Nemosofts nemosofts;

    TextView tv_home_lat, tv_home_mos, tv_home_all, tv_home_cat;

    private RecyclerView recyclerView;
    private Home_VideoAdapter adapter;
    private List<itemVideo> itemVideo_latest;

    private RecyclerView recyclerView2, recyclerView3, rv_cat;
    private Home_Video_All_Adapter adapter2, adapter3;
    private List<itemVideo> listltems2, listltems3;

    private AdapterCatHome adapterCategories;
    private ArrayList<itemCategory> arrayList_cat;

    ProgressBar progressBar;
    LinearLayout latest, category, all, most_view, About;
    LinearLayout view_viewPager_home, view_home;
    private EnchantedViewPager enchantedViewPager;
    private HomePagerAdapter homePagerAdapter;
    private ArrayList<ItemHomeBanner> arrayList_banner;

    View view_1, view_2, view_3;

    private Menu menu2;
    private static final String MERCHANT_ID=null;
    IInAppBillingService mService;
    private TextView text_view_go_pro;
    private BillingProcessor bp;
    private static final String LOG_TAG = "iabv3";

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode ) {
            setTheme(R.style.AppTheme2);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        nemosofts = new Nemosofts(this);


        initBuy();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        menu_login = menu.findItem(R.id.nav_login);

        changeLoginName();

        view_1 = findViewById(R.id.view_1);
        view_2 = findViewById(R.id.view_2);
        view_3 = findViewById(R.id.view_3);

        if (Constant.isRTL) {
            view_1.setVisibility(View.GONE);
            view_2.setVisibility(View.GONE);
            view_3.setVisibility(View.GONE);
        }

        view_home = findViewById(R.id.view_home);
        progressBar = findViewById(R.id.progress);

        itemVideo_latest = new ArrayList<>();
        listltems2 = new ArrayList<>();
        listltems3 = new ArrayList<>();
        arrayList_cat = new ArrayList<>();

        recyclerView = findViewById(R.id.latest_video);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));


        SnapHelper movie = new LinearSnapHelper();
        movie.attachToRecyclerView(recyclerView2);
        recyclerView2 = findViewById(R.id.all_video);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setHasFixedSize(true);



        SnapHelper movie2 = new LinearSnapHelper();
        movie2.attachToRecyclerView(recyclerView3);
        recyclerView3 = findViewById(R.id.most_video);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setHasFixedSize(true);


        rv_cat = findViewById(R.id.rv_home_cat);
        rv_cat.setHasFixedSize(true);
        LinearLayoutManager llm_cat = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rv_cat.setLayoutManager(llm_cat);

        view_viewPager_home = findViewById(R.id.view_viewPager_home);
        arrayList_banner = new ArrayList<>();
        enchantedViewPager = findViewById(R.id.viewPager_home);
        enchantedViewPager.useAlpha();
        enchantedViewPager.useScale();

        loadHome();


        About = (LinearLayout) findViewById(R.id.About);
        About .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fav = new Intent(MainActivity.this, Fav_VideoActivity.class);
                startActivity(fav);
            }
        });

        latest = (LinearLayout) findViewById(R.id.latest);
        latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent latest2 = new Intent(MainActivity.this, LatestActivity.class);
                startActivity(latest2);
            }
        });

        category = (LinearLayout) findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent Category = new Intent(MainActivity.this, CatActivity.class);
               startActivity(Category);
            }
        });

        all = (LinearLayout) findViewById(R.id.all);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent All = new Intent(MainActivity.this, All_VideoActivity.class);
                startActivity(All);
            }
        });
        most_view = (LinearLayout) findViewById(R.id.most_view);
        most_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent All = new Intent(MainActivity.this, Most_viewActivity.class);
                startActivity(All);
            }
        });

        LinearLayout adView = findViewById(R.id.adView);
        methods.showBannerAd(adView);

        LinearLayout adView2 = findViewById(R.id.adView2);
        methods.showBannerAd(adView2);

        tv_home_lat = findViewById(R.id.tv_home_lat);
        tv_home_mos = findViewById(R.id.tv_home_mos);
        tv_home_all = findViewById(R.id.tv_home_all);
        tv_home_cat = findViewById(R.id.tv_home_cat);

        tv_home_lat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent latest2 = new Intent(MainActivity.this, LatestActivity.class);
                startActivity(latest2);
            }
        });
        tv_home_mos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent All = new Intent(MainActivity.this, Most_viewActivity.class);
                startActivity(All);
            }
        });
        tv_home_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent All = new Intent(MainActivity.this, All_VideoActivity.class);
                startActivity(All);
            }
        });
        tv_home_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Category = new Intent(MainActivity.this, CatActivity.class);
                startActivity(Category);
            }
        });
    }

    private void loadHome() {
        if (methods.isNetworkAvailable()) {
            LoadHome loadHome = new LoadHome(new HomeListener() {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    view_home.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemHomeBanner> arrayListBanner, ArrayList<itemVideo> arrayList_home1, ArrayList<itemVideo> arrayList_home2, ArrayList<itemVideo> arrayList_home3, ArrayList<itemCategory> arrayList_cat2) {
                    if (success.equals("1")) {
                        itemVideo_latest.addAll(arrayList_home1);
                        listltems2.addAll(arrayList_home2);
                        listltems3.addAll(arrayList_home3);
                        arrayList_banner.addAll(arrayListBanner);

                        if (arrayList_banner.size() == 0) {
                            view_viewPager_home.setVisibility(View.GONE);
                        }else {
                            homePagerAdapter = new HomePagerAdapter(MainActivity.this, arrayList_banner);
                            enchantedViewPager.setAdapter(homePagerAdapter);
                            if (homePagerAdapter.getCount() > 2) {
                                enchantedViewPager.setCurrentItem(1);
                            }
                        }

                        arrayList_cat.addAll(arrayList_cat2);

                        adapter = new Home_VideoAdapter(itemVideo_latest, MainActivity.this);
                        recyclerView.setAdapter(adapter);

                        adapter2 = new Home_Video_All_Adapter(listltems2, MainActivity.this);
                        recyclerView2.setAdapter(adapter2);

                        adapter3 = new Home_Video_All_Adapter(listltems3, MainActivity.this);
                        recyclerView3.setAdapter(adapter3);

                        adapterCategories = new AdapterCatHome(MainActivity.this, arrayList_cat, new RecyclerViewClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                                intent.putExtra("name", arrayList_cat.get(position).getCategory_name());
                                intent.putExtra("cid", arrayList_cat.get(position).getCid());
                                intent.putExtra("image", arrayList_cat.get(position).getCategory_image());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        rv_cat.setAdapter(adapterCategories);

                        progressBar.setVisibility(View.GONE);
                        view_home.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        view_home.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Setting.METHOD_HOME, 0,"","","","","","","","","","","","","","","", null));
            loadHome.execute();
        } else {
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }




    private void changeLoginName() {
        if (menu_login != null) {
            if (Setting.isLoginOn) {
                if (Setting.isLogged) {
                    menu_login.setTitle(getResources().getString(R.string.logout));
                    menu_login.setIcon(getResources().getDrawable(R.drawable.ic_logout));
                } else {
                    menu_login.setTitle(getResources().getString(R.string.login));
                    menu_login.setIcon(getResources().getDrawable(R.drawable.ic_login));
                }
            } else {
                menu_login.setVisible(false);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_latest :
                Intent latest5 = new Intent(MainActivity.this, LatestActivity.class);
                startActivity(latest5);
                break;
            case R.id.nav_category :
                Intent category5 = new Intent(MainActivity.this, CatActivity.class);
                startActivity(category5);
                break;
            case R.id.nav_all :
                Intent all5 = new Intent(MainActivity.this, All_VideoActivity.class);
                startActivity(all5);
                break;
            case R.id.nav_most :
                Intent most = new Intent(MainActivity.this, Most_viewActivity.class);
                startActivity(most);
                break;
            case R.id.nav_fav :
                Intent fav  = new Intent(MainActivity.this, Fav_VideoActivity.class);
                startActivity(fav );
                break;
            case R.id.action_set :
                Intent About4 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(About4);
                finish();
                break;
            case R.id.nav_login:
                methods.clickLogin();
                break;
            default :
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu2 = menu;
        if (Setting.in_app){
            if (Setting.getPurchases){
                menu.clear();
            }
        }else {
            menu.clear();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_pro :
                showDialog_pay();
                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if(!BillingProcessor.isIabServiceAvailable(this)) {
        }

        bp = new BillingProcessor(this, Setting.MERCHANT_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                Intent intent= new Intent(MainActivity.this,SplashActivity.class);
                startActivity(intent);
                finish();
                updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
            }
            @Override
            public void onBillingInitialized() {
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }
    private void updateTextViews() {
        bp.loadOwnedPurchasesFromGoogle();
    }

    public Bundle getPurchases(){
        if (!bp.isInitialized()) {
            return null;
        }
        try{
            return  mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showDialog_pay(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.dialog_subscribe, null);

        final BottomSheetDialog dialog_setas = new BottomSheetDialog(this);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        this.text_view_go_pro=(TextView) dialog_setas.findViewById(R.id.text_view_go_pro);
        RelativeLayout relativeLayout_close_rate_gialog=(RelativeLayout) dialog_setas.findViewById(R.id.relativeLayout_close_rate_gialog);
        relativeLayout_close_rate_gialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_setas.dismiss();
            }
        });
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(MainActivity.this, Setting.SUBSCRIPTION_ID);
            }
        });
        dialog_setas.setOnKeyListener(new BottomSheetDialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog_setas.dismiss();
                }
                return true;
            }
        });
        dialog_setas.show();

    }


}
