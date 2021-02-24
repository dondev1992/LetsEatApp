package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.letseat.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class SignUp extends AppCompatActivity {

    private MaterialEditText phoneEdt, nameEdt, passwordEdt;
    private FButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneEdt = findViewById(R.id.edtPhoneNum);
        nameEdt = findViewById(R.id.edtName);
        passwordEdt = findViewById(R.id.edtpWord);

        btnSignUp = findViewById(R.id.signUpBtn);

       //INIT FIREBASE
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("user");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Waiting Please...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //CHECKING TO SEE IF USER EXISTS
                        if (snapshot.child(phoneEdt.getText().toString()).exists()) {

                            //GET USER INFORMATION
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Phone Number already Exists!!", Toast.LENGTH_SHORT).show();

                        } else {
                            mDialog.dismiss();
                            User user = new User(nameEdt.getText().toString(), passwordEdt.getText().toString());
                            table_user.child(phoneEdt.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this, "Sign Up Successful!!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}