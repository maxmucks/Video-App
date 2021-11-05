package deafop.srhr.video.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;


public class intActivity extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode) {
            setTheme(R.style.AppTheme2);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(intActivity.this, SplashActivity.class);
                startActivity(main);
                finish();
            }
        });
    }


}
