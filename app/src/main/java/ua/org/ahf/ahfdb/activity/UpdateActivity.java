package ua.org.ahf.ahfdb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.helper.AsyncResponse;
import ua.org.ahf.ahfdb.helper.DbHelper;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    AsyncResponse listener = new AsyncResponse() {
        @Override
        public void processFinish(Boolean result) {
            if (result) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
//                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.b_retry).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.tv_message)).setText(getString(R.string.download_error));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        findViewById(R.id.b_retry).setOnClickListener(this);
        callDatabaseDownload();

//        ((ProgressBar)findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("update_success", false);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_retry:
                callDatabaseDownload();
                break;
        }
    }

    private void callDatabaseDownload() {
        findViewById(R.id.b_retry).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.tv_message)).setText(getString(R.string.downloading));
        DbHelper.instance(this).downloadData(listener);
    }
}
