package com.example.letseat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letseat.Common.Common;
import com.example.letseat.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;

public class SignIn extends AppCompatActivity {
    private EditText phoneNumEdt, passwordEdt;
    private FButton signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        phoneNumEdt = findViewById(R.id.edtPhoneNumber);
        passwordEdt = findViewById(R.id.edtPassword);
        signInBtn = findViewById(R.id.logInBtn);

        //INIT FIREBASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("user");

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Waiting Please...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //CHECKING TO SEE IF USER EXISTS
                        if (snapshot.child(phoneNumEdt.getText().toString()).exists()) {
                            //GET USER INFORMATION
                            mDialog.dismiss();
                            User user = snapshot.child(phoneNumEdt.getText().toString()).getValue(User.class);
                            user.setPhone(phoneNumEdt.getText().toString());//SET PHONE NUMBER
                            if (user.getPassword().equals(passwordEdt.getText().toString())) {

                                Intent homeIntent = new Intent(SignIn.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "User Does Not Exist", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//                String phone = phoneNumEdt.getText().toString();
//                String pWord = passwordEdt.getText().toString();


            }
        });
    }
}