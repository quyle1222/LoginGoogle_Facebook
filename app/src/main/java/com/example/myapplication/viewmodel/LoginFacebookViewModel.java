package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.FacebookUserModel.FacebookUserModel;
import com.example.myapplication.repository.Repository;
import com.facebook.AccessToken;

import org.jetbrains.annotations.NotNull;

public class LoginFacebookViewModel extends AndroidViewModel {
    private FacebookUserModel user;
    private AccessToken token;
    private LiveData<FacebookUserModel> userLiveData;
    Repository repository;


    public LoginFacebookViewModel(@NonNull @NotNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public FacebookUserModel getUser() {
        return user;
    }

    public void setUser(FacebookUserModel user) {
        this.user = user;
    }

    public AccessToken getToken() {
        return token;
    }

    public LiveData<FacebookUserModel> getUserLiveData() {
        this.userLiveData = repository.getResultFacebook();
        return userLiveData;
    }

    public void setUserLiveData(LiveData<FacebookUserModel> userLiveData) {
        this.userLiveData = userLiveData;
    }

    public void setToken(AccessToken token) {
        this.token = token;
    }
}
