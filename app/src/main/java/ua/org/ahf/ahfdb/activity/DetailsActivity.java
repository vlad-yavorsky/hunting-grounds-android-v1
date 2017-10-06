package ua.org.ahf.ahfdb.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.helper.DbHelper;
import ua.org.ahf.ahfdb.helper.Utils;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get id of company to get all information about it from database
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Cursor cursor = DbHelper.instance(this).findById(id);

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
            int area = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.AREA);
            int website  = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.WEBSITE);
            int email  = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.EMAIL);
            int juridicalAddress  = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.JURIDICAL_ADDRESS);
            int actualAddress  = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.ACTUAL_ADDRESS);
            int director  = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.DIRECTOR);

            ((TextView)findViewById(R.id.tv_name)).setText(cursor.getString(name));
            setTitle(cursor.getString(name));

            if (cursor.getString(description).isEmpty()) {
                findViewById(R.id.tv_description).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.tv_description)).setText(Html.fromHtml(cursor.getString(description), null, new Utils.UlTagHandler()));
//            ((TextView)findViewById(R.id.tv_description)).setText(Html.fromHtml(cursor.getString(description), Html.FROM_HTML_MODE_COMPACT));
            }

            if (cursor.getString(area) == null) {
                findViewById(R.id.tv_area).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.tv_area)).setText(getResources().getString(R.string.area) + " " + cursor.getString(area) + " " + getResources().getString(R.string.kilo_ha));
            }

            if (cursor.getString(email).isEmpty()) {
                findViewById(R.id.tv_email).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.tv_email)).setText(getResources().getString(R.string.email) + " " + cursor.getString(email));
            }

            if (cursor.getString(website).isEmpty()) {
                findViewById(R.id.tv_website).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.tv_website)).setText(getResources().getString(R.string.website) + " " + cursor.getString(website));
            }

            if (cursor.getString(juridicalAddress).isEmpty()) {
                findViewById(R.id.tv_juridical_address).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.tv_juridical_address)).setText(getResources().getString(R.string.juridical_address) + " " + cursor.getString(juridicalAddress));
            }

            if (cursor.getString(actualAddress).isEmpty()) {
                findViewById(R.id.tv_actual_address).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.tv_actual_address)).setText(getResources().getString(R.string.actual_address) + " " + cursor.getString(actualAddress));
            }

            if (cursor.getString(director).isEmpty()) {
                findViewById(R.id.tv_director).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.tv_director)).setText(getResources().getString(R.string.director) + " " + cursor.getString(director));
            }
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
