package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.Ultis.MySharePreferences;
import com.example.myapplication.loginModel.LoginRequest;
import com.example.myapplication.viewmodel.LoginFacebookViewModel;
import com.example.myapplication.viewmodel.LoginViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Fragment_Login extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    EditText edtEmail, edtPassword;
    Button btnLogin;
    LoginViewModel loginViewModel;
    LinearLayout layout;
    MySharePreferences mySharePreferences;
    private String mParam1;
    private String mParam2;
    private CallbackManager mCallbackManager;
    private LoginFacebookViewModel loginFacebookViewModel;
    private LoginButton btnLoginFB;
    String id, name, first_name, last_name, email;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;


    private static final String EMAIL = "email";

    public Fragment_Login() {
    }

    public static Fragment_Login newInstance(String param1, String param2) {
        Fragment_Login fragment = new Fragment_Login();
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
        View view = inflater.inflate(R.layout.fragment__login, container, false);
        init(view);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(view.getContext());
//        if (account != null) {
//            loginSuccess(view);
//        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                mySharePreferences.saveType("basic");
                LoginRequest loginRequest = new LoginRequest(email, password);
                loginCheck(view, loginRequest);
            }
        });

        btnLoginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySharePreferences.saveType("fb");
                loginFacebook(view);
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySharePreferences.saveType("google");
                signIn();
            }
        });
        return view;
    }

    private void init(View view) {
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        layout = view.findViewById(R.id.layoutContainerLogin);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginFacebookViewModel = new ViewModelProvider(this).get(LoginFacebookViewModel.class);
        mySharePreferences = new MySharePreferences(view.getContext());
        btnLoginFB = view.findViewById(R.id.btnLoginFB);
        mCallbackManager = CallbackManager.Factory.create();
        btnLoginFB.setPermissions(Arrays.asList("email", "public_profile"));
        btnLoginFB.setFragment(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        signInButton = view.findViewById(R.id.sign_in_button);

    }

    private void loginCheck(View view, LoginRequest loginRequest) {
        loginViewModel.setLoginRequest(loginRequest);
        loginViewModel.getInfoLogin().observe(this, loginInfo -> {
            if (loginInfo != null && loginInfo.isSuccess()) {
                loginSuccess(view);
            } else {
                Toast.makeText(view.getContext(), "Login Fail", Toast.LENGTH_SHORT).show();
                edtEmail.setText(null);
                edtPassword.setText(null);
            }
        });
    }

    private void loginFacebook(View view) {
        btnLoginFB.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mySharePreferences.saveToken(loginResult.getAccessToken().getToken());
                mySharePreferences.saveId(loginResult.getAccessToken().getUserId());
                loginSuccess(view);

            }

            @Override
            public void onCancel() {
                Toast.makeText(view.getContext(), "Login Fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(view.getContext(), "Login Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginSuccess(View view) {
        Toast.makeText(view.getContext(), "Login Complete", Toast.LENGTH_SHORT).show();
        AppCompatActivity activityCompat = (AppCompatActivity) view.getContext();
        activityCompat.getSupportFragmentManager().beginTransaction().replace(R.id.layoutLoginFragment, new Fragment_Info()).addToBackStack(null).commit();
        layout.setVisibility(View.GONE);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1234);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                mySharePreferences.saveId(account.getDisplayName());
                mySharePreferences.saveToken(account.getEmail());
                loginSuccess(getView());
            }

        } catch (ApiException e) {
            Toast.makeText(getContext(), "Fail", Toast.LENGTH_LONG).show();
            Log.w("login fail", "signInResult:failed code=" + e.getStatusCode());
        }
    }

}

