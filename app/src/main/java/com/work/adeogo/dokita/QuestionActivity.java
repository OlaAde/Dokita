package com.work.adeogo.dokita;

import android.content.Intent;
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

import com.work.adeogo.dokita.adapters.QuestionAdapter;
import com.work.adeogo.dokita.models.Question;
import com.work.adeogo.dokita.utils.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

//    private RecyclerView mRecyclerView;
    private QuestionAdapter mAdapter;
    private LinearLayoutManager mManager;
//    private List<Question> mQuestionList ;
    private ListView mQuestionListView;

    private EditText mQuestionEditText;
    private Button mSendButton;
    private TextView mNoInternetTextView;
    private TextView mNoQuestionTextView;
    private ImageView mAddImageView;
    private LinearLayout mSendLinearLayout;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setTitle("Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSendLinearLayout = (LinearLayout) findViewById(R.id.questionLinearLayout);
        mNoInternetTextView = (TextView) findViewById(R.id.questionNoInternetTextView);
        mNoQuestionTextView = (TextView) findViewById(R.id.questionNoQuestionsTextView);
        mAddImageView = (ImageView) findViewById(R.id.add_question);
        mQuestionListView = (ListView) findViewById(R.id.questionListView);

        mQuestionEditText = (EditText) findViewById(R.id.questionMessageEditText);
        mSendButton = (Button) findViewById(R.id.questionSendButton);

        List<Question> questions = new ArrayList<>();

        mAdapter = new QuestionAdapter(this, R.layout.item_question, questions);
        mQuestionListView.setAdapter(mAdapter);

//        mManager = new LinearLayoutManager(this);
//        mAdapter = new QuestionAdapter(this);
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setLayoutManager(mManager);
//
//        mQuestionList = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userId = user.getUid();
                    mUsername = user.getDisplayName();
                    mDatabaseReference = mFirebaseDatabase.getReference().child("users/" +userId + "/questions");
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.GreenTheme)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddImageView.setVisibility(View.GONE);
                mSendLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        mQuestionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
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

//        mAdapter.swapData(mQuestionList);

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mQuestionEditText.getText().toString())){
                    Question question = new Question(mQuestionEditText.getText().toString().trim(), mUsername);
                    mDatabaseReference.push().setValue(question);
                    // Clear input box
                    mQuestionEditText.setText("");
                }else {
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
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    questionAvailable();
                    Question question = dataSnapshot.getValue(Question.class);
                    if (TextUtils.equals(question.getName(), mUsername)){
                        question.setName("Your Question");
                    }
                    mAdapter.add(question);

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
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
    }


    private void noInternet(){
        mNoQuestionTextView.setVisibility(View.GONE);
        mNoInternetTextView.setVisibility(View.VISIBLE);
        mSendLinearLayout.setVisibility(View.GONE);
    }

    private void questionAvailable(){
        mNoQuestionTextView.setVisibility(View.GONE);
        mNoInternetTextView.setVisibility(View.GONE);
        mSendLinearLayout.setVisibility(View.GONE);
        mAddImageView.setVisibility(View.VISIBLE);
    }


}
