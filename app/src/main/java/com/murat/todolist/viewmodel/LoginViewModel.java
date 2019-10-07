package com.murat.todolist.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.murat.todolist.model.LoginUser;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> EmailAdress = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<LoginUser> userMutableLiveData;
    private MutableLiveData<String> userMutableLiveData2;

    public MutableLiveData<LoginUser> getUser() {

        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }

        return userMutableLiveData;
    }
    public MutableLiveData<String> register(){
        if (userMutableLiveData2 == null) {
            userMutableLiveData2 = new MutableLiveData<>();
        }
        return userMutableLiveData2;
    }

    public void onClick(View view) {
        LoginUser loginUser = new LoginUser(EmailAdress.getValue(), password.getValue());
        userMutableLiveData.setValue(loginUser);
    }

    public void onClickListener(View view) {
        userMutableLiveData2.setValue("");
    }

}
