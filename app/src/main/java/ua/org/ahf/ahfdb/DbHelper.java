package ua.org.ahf.ahfdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper {

    private static final String DB_NAME = "ahf.db";
    private static final int DB_VERSION = 8;
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
                sqLiteDatabase.execSQL(DbSchema.OblastTable.CREATE_SQL);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                sqLiteDatabase.execSQL(DbSchema.CompanyTable.DELETE_SQL);
                sqLiteDatabase.execSQL(DbSchema.OblastTable.DELETE_SQL);
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
                    Column.DESCRIPTION + " text," +
                    Column.WEBSITE + " text," +
                    Column.EMAIL + " text," +
                    Column.JURIDICAL_ADDRESS + " text," +
                    Column.ACTUAL_ADDRESS + " text," +
                    Column.DIRECTOR + " text," +
                    Column.IS_ENABLED + " integer," +
                    Column.OBLAST_ID + " integer," +
                    Column.LOCALE + " text" +
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
                public static final String WEBSITE = "website";
                public static final String EMAIL = "email";
                public static final String JURIDICAL_ADDRESS = "juridical_address";
                public static final String ACTUAL_ADDRESS = "actual_address";
                public static final String DIRECTOR = "director";
                public static final String IS_ENABLED = "is_enabled";
                public static final String OBLAST_ID = "oblast_id";
                public static final String LOCALE = "locale";
            }
        }

        public class OblastTable {
            public static final String NAME = "oblast";
            public static final String CREATE_SQL = "create table " + NAME +
                    "(" +
                    Column.ID + " integer primary key, " +
                    Column.NAME + " text" +
                    ")";
            public static final String DELETE_SQL = "drop table if exists " + NAME;

            public class Column {
                public static final String ID = "_id";
                public static final String NAME = "name";
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
        contentValues.put(DbSchema.CompanyTable.Column.WEBSITE, company.getWebsite());
        contentValues.put(DbSchema.CompanyTable.Column.EMAIL, company.getEmail());
        contentValues.put(DbSchema.CompanyTable.Column.JURIDICAL_ADDRESS, company.getJuridicalAddress());
        contentValues.put(DbSchema.CompanyTable.Column.ACTUAL_ADDRESS, company.getActualAddress());
        contentValues.put(DbSchema.CompanyTable.Column.DIRECTOR, company.getDirector());
        contentValues.put(DbSchema.CompanyTable.Column.IS_ENABLED, company.isEnabled());
        contentValues.put(DbSchema.CompanyTable.Column.OBLAST_ID, company.getOblastId());
        contentValues.put(DbSchema.CompanyTable.Column.LOCALE, company.getLocale());
        return contentValues;
    }

    private ContentValues getContentValues(Oblast oblast) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbSchema.OblastTable.Column.ID, oblast.getId());
        contentValues.put(DbSchema.OblastTable.Column.NAME, oblast.getName());
        return contentValues;
    }

    public static DbHelper instance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    // Used for list
    public Cursor findAll(String locale) {
        Cursor cursor = null;
        String selection = DbSchema.CompanyTable.Column.LOCALE + " = ?";
        String[] selectionArgs = {locale};
        String orderBy = DbSchema.CompanyTable.Column.IS_MEMBER + " DESC, " + DbSchema.CompanyTable.Column.NAME  + " ASC";
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, selection, selectionArgs, null, null, orderBy);
        return cursor;
    }

    // Used for details page
    public Cursor findById(String id) {
        Cursor cursor = null;
        String selection = DbSchema.CompanyTable.Column.ID + " = ?";
        String[] selectionArgs = {id};
        String orderBy = DbSchema.CompanyTable.Column.ID;
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, selection, selectionArgs, null, null, orderBy);
        return cursor;
    }

    // Used for map
    public Cursor findByType(int companyType, String locale) {
        Cursor cursor = null;
        String selection = DbHelper.DbSchema.CompanyTable.Column.IS_HUNTING_GROUND + " = ?";
        if(companyType == 2) {
            selection = DbHelper.DbSchema.CompanyTable.Column.IS_FISHING_GROUND + " = ?";
        } else if(companyType == 3) {
            selection = DbHelper.DbSchema.CompanyTable.Column.IS_POND_FARM + " = ?";
        }
        selection = selection + " AND " + DbHelper.DbSchema.CompanyTable.Column.LOCALE + " = ?";
        String[] selectionArgs = {"1", locale};
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

    public Cursor findOblastById(String id) {
        Cursor cursor = null;
        String selection = DbSchema.OblastTable.Column.ID + " = ?";
        String[] selectionArgs = {id};
        cursor = mSQLiteDatabase.query(DbSchema.OblastTable.NAME, null, selection, selectionArgs, null, null, null);
        return cursor;
    }

    public Oblast create(Oblast oblast) {
        Oblast ret = null;
        ContentValues contentValues = getContentValues(oblast);
        long rowId = mSQLiteDatabase.insert(DbSchema.OblastTable.NAME, null, contentValues);
        if (rowId != -1) {
            oblast.setId(rowId);
            ret = oblast;
        }
        return ret;
    }

    public int deleteAllCompanies() {
        return mSQLiteDatabase.delete(DbSchema.CompanyTable.NAME, "1", null);
    }

    public void createOblasts() {
        create(new Oblast("vinnytsia_oblast"));
        create(new Oblast("volyn_oblast"));
        create(new Oblast("dnipropetrovsk_oblast"));
        create(new Oblast("donetsk_oblast"));
        create(new Oblast("zhitomyr_oblast"));
        create(new Oblast("zakarpattia_oblast"));
        create(new Oblast("zaporizhia_oblast"));
        create(new Oblast("ivano_frankivsk_oblast"));
        create(new Oblast("kyiv_oblast"));
        create(new Oblast("kirovohrad_oblast"));
        create(new Oblast("luhansk_oblast"));
        create(new Oblast("lviv_oblast"));
        create(new Oblast("mykolaiv_oblast"));
        create(new Oblast("odessa_oblast"));
        create(new Oblast("poltava_oblast"));
        create(new Oblast("rivne_oblast"));
        create(new Oblast("sumy_oblast"));
        create(new Oblast("ternopil_oblast"));
        create(new Oblast("kharkiv_oblast"));
        create(new Oblast("kherson_oblast"));
        create(new Oblast("khmelnytskyi_oblast"));
        create(new Oblast("cherkasy_oblast"));
        create(new Oblast("chernivtsi_oblast"));
        create(new Oblast("chernygiv_oblast"));
        create(new Oblast("krim"));
    }

}
