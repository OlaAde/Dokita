package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.QuestionAdapter;
import com.upload.adeogo.dokita.models.ChatHead;
import com.upload.adeogo.dokita.models.Notification;
import com.upload.adeogo.dokita.models.Question;
import com.upload.adeogo.dokita.utils.Constants;
import com.upload.adeogo.dokita.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class QuestionActivity extends AppCompatActivity {

    private QuestionAdapter mAdapter;
    private LinearLayoutManager mManager;
//    private List<Question> mQuestionList ;
    private ListView mQuestionListView;

    private EditText mQuestionEditText;
    private TextView mNoInternetTextView;
    private TextView mNoQuestionTextView;
    private ImageView mLinkImageView, mSendImage;
    private LinearLayout mSendLinearLayout;

    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mSelfDoctorDatabaseReference, mPictureRef, mDoctorDatabaseReference, mSelfDoctorChatHeadDatabaseReference, mDoctorChatHeadDatabaseReference;

    private ChildEventListener mChildEventListener, mPhotoChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final int RC_SIGN_IN = 1, RC_PHOTO_PICKER = 2;
    private String mUsername, mPictureUrl, mSelfPictureUrl, userId,mDoctorName, mDoctorId;



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_question);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSendLinearLayout = findViewById(R.id.questionLinearLayout);
        mNoInternetTextView = findViewById(R.id.questionNoInternetTextView);
        mNoQuestionTextView = findViewById(R.id.questionNoQuestionsTextView);
        mQuestionListView = findViewById(R.id.questionListView);
        mLinkImageView = findViewById(R.id.linkImageView);

        mQuestionEditText = findViewById(R.id.questionMessageEditText);
        mSendImage = findViewById(R.id.questionSendButton);



        Intent intent = getIntent();
        mDoctorName = intent.getStringExtra("doctor_name");
        mDoctorId = intent.getStringExtra("doctor_id");
        mPictureUrl = intent.getStringExtra("pictureUrl");
        mSelfPictureUrl = intent.getStringExtra("selfPhotoUrl");
        List<Question> questions = new ArrayList<>();

        getSupportActionBar().setTitle(mDoctorName);
        mAdapter = new QuestionAdapter(this, R.layout.item_question, questions);
        mQuestionListView.setAdapter(mAdapter);

        mLinkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // PatientData is signed in
                    userId = user.getUid();
                    mUsername = user.getDisplayName();

                    mSelfDoctorDatabaseReference = mFirebaseDatabase.getReference().child("users").child(userId).child("questions").child("doctors").child(mDoctorId);
                    mSelfDoctorChatHeadDatabaseReference = mFirebaseDatabase.getReference().child("users").child(userId).child("questions").child("chat_head").child("doctors").child(mDoctorId);

                    mDoctorChatHeadDatabaseReference = mFirebaseDatabase.getReference().child("new_doctors").child(mDoctorId).child("questions").child("chat_head").child("clients").child(userId);

                    mDoctorDatabaseReference = mFirebaseDatabase.getReference().child("new_doctors").child(mDoctorId).child("questions").child("clients").child(userId);
                    mPictureRef = mFirebaseDatabase.getReference().child("users").child(userId);

                    mChatPhotosStorageReference = mFirebaseStorage.getReference().child("clients").child("doctors").child("chat_photos");
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // PatientData is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(QuestionActivity.this, LoginActivity.class));
                }

            }
        };

        int position = mQuestionListView.getFirstVisiblePosition();
        mQuestionListView.smoothScrollToPosition(position);

        mQuestionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged (CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendImage.setEnabled(true);
                } else {
                    mSendImage.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        boolean isConnect = NetworkUtils.isOnline(this);
        if (!isConnect){
            mNoInternetTextView.setVisibility(View.VISIBLE);
            noInternet();
        }

        // mAdapter.swapData(mQuestionList);

        // Send button sends a message and clears the EditText
        mSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mQuestionEditText.getText().toString())) {

                    Question question = new Question(mQuestionEditText.getText().toString().trim(), mUsername, 0, null);
                    mSelfDoctorDatabaseReference.push().setValue(question);


                    long unixTimeStamp = NetworkUtils.getUnixTime();
                    ChatHead selfChatHead = new ChatHead(mDoctorId, mDoctorName, mPictureUrl, unixTimeStamp, 0);
                    mSelfDoctorChatHeadDatabaseReference.setValue(selfChatHead);

                    ChatHead doctorChatHead = new ChatHead(userId, mUsername, mSelfPictureUrl, unixTimeStamp, 0);

                    mDoctorChatHeadDatabaseReference.setValue(doctorChatHead);
                    mDoctorDatabaseReference.push().setValue(question);



                    Notification notification = new Notification();
                    notification.setText(mQuestionEditText.getText().toString().trim());
                    notification.setTopic( mDoctorId);
                    notification.setUid(userId);
                    notification.setUsername(mUsername);
                    notification.setType("0");
                    notification.setWhich(Integer.toString(0));
                    if (mPictureUrl!= null){
                        notification.setImageUrl(mPictureUrl);
                    }else {
                        mPictureUrl = Constants.mPlaceHolder;
                        notification.setImageUrl(mPictureUrl);
                    }


                    FirebaseDatabase.getInstance().getReference("notifications").child(mDoctorId).push().setValue(notification);

                    FirebaseMessaging.getInstance().subscribeToTopic(userId);
                    // Clear input box
                    mQuestionEditText.setText("");

                } else {
                    Toast.makeText(QuestionActivity.this, "Enter a question", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Question question = dataSnapshot.getValue(Question.class);
                    if (TextUtils.equals(question.getName(), mUsername)){
                        question.setYou(0);
                        question.setName("You");
                    }else {
                        question.setYou(1);
                    }
                    mAdapter.add(question);
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mSelfDoctorDatabaseReference.addChildEventListener(mChildEventListener);
        }

        if (mPhotoChildEventListener == null) {
            mPhotoChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey().toString();
                    if (TextUtils.equals(key, "photoUrl")){
                        mSelfPictureUrl = dataSnapshot.getValue().toString();
                    }
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mPictureRef.addChildEventListener(mPhotoChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mSelfDoctorDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }

        if (mPhotoChildEventListener != null) {
            mPictureRef.removeEventListener(mPhotoChildEventListener);
            mPhotoChildEventListener = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            Question question = new Question(null, mUsername, 0, downloadUrl.toString());
                            mSelfDoctorDatabaseReference.push().setValue(question);


                            long unixTimeStamp = NetworkUtils.getUnixTime();
                            ChatHead selfChatHead = new ChatHead(mDoctorId, mDoctorName, mPictureUrl, unixTimeStamp, 0);
                            mSelfDoctorChatHeadDatabaseReference.setValue(selfChatHead);

                            ChatHead doctorChatHead = new ChatHead(userId, mUsername, mSelfPictureUrl, unixTimeStamp, 0);

                            mDoctorChatHeadDatabaseReference.setValue(doctorChatHead);
                            mDoctorDatabaseReference.push().setValue(question);


                            Notification notification = new Notification();
                            notification.setText(mQuestionEditText.getText().toString().trim());
                            notification.setTopic( mDoctorId);
                            notification.setUid(userId);
                            notification.setUsername(mUsername);
                            notification.setType("0");
                            notification.setWhich(Integer.toString(0));
                            if (mPictureUrl!= null){
                                notification.setImageUrl(mPictureUrl);
                            }else {
                                mPictureUrl = Constants.mPlaceHolder;
                                notification.setImageUrl(mPictureUrl);
                            }

                            FirebaseDatabase.getInstance().getReference("notifications").child(mDoctorId).push().setValue(notification);

                            FirebaseMessaging.getInstance().subscribeToTopic(userId);
//                            // Set the download URL to the message box, so that the user can send it to the database
//                            FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUrl.toString());
//                            mMessagesDatabaseReference.push().setValue(friendlyMessage);
                            System.out.println("On Success!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("OnFailureListener: " + e);
                            e.printStackTrace();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();

        mAdapter.clear();
    }

    private void noInternet(){
        mNoQuestionTextView.setVisibility(View.GONE);
        mNoInternetTextView.setVisibility(View.VISIBLE);
        mSendLinearLayout.setVisibility(View.GONE);
    }


}
