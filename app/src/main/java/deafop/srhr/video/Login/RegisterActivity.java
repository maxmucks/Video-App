package deafop.srhr.video.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.SharedPref.SharedPre;


public class RegisterActivity extends AppCompatActivity {

    Methods methods;
    EditText editText_name, editText_email, editText_pass, editText_cpass, editText_phone;
    TextView text_signin;
    Button register;
    ProgressDialog progressDialog;
    SharedPre sharedPre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPre = new SharedPre(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.registering));
        progressDialog.setCancelable(false);

        text_signin = findViewById(R.id.tv_regis_signin);
        register = findViewById(R.id.register);
        editText_name = findViewById(R.id.et_regis_name);
        editText_email = findViewById(R.id.et_regis_email);
        editText_pass = findViewById(R.id.et_regis_password);
        editText_cpass = findViewById(R.id.et_regis_cpassword);
        editText_phone = findViewById(R.id.et_regis_phone);


        TextView tv_welcome = findViewById(R.id.tv);

        tv_welcome.setTypeface(tv_welcome.getTypeface(), Typeface.BOLD);
        text_signin.setTypeface(text_signin.getTypeface(), Typeface.BOLD);
        register.setTypeface(register.getTypeface(), Typeface.BOLD);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    loadRegister();
                }
            }
        });

        text_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private Boolean validate() {
        if (editText_name.getText().toString().trim().isEmpty()) {
            editText_name.setError(getResources().getString(R.string.enter_name));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            editText_email.setError(getResources().getString(R.string.enter_email));
            editText_email.requestFocus();
            return false;
        } else if (!isEmailValid(editText_email.getText().toString())) {
            editText_email.setError(getString(R.string.error_invalid_email));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().isEmpty()) {
            editText_pass.setError(getResources().getString(R.string.enter_password));
            editText_pass.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().endsWith(" ")) {
            editText_pass.setError(getResources().getString(R.string.pass_end_space));
            editText_pass.requestFocus();
            return false;
        } else if (editText_cpass.getText().toString().isEmpty()) {
            editText_cpass.setError(getResources().getString(R.string.enter_cpassword));
            editText_cpass.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().equals(editText_cpass.getText().toString())) {
            editText_cpass.setError(getResources().getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        } else if (editText_phone.getText().toString().trim().isEmpty()) {
            editText_phone.setError(getResources().getString(R.string.enter_phone));
            editText_phone.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private void loadRegister() {
        if (methods.isNetworkAvailable()) {
            LoadRegister loadRegister = new LoadRegister(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        switch (registerSuccess) {
                            case "1":
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("from", "");
                                startActivity(intent);
                                finish();
                                break;
                            case "-1":
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                break;
                            default:
                                if (message.contains("already") || message.contains("Invalid email format")) {
                                    editText_email.setError(message);
                                    editText_email.requestFocus();
                                } else {
                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Setting.METHOD_REGISTER, 0, "", "", "", "", "", "", "", "", "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(),"","", null));
            loadRegister.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }
}