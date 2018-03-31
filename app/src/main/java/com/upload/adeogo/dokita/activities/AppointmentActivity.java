package com.upload.adeogo.dokita.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.NewsAdapter;
import com.upload.adeogo.dokita.models.Appointment;
import com.upload.adeogo.dokita.models.MedicalArticle;
import com.upload.adeogo.dokita.utils.JSONFormat;
import com.upload.adeogo.dokita.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class AppointmentActivity extends AppCompatActivity {

    private static final int LOADER_ID = 1;
    @BindView(R.id.search_image_button)
    ImageView mSearchImageButton;
    @BindView(R.id.mainLayout)
    NestedScrollView mMainLayout;
    @BindView(R.id.newsRecyclerView)
    RecyclerView mNewsRecyclerView;
    @BindView(R.id.searchView)
    EditText mSearchViewEditText;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.fab_l)
    OptionsFabLayout fabWithOptions;


    @BindView(R.id.carouselView)
    CarouselView carouselView;
    @BindView(R.id.poweredClick)
    TextView mPoweredTextView;

    int[] sampleImages = {R.drawable.test_advert_1, R.drawable.image2, R.drawable.image3};
    private LinearLayoutManager mNewsLayoutManager;
    private String mSearchQuery;
    private Parcelable mListState;
    private List<MedicalArticle> medicalArticles = null;
    private NewsAdapter newsAdapter;
    private ImageListener imageListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        ButterKnife.bind(this);

        imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
            }
        };

        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toasty.success(AppointmentActivity.this, "Clicked item: " + position, Toast.LENGTH_SHORT, false).show();

            }
        });

        //Set main fab clicklistener.
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabWithOptions.isOptionsMenuOpened()) {
                    fabWithOptions.closeOptionsMenu();
                }
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.action_profile:
                        Intent intent = new Intent(AppointmentActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_cart:
                        Snackbar.make(mMainLayout, "Feature Coming Soon!", Snackbar.LENGTH_LONG).show();
                        break;

                    case R.id.action_settings:
                        Snackbar.make(mMainLayout, "Feature Coming Soon!", Snackbar.LENGTH_LONG).show();
                        break;

                    case R.id.action_messages:
                        Intent chatIntent = new Intent(AppointmentActivity.this, QuestionListActivity.class);
                        startActivity(chatIntent);
                        break;
                    default:
                        break;
                }
            }
        });

        newsAdapter = new NewsAdapter(this);

        mNewsLayoutManager = new LinearLayoutManager(this);


        mNewsRecyclerView.setLayoutManager(mNewsLayoutManager);
        mNewsRecyclerView.setAdapter(newsAdapter);

        updateLayout();


        mPoweredTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mSearchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSearch();
            }
        });

        mSearchViewEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return goToSearch();
            }
        });
    }

    private boolean goToSearch() {
        mSearchQuery = mSearchViewEditText.getText().toString();
        if (TextUtils.isEmpty(mSearchQuery)) {
            Snackbar.make(mMainLayout, "Enter Search Data!", Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            Intent intent = new Intent(AppointmentActivity.this, SearchResultActivity.class);
            intent.putExtra("query", mSearchQuery);
            startActivity(intent);
            return true;
        }

    }

    public void updateLayout() {
        new LoadNewsTask().execute();
    }


    public class LoadNewsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            URL url = NetworkUtils.buildUrl();
            String reply = null;
            try {
                reply = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reply;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                medicalArticles = JSONFormat.getObjectArray(s);
                progressBar.setVisibility(View.GONE);
                newsAdapter.swapData(medicalArticles);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}