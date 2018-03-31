package com.upload.adeogo.dokita.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.upload.adeogo.dokita.R;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private TextView btnRegister, mForgotPsd, mRiderTextView, btnSignIn;
    private RelativeLayout mRelativeLayout;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private MaterialEditText edtEmail, edtPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        mForgotPsd = findViewById(R.id.txt_view_pwd);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setFocusableInTouchMode(false);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setFocusableInTouchMode(false);
        mRelativeLayout = findViewById(R.id.rootLayout);

        mRiderTextView = findViewById(R.id.txt_rider_app);
        mRiderTextView.setFocusableInTouchMode(false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("users");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        mForgotPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForgetPwd();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(mRelativeLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(mRelativeLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                final android.app.AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
                waitingDialog.show();

                mFirebaseAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AppointmentActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(mRelativeLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT)
                                .show();

                        //Activate button
                        btnSignIn.setEnabled(true);
                    }
                });
            }
        });

        mRiderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goPlay();
            }
        });
    }

    private void goPlay()
    {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.works.adeogo.doctor" );
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.works.adeogo.doctor")));
        }
    }


    private void showRegisterDialog() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
     }

    private void showDialogForgetPwd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("FORGOT PASSWORD");
        alertDialog.setMessage("Please enter your email address");

        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View forgot_pwd_layout = inflater.inflate(R.layout.layout_pwd, null);

        final MaterialEditText edtEmail = forgot_pwd_layout.findViewById(R.id.edtEmail);
        alertDialog.setView(forgot_pwd_layout);

        //setButton
        alertDialog.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final android.app.AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
                if (TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(mRelativeLayout, "Please enter email", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                waitingDialog.show();

                mFirebaseAuth.sendPasswordResetEmail(edtEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogInterface.dismiss();
                                waitingDialog.dismiss();

                                Snackbar.make(mRelativeLayout, "Reset password link has been sent", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogInterface.dismiss();
                        waitingDialog.dismiss();

                        Snackbar.make(mRelativeLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

}