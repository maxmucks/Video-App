package deafop.srhr.video.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;




public class AboutActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView company, email, website, contact;
    Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode) {
            setTheme(R.style.AppTheme2);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        company = (TextView) findViewById(R.id.company);
        email = (TextView) findViewById(R.id.email);
        website = (TextView) findViewById(R.id.website);
        contact = (TextView) findViewById(R.id.contact);

        company.setText(Setting.company);
        email.setText(Setting.email);
        website.setText(Setting.website);
        contact.setText(Setting.contact);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}