package com.example.myapplication;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Ultis.MySharePreferences;
import com.example.myapplication.fragment.Fragment_Info;
import com.example.myapplication.fragment.Fragment_Login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    MySharePreferences mySharePreferences;
    String id = "";
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        PackageInfo info;


        try {
            PackageInfo info1 = getPackageManager().getPackageInfo(
                    "com.facebook.samples.loginhowto",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info1.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        mySharePreferences = new MySharePreferences(this);
        try {
            token = mySharePreferences.getToken();
            id = mySharePreferences.getId();
        } catch (Exception e) {

        }
        if (token.isEmpty() && id.isEmpty()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new Fragment_Login()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new Fragment_Info()).commit();
        }
    }
}