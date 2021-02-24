package com.example.letseat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    private FButton signUpBtn, signInBtn;
    private TextView sloganText;
    private SignInButton googleMainSigninBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInBtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signupBtn);
        googleMainSigninBtn = findViewById(R.id.google_signin_main);

        sloganText = findViewById(R.id.text_slogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        sloganText.setTypeface(typeface);

        googleMainSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleSignIntent = new Intent(MainActivity.this, SignInGoogle.class);
                startActivity(googleSignIntent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignIn.class));

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));

            }
        });




    }
}