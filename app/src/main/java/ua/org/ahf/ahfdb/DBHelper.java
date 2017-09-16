package ua.org.ahf.ahfdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ahf.db";

    public static final String TABLE_COMPANY = "company";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IS_MEMBER = "is_member";
    public static final String COLUMN_IS_HUNTING_GROUND = "is_hunting_ground";
    public static final String COLUMN_IS_FISHING_GROUND = "is_fishing_ground";
    public static final String COLUMN_IS_POND_FARM = "is_pond_farm";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String SQL_CREATE_ENTRIES =
            "create table " + TABLE_COMPANY + " (" +
                    COLUMN_ID + " integer primary key, " +
                    COLUMN_IS_MEMBER + " integer, " +
                    COLUMN_IS_HUNTING_GROUND + " integer, " +
                    COLUMN_IS_FISHING_GROUND + " integer, " +
                    COLUMN_IS_POND_FARM + " integer, " +
                    COLUMN_LAT + " real, " +
                    COLUMN_LNG + " real, " +
                    COLUMN_NAME + " text, " +
                    COLUMN_DESCRIPTION + " text" +
            " )";

    private static final String SQL_DELETE_ENTRIES = "drop table if exists " + TABLE_COMPANY;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
