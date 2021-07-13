package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CallbackManager mCallbackManager;
    private TextView textViewUser;
    private ImageView imageView;
    private LoginButton login_button;
    private static final String TAG = "FacebookAuthentication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());

        textViewUser = (TextView) findViewById(R.id.textViewUser);
        imageView = (ImageView)findViewById(R.id.imageView);
        login_button =(LoginButton) findViewById(R.id.login_button);
        mCallbackManager = CallbackManager.Factory.create();
        login_button.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG , "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());

            }



            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG , "handlefacebooktoken" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Sign in successful");
                    mUser = mAuth.getCurrentUser();
                    updateUI(mUser);
                }else{
                    Log.d(TAG, "Sign in failed");
                    Toast.makeText(MainActivity.this, "Failed to sign-in!", Toast.LENGTH_LONG).show();


                }
            }
        });
    }

    private void updateUI(FirebaseUser user){
        if (user!= null){
            textViewUser.setText(user.getDisplayName());
            if(user.getPhotoUrl() != null){
                String photoUrl = user.getPhotoUrl().toString();
                photoUrl= photoUrl + "?type=large";
                Picasso.get().load(photoUrl).into(imageView);

            }
        }else{
            textViewUser.setText("");
            imageView.setImageResource(R.drawable.com_facebook_auth_dialog_background);

        }

    }
}