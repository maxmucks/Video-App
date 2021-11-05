package deafop.srhr.video.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import deafop.srhr.video.item.itemCategory;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.adapter.CategoryAdapter;
import deafop.srhr.video.asyncTask.LoadCat;
import deafop.srhr.video.interfaces.CatListener;



public class CatActivity extends AppCompatActivity {
    private Methods methods;
    private RecyclerView recyclerView_category;
    private CategoryAdapter adapterCat;
    private ArrayList<itemCategory> arrayList;
    private int page = 1;
    GridLayoutManager grid;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode ) {
            setTheme(R.style.AppTheme2);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);


        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.category));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.load_video);
        recyclerView_category = findViewById(R.id.tv);
        grid = new GridLayoutManager(this, 1);
        grid.setSpanCount(1);
        recyclerView_category.setLayoutManager(grid);
        loadCategories();


        LinearLayout adView = findViewById(R.id.adView);
        methods.showBannerAd(adView);
    }

    private void loadCategories() {
        if (methods.isNetworkAvailable()) {
            LoadCat loadCat = new LoadCat(new CatListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        arrayList.clear();
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<itemCategory> arrayListCat) {
                    if (this != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                if (arrayListCat.size() == 0) {
                                    Toast.makeText(CatActivity.this, "", Toast.LENGTH_SHORT).show();
                                } else {
                                    page = page + 1;
                                    arrayList.addAll(arrayListCat);
                                    setAdapter();
                                }
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                }
            }, methods.getAPIRequest(Setting.METHOD_CAT, 0, "", "", "", "", "", "", "", "","","","","","","","", null));
            loadCat.execute();
        } else {
            Toast.makeText(CatActivity.this, "", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        adapterCat = new CategoryAdapter(arrayList, CatActivity.this);
        recyclerView_category.setAdapter(adapterCat);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
