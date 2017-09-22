package ua.org.ahf.ahfdb;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get toolbar to set back button
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get id of company to get all information about it from database
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Cursor cursor = DbHelper.instance(this).fetchById(id);

        if (cursor.moveToFirst()) {
//            int id = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.ID);
//            int isMember = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_MEMBER);
//            int isHuntingGround = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_HUNTING_GROUND);
//            int isFishingGround = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_FISHING_GROUND);
//            int isPondFarm = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_POND_FARM);
//            int lat = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.LAT);
//            int lng = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.LNG);
            int name = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.NAME);
            int description = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.DESCRIPTION);

            ((TextView)findViewById(R.id.tv_name)).setText(cursor.getString(name));
            ((TextView)findViewById(R.id.tv_description)).setText(cursor.getString(description));
        }
    }

    // Set back button for toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
