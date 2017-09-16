package ua.org.ahf.ahfdb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        TextView tv_description = (TextView) findViewById(R.id.tv_description);

        SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();


        // Define 'where' part of query.
        String selection = DBHelper.COLUMN_ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id };

        Cursor cursor = db.query(
                DBHelper.TABLE_COMPANY, // The table to query
                null,                   // The columns to return
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null                    // The sort order
        );

        if (cursor.moveToFirst()) {
//        int id = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int isMember = cursor.getColumnIndex(DBHelper.COLUMN_IS_MEMBER);
            int isHuntingGround = cursor.getColumnIndex(DBHelper.COLUMN_IS_HUNTING_GROUND);
            int isFishingGround = cursor.getColumnIndex(DBHelper.COLUMN_IS_FISHING_GROUND);
            int isPondFarm = cursor.getColumnIndex(DBHelper.COLUMN_IS_POND_FARM);
            int lat = cursor.getColumnIndex(DBHelper.COLUMN_LAT);
            int lng = cursor.getColumnIndex(DBHelper.COLUMN_LNG);
            int name = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int description = cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION);

            tv_name.setText(cursor.getString(name));
            tv_description.setText(cursor.getString(description));
        }

    }
}
