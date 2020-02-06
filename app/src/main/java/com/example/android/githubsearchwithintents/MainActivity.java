package com.example.android.githubsearchwithintents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.githubsearchwithintents.data.GitHubRepo;
import com.example.android.githubsearchwithintents.utils.GitHubUtils;
import com.example.android.githubsearchwithintents.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GitHubSearchAdapter.OnSearchResultClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mSearchResultsRV;
    private EditText mSearchBoxET;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mErrorMessageTV;
    private GitHubSearchAdapter mGitHubSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxET = findViewById(R.id.et_search_box);
        mSearchResultsRV = findViewById(R.id.rv_search_results);

        mSearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsRV.setHasFixedSize(true);

        mGitHubSearchAdapter = new GitHubSearchAdapter(this);
        mSearchResultsRV.setAdapter(mGitHubSearchAdapter);

        mLoadingIndicatorPB = findViewById(R.id.pb_loading_indicator);
        mErrorMessageTV = findViewById(R.id.tv_error_message);

        Button searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBoxET.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    doGitHubSearch(searchQuery);
                }
            }
        });
    }

    @Override
    public void onSearchResultClicked(GitHubRepo repo) {
        Intent intent = new Intent(this, RepoDetailActivity.class);
        intent.putExtra(RepoDetailActivity.EXTRA_GITHUB_REPO, repo);
        startActivity(intent);
    }

    private void doGitHubSearch(String searchQuery) {
        String url = GitHubUtils.buildGitHubSearchURL(searchQuery);
        Log.d(TAG, "querying url: " + url);
        new GitHubSearchTask().execute(url);
    }

    public class GitHubSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicatorPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHttpGet(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
            if (s != null) {
                mErrorMessageTV.setVisibility(View.INVISIBLE);
                mSearchResultsRV.setVisibility(View.VISIBLE);
                ArrayList<GitHubRepo> searchResultsList = GitHubUtils.parseGitHubSearchResults(s);
                mGitHubSearchAdapter.updateSearchResults(searchResultsList);
            } else {
                mErrorMessageTV.setVisibility(View.VISIBLE);
                mSearchResultsRV.setVisibility(View.INVISIBLE);
            }
        }
    }
}
