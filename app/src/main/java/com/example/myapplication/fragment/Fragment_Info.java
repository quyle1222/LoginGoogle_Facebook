package com.example.myapplication.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.Ultis.MySharePreferences;
import com.example.myapplication.viewmodel.LoginFacebookViewModel;
import com.example.myapplication.viewmodel.UserViewModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


public class Fragment_Info extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView txtFullName;
    private TextView txtEmail;
    private ImageView imgAvatar;
    private UserViewModel userViewModel;
    private MySharePreferences mySharePreferences;
    private LoginFacebookViewModel loginFacebookViewModel;
    private Button btnLogOut;
    private RelativeLayout layout;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    public Fragment_Info() {

    }

    public static Fragment_Info newInstance(String param1, String param2) {
        Fragment_Info fragment = new Fragment_Info();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__info, container, false);
        initView(view);
        switch (mySharePreferences.getType()) {
            case "basic": {
                getDataUser();
                break;
            }
            case "fb": {
                updateResultFacebook();
                break;
            }
            case "google": {
                updateUI();
                break;
            }

        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                mySharePreferences.removeId();
                mySharePreferences.removeToken();
                AppCompatActivity activityCompat = (AppCompatActivity) view.getContext();
                activityCompat.getSupportFragmentManager().beginTransaction().replace(R.id.mainDetailsFragment, new Fragment_Login()).addToBackStack(null).commit();
                layout.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void initView(View view) {
        txtFullName = view.findViewById(R.id.txtFullName);
        txtEmail = view.findViewById(R.id.txtEmail);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mySharePreferences = new MySharePreferences(view.getContext());
        btnLogOut = view.findViewById(R.id.btnLogOut);
        layout = view.findViewById(R.id.layoutDetailsFragment);
        loginFacebookViewModel = new ViewModelProvider(this).get(LoginFacebookViewModel.class);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

    }

    private void getDataUser() {

        userViewModel.getUserInfoLiveData().observe(getViewLifecycleOwner(), userInfo -> {
            if (userInfo != null && userInfo.getSuccess()) {
                txtFullName.setText(userInfo.getData().getUserFullName());
                txtEmail.setText(userInfo.getData().getUserMail());
                imgAvatar.setImageURI(Uri.parse("https://test.kendo-suppli.jp" + userInfo.getData().getUserAvatarPath()));
            } else {
                txtFullName.setText("null");
            }
        });
    }

    private void updateUI() {
        txtFullName.setText(mySharePreferences.getToken());
        txtEmail.setText(mySharePreferences.getId());
    }


    private void updateResultFacebook() {
        txtFullName.setText(mySharePreferences.getToken());
        txtEmail.setText(mySharePreferences.getId());
    }

}