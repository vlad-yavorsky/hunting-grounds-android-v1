package ua.org.ahf.ahfdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper {

    private static final String DB_NAME = "ahf.db";
    private static final int DB_VERSION = 4;
    private static DbHelper mInstance;
    private SQLiteDatabase mSQLiteDatabase;

    public DbHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }

        mSQLiteDatabase = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase.execSQL(DbSchema.CompanyTable.CREATE_SQL);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                sqLiteDatabase.execSQL(DbSchema.CompanyTable.DELETE_SQL);
                onCreate(sqLiteDatabase);
            }
        }.getWritableDatabase();
    }

    public class DbSchema {
        public class CompanyTable {
            public static final String NAME = "company";
            public static final String CREATE_SQL = "create table " + NAME +
                    "(" +
                    Column.ID + " integer primary key, " +
                    Column.IS_MEMBER + " integer, " +
                    Column.IS_HUNTING_GROUND + " integer, " +
                    Column.IS_FISHING_GROUND + " integer, " +
                    Column.IS_POND_FARM + " integer, " +
                    Column.AREA + " real, " +
                    Column.LAT + " real, " +
                    Column.LNG + " real, " +
                    Column.NAME + " text, " +
                    Column.DESCRIPTION + " text" +
                    ")";
            public static final String DELETE_SQL = "drop table if exists " + NAME;

            public class Column {
                public static final String ID = "_id";
                public static final String IS_MEMBER = "is_member";
                public static final String IS_HUNTING_GROUND = "is_hunting_ground";
                public static final String IS_FISHING_GROUND = "is_fishing_ground";
                public static final String IS_POND_FARM = "is_pond_farm";
                public static final String AREA = "area";
                public static final String LAT = "lat";
                public static final String LNG = "lng";
                public static final String NAME = "name";
                public static final String DESCRIPTION = "description";

            }
        }
    }

    private ContentValues getContentValues(Company company) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbSchema.CompanyTable.Column.ID, company.getId());
        contentValues.put(DbSchema.CompanyTable.Column.IS_MEMBER, company.isMember());
        contentValues.put(DbSchema.CompanyTable.Column.IS_HUNTING_GROUND, company.isHuntingGround());
        contentValues.put(DbSchema.CompanyTable.Column.IS_FISHING_GROUND, company.isFishingGround());
        contentValues.put(DbSchema.CompanyTable.Column.IS_POND_FARM, company.isPondFarm());
        contentValues.put(DbSchema.CompanyTable.Column.AREA, company.getArea());
        contentValues.put(DbSchema.CompanyTable.Column.LAT, company.getLat());
        contentValues.put(DbSchema.CompanyTable.Column.LNG, company.getLng());
        contentValues.put(DbSchema.CompanyTable.Column.NAME, company.getName());
        contentValues.put(DbSchema.CompanyTable.Column.DESCRIPTION, company.getDescription());
        return contentValues;
    }

    public static DbHelper instance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public Cursor fetchAll() {
        Cursor cursor = null;
        String orderBy = DbSchema.CompanyTable.Column.IS_MEMBER + " DESC, " + DbSchema.CompanyTable.Column.NAME  + " ASC";
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, null, null, null, null, orderBy);
        return cursor;
    }

    public Cursor fetchById(String id) {
        Cursor cursor = null;
        String selection = DbSchema.CompanyTable.Column.ID + " = ?";
        String[] selectionArgs = {id};
        String orderBy = DbSchema.CompanyTable.Column.ID;
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, selection, selectionArgs, null, null, orderBy);
        return cursor;
    }

    public Cursor fetchByType(int companyType) {
        Cursor cursor = null;
        String selection = DbHelper.DbSchema.CompanyTable.Column.IS_HUNTING_GROUND + " = ?";
        if(companyType == 2) {
            selection = DbHelper.DbSchema.CompanyTable.Column.IS_FISHING_GROUND + " = ?";
        } else if(companyType == 3) {
            selection = DbHelper.DbSchema.CompanyTable.Column.IS_POND_FARM + " = ?";
        }
        String[] selectionArgs = {"1"};
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, selection, selectionArgs, null, null, null);
        return cursor;
    }

    public Company create(Company company) {
        Company ret = null;
//        if (company.getWhenCreated() == null) {
//            company.setWhenCreated(new Date());
//        }
        ContentValues contentValues = getContentValues(company);
        long rowId = mSQLiteDatabase.insert(DbSchema.CompanyTable.NAME, null, contentValues);
        if (rowId != -1) {
            company.setId(rowId);
            ret = company;
        }
        return ret;
    }

    public int deleteAllCompanies() {
        return mSQLiteDatabase.delete(DbSchema.CompanyTable.NAME, "1", null);
    }

}
