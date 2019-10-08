package com.murat.todolist.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.murat.todolist.R;
import com.murat.todolist.database.LoginDatabase;
import com.murat.todolist.model.LoginUser;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etEmail;
    TextInputEditText etUsername;
    TextInputEditText etRe_password;
    TextInputEditText etPassword;
    Button btnRegister;
    ConstraintLayout cnsMain;
    LoginDatabase loginDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginDatabase = LoginDatabase.getInstance(this);
        etEmail = findViewById(R.id.etMailSignup);
        etUsername = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPasswordSignup);
        etRe_password = findViewById(R.id.etRePassword);
        cnsMain = findViewById(R.id.cnsMain);
        btnRegister = findViewById(R.id.btnSignup);
        btnRegister.setOnClickListener(v -> {
            emailControl(etEmail.getText().toString());
        });
    }

    private void register() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String re_pass = etRe_password.getText().toString();
        if (username.isEmpty()) {
            etUsername.setError("this field cannot be blank");
            etUsername.requestFocus();
        }
        else if (email.isEmpty()) {
            etUsername.setError("this field cannot be blank");
            etUsername.requestFocus();
        }
       else if (!re_pass.equals(pass)) {
            Snackbar snackbar = Snackbar.make(cnsMain, "Passwords do not match", Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", v -> {
                snackbar.dismiss();
            });
            snackbar.show();
        }
        else if (pass.length() < 6) {
            Snackbar snackbar = Snackbar.make(cnsMain, "Passwords must be at least 6 characters", Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", v -> {
                snackbar.dismiss();
            });
            snackbar.show();
        }
        else if (!username.matches("") && !email.matches("") && pass.matches(re_pass)) {
            LoginUser login = new LoginUser(etEmail.getText().toString(), etPassword.getText().toString(), etUsername.getText().toString());
            insertRow(login);
        }


    }

    public void emailControl(String email) {
        if (loginDatabase.loginDAO().queryUser(email)!=null){
            Toast.makeText(RegisterActivity.this, "böle bir kullanıcı var", Toast.LENGTH_SHORT).show();

        }else{
            register();
        }

    }


    @SuppressLint("StaticFieldLeak")
    private void insertRow(LoginUser loginUser) {
        new AsyncTask<LoginUser, Void, Long>() {
            @Override
            protected Long doInBackground(LoginUser... params) {
                Log.d("ok", "" + params[0]);
                return loginDatabase.loginDAO().insertUser(params[0]);
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);
                Toast.makeText(RegisterActivity.this, "Succesfuly "+id, Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(RegisterActivity.this, LoginActiviy.class));


            }
        }.execute(loginUser);

    }

}
