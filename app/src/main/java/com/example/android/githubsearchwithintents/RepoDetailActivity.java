package com.example.android.githubsearchwithintents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.githubsearchwithintents.data.GitHubRepo;

public class RepoDetailActivity extends AppCompatActivity {
    public static final String EXTRA_GITHUB_REPO = "GitHubRepo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_GITHUB_REPO)) {
            GitHubRepo repo = (GitHubRepo)intent.getSerializableExtra(EXTRA_GITHUB_REPO);

            TextView repoNameTV = findViewById(R.id.tv_repo_name);
            repoNameTV.setText(repo.full_name);

            TextView repoDescriptionTV = findViewById(R.id.tv_repo_description);
            repoDescriptionTV.setText(repo.description);

            TextView repoStarsTV = findViewById(R.id.tv_repo_stars);
            repoStarsTV.setText(Integer.toString(repo.stargazers_count));
        }
    }
}
