package com.upload.adeogo.dokita.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.ListAdapter;
import com.upload.adeogo.dokita.models.ChatHead;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class QuestionListActivity extends AppCompatActivity implements ListAdapter.ListAdapterOnclickHandler {

    public static final String ANONYMOUS = "anonymous";
    private String mDoctorName, mUsername, userId;
    private ListAdapter mListAdapter;
    private LinearLayoutManager mManager;
    private RecyclerView mRecyclerView;

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference mPictureDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ProgressBar mProgressBar;
    private List<ChatHead> mChatList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_question_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mProgressBar = findViewById(R.id.progressBar);

        mListAdapter = new ListAdapter(QuestionListActivity.this, this);
        mManager = new LinearLayoutManager(this);

        mListAdapter.swapData(mChatList);


        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setLayoutManager(mManager);

        //firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userId = user.getUid();
                    mChatDatabaseReference = mFirebaseDatabase.getReference().child("users").child(userId).child("questions").child("chat_head");
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivity(new Intent(QuestionListActivity.this, LoginActivity.class));
                }
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatHead chatHead = dataSnapshot.getValue(ChatHead.class);

                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (chatHead!= null ){
                        boolean check = true;
                        for(int i = 0; i < mChatList.size(); i++){
                            if (TextUtils.equals(chatHead.getUserId(), mChatList.get(i).getUserId() )){
                                check = false;
                            }
                        }
                        if (check){
                            mChatList.add(chatHead);
                        }
                    }

                    mListAdapter.swapData(mChatList);
                    mListAdapter.notifyDataSetChanged();
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mChatDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mChatDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mChatList.clear();
    }

    @Override
    public void voidMethod(List<ChatHead> list, int adapterPosition) {
        Intent intent = new Intent(this, QuestionActivity.class);
        ChatHead chatHead = list.get(adapterPosition);
        intent.putExtra("doctor_id", chatHead.getUserId());
        intent.putExtra("doctor_name", chatHead.getUserName());
        intent.putExtra("pictureUrl", chatHead.getPictureUrl());
        startActivity(intent);
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
    }

}
