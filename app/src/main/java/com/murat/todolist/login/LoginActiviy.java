package com.murat.todolist.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.murat.todolist.R;
import com.murat.todolist.database.LoginDatabase;
import com.murat.todolist.databinding.ActivityLoginBinding;
import com.murat.todolist.model.LoginUser;
import com.murat.todolist.ui.MainActivity;
import com.murat.todolist.viewmodel.LoginViewModel;


public class LoginActiviy extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding loginBinding;
    LoginDatabase loginDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginDatabase = LoginDatabase.getInstance(this);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginBinding = DataBindingUtil.setContentView(LoginActiviy.this, R.layout.activity_login);
        loginBinding.setLifecycleOwner(this);
        loginBinding.setLoginViewModel(loginViewModel);
        loginViewModel.getUser().observe(this, loginUser -> {
            if (TextUtils.isEmpty(loginUser.getStrEmailAddress())) {
                loginBinding.etUsarname.setError("Enter an E-mail address");
                loginBinding.etUsarname.requestFocus();
            } else if (!loginUser.isEmailValid()) {
                loginBinding.etUsarname.setError("Enter a Valid Email Address");
                loginBinding.etUsarname.requestFocus();
            } else if (TextUtils.isEmpty(loginUser.getStrPassword())) {
                loginBinding.etPassword.setError("Enter a Password");
                loginBinding.etPassword.requestFocus();
            } else if (!loginUser.isPasswordLengthGreaterThan5()) {
                loginBinding.etPassword.setError("Enter at least 6 Digit password");
                loginBinding.etPassword.requestFocus();
            } else {
                loginBinding.btnLogin.setOnClickListener(v -> {
                    loadControlUser(loginBinding.etUsarname.getText().toString(), loginBinding.etPassword.getText().toString());
                });
            }

        });
        loginViewModel.register().observe(this, v -> {

            loginBinding.tvRegister.setOnClickListener(v1 -> {
                startActivity(new Intent(LoginActiviy.this, RegisterActivity.class));
            });

        });
    }

    boolean isOk = false;

    @SuppressLint("StaticFieldLeak")
    private void loadControlUser(String email, String password) {

        new AsyncTask<String, String, LoginUser>() {
            @Override
            protected LoginUser doInBackground(String... params) {

                if (loginDatabase.loginDAO().queryUser(email, password)) {
                    isOk = true;
                }
                return null;
            }

            @Override
            protected void onPostExecute(LoginUser todoList) {
                if (isOk) {
                    Toast.makeText(LoginActiviy.this, "Welcome To Do App ", Toast.LENGTH_SHORT).show();
                    userAdd();
                    startActivity(new Intent(LoginActiviy.this, MainActivity.class));

                } else {
                    Toast.makeText(LoginActiviy.this, "find not User ", Toast.LENGTH_SHORT).show();

                }
            }
        }.execute(email, password);


    }


    private static String PREFS_NAME = "myInfoS";
    public static String PREF_USERNAME = "username";
    public static String PREF_PASSWORD = "password";

    private void userAdd() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String username = loginBinding.etUsarname.getText().toString();
        String password = loginBinding.etPassword.getText().toString();
        editor.putString(PREF_USERNAME, username);
        editor.putString(PREF_PASSWORD, password);
        editor.apply();
    }

}