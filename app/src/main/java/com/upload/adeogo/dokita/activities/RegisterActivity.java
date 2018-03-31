package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackandphantom.circularprogressbar.CircleProgressbar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.models.PatientData;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private ScrollView mRegisterScrollView;
    private ActionBar mActionBar;
    private StorageReference mProfilePhotosStorageReference;
    private CircleProgressbar mProgressBar;
    private CircleImageView mImageView;
    private IntlPhoneInput mPhoneInput;

    private String mPhotoUrl = "", mPhoneNumber;
    private static final int RC_PHOTO_PICKER = 12;

    private MaterialEditText edtEmail, edtPassword, edtName;
    private android.app.AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mActionBar = getSupportActionBar();

        mActionBar.setTitle("Register");
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        waitingDialog = new SpotsDialog(RegisterActivity.this);

        mImageView = findViewById(R.id.registerProfileImageView);
        mProgressBar = findViewById(R.id.progressBarImageView);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        mPhoneInput = findViewById(R.id.phoneInput);

        mRegisterScrollView = findViewById(R.id.registerScrollView);

        mProfilePhotosStorageReference = FirebaseStorage.getInstance().getReference().child("clients").child("profile_photos");

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mProfilePhotosStorageReference.child(selectedImageUri.getLastPathSegment());


            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            float upload = taskSnapshot.getBytesTransferred();
                            float total = taskSnapshot.getTotalByteCount();
                            float currentProgress = upload * 100 / total;

                            mProgressBar.setProgress(currentProgress);
                        }
                    }).addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {

                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // When the image has successfully uploaded, we get its download URL
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mPhotoUrl = downloadUrl.toString();

                    Glide.with(RegisterActivity.this)
                            .load(mPhotoUrl)
                            .into(mImageView);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_register) {

            if (TextUtils.isEmpty(mPhotoUrl)) {
                Toast.makeText(RegisterActivity.this, "Please pick an image", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                Snackbar.make(mRegisterScrollView, "Please pick your profile image", Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }

            if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                Snackbar.make(mRegisterScrollView, "Please enter email address", Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }

            if (TextUtils.isEmpty(edtName.getText().toString())) {
                Snackbar.make(mRegisterScrollView, "Please enter phone number", Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }

            if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                Snackbar.make(mRegisterScrollView, "Please enter password", Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }

            if (edtPassword.getText().toString().length() < 6) {
                Snackbar.make(mRegisterScrollView, "Password too short !!!", Snackbar.LENGTH_SHORT)
                        .show();
                return false;
            }

            mPhoneNumber = mPhoneInput.getNumber();

            if (TextUtils.isEmpty(mPhoneNumber)) {
                Toast.makeText(RegisterActivity.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!mPhoneInput.isValid()) {
                Toast.makeText(RegisterActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return false;
            }

            requirePermission();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requirePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disclaimer");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView layout = (TextView) inflater.inflate(R.layout.disclaimer_content, null);
        layout.setText(getResources().getString(R.string.disclaimer_content));

        builder.setView(layout);

        final AlertDialog dialog = builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                waitingDialog.show();
                //Register new user
                mFirebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                PatientData patientData = new PatientData();
                                patientData.setEmail(edtEmail.getText().toString());
                                patientData.setPassword(edtPassword.getText().toString());
                                patientData.setName(edtName.getText().toString());
                                patientData.setPhone(mPhoneNumber);
                                patientData.setPhotoUrl(mPhotoUrl);

                                mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(patientData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                                if (user != null) {
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(edtName.getText().toString()).build();
                                                    user.updateProfile(profileUpdates);
                                                }
                                                Snackbar.make(mRegisterScrollView, "Registered successfully !!!", Snackbar.LENGTH_SHORT)
                                                        .show();
                                                waitingDialog.dismiss();
                                                finish();
                                                Intent intent = new Intent(RegisterActivity.this, FirstActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(mRegisterScrollView, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                                waitingDialog.dismiss();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mRegisterScrollView, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT)
                                .show();
                        waitingDialog.dismiss();
                    }
                });
            }
        }).setNeutralButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);

            }
        }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.orangey));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.orangey));
            }
        });

        dialog.show();
    }

}
