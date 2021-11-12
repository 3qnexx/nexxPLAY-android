package tv.nexx.android.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AudioDownloadActivity extends Activity {

    private static final String TAG = AudioDownloadActivity.class.getCanonicalName();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_download);
        progressBar = findViewById(R.id.downloadProgressBar);
    }

    public void onDownloadButtonClicked(View v) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "startDownloadLocalMedia onFail : NOT SUPPORTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "startDownloadLocalMedia onFail : NOT SUPPORTED");
        progressBar.setVisibility(View.GONE);
    }

    public void onPlayButtonClicked(View v) {
        Toast.makeText(this, "onPlayButtonClicked onFail : NOT SUPPORTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onPlayButtonClicked onFail : NOT SUPPORTED");
    }

    public void onDeleteCurrentButtonClicked(View view) {
        Toast.makeText(this, "onDeleteCurrentButtonClicked onFail : NOT SUPPORTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onDeleteCurrentButtonClicked onFail : NOT SUPPORTED");
        progressBar.setProgress(0);
        progressBar.setVisibility(View.INVISIBLE);

    }

    public void onDeleteAllButtonClicked(View view) {
        Toast.makeText(this, "onDeleteAllButtonClicked onFail : NOT SUPPORTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onDeleteAllButtonClicked onFail : NOT SUPPORTED");
        progressBar.setProgress(0);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
