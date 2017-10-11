package ua.org.ahf.ahfdb.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ua.org.ahf.ahfdb.model.Company;
import ua.org.ahf.ahfdb.model.Oblast;

public class DbHelper {

    private static final String DB_NAME = "ahf.db";
    private static final int DB_VERSION = 1;
    private static DbHelper mInstance;
    private SQLiteDatabase mSQLiteDatabase;
    private Context context;

    private static final String JSON_URL = "http://ahf.org.ua/get-data.php";
    private static final String TAG_RESULT = "result";

    private static String sortByColumnName = DbSchema.CompanyTable.Column.NAME;

    final String basicAuth = "Basic " + Base64.encodeToString("androiduser:qrgM,bXL4s_ZV!uB".getBytes(), Base64.NO_WRAP);

    private DbHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }

        this.context = context;
        mSQLiteDatabase = new SQLiteOpenHelper(this.context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase.execSQL(DbSchema.CompanyTable.CREATE_SQL);
                sqLiteDatabase.execSQL(DbSchema.OblastTable.CREATE_SQL);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//                sqLiteDatabase.execSQL(DbSchema.CompanyTable.DELETE_SQL);
//                sqLiteDatabase.execSQL(DbSchema.OblastTable.DELETE_SQL);
                onCreate(sqLiteDatabase);
            }
        }.getWritableDatabase();
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
        contentValues.put(DbSchema.CompanyTable.Column.NAME_LOWERCASE, company.getNameLowercase());
        contentValues.put(DbSchema.CompanyTable.Column.DESCRIPTION, company.getDescription());
        contentValues.put(DbSchema.CompanyTable.Column.WEBSITE, company.getWebsite());
        contentValues.put(DbSchema.CompanyTable.Column.EMAIL, company.getEmail());
        contentValues.put(DbSchema.CompanyTable.Column.JURIDICAL_ADDRESS, company.getJuridicalAddress());
        contentValues.put(DbSchema.CompanyTable.Column.ACTUAL_ADDRESS, company.getActualAddress());
        contentValues.put(DbSchema.CompanyTable.Column.DIRECTOR, company.getDirector());
        contentValues.put(DbSchema.CompanyTable.Column.IS_ENABLED, company.isEnabled());
        contentValues.put(DbSchema.CompanyTable.Column.OBLAST_ID, company.getOblastId());
        contentValues.put(DbSchema.CompanyTable.Column.LOCALE, company.getLocale());
        contentValues.put(DbSchema.CompanyTable.Column.PHONE_1, company.getPhone1());
        contentValues.put(DbSchema.CompanyTable.Column.PHONE_2, company.getPhone2());
        contentValues.put(DbSchema.CompanyTable.Column.PHONE_3, company.getPhone3());
//        contentValues.put(DbSchema.CompanyTable.Column.FAVORITE, company.isFavorite());
        contentValues.put(DbSchema.CompanyTable.Column.TERRITORY_COORDS, company.getTerritoryCoords());
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
    public Cursor findAll(String locale, String type) {
        Cursor cursor = null;
        String whereClause = DbSchema.CompanyTable.Column.LOCALE + " = ?";
        if(type == "favorites") {
            whereClause += " AND " + DbSchema.CompanyTable.Column.FAVORITE + " = 1";
        }
        String[] whereArgs = {locale};
        String orderBy = sortByColumnName  + " ASC";
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, whereClause, whereArgs, null, null, orderBy);
        return cursor;
    }

    // Used for details page
    public Cursor findById(String id) {
        Cursor cursor = null;
        String whereClause = DbSchema.CompanyTable.Column.ID + " = ?";
        String[] whereArgs = {id};
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, whereClause, whereArgs, null, null, null);
        return cursor;
    }

    // Used for search page
    public Cursor findByName(String name, String locale, String type) {
        Cursor cursor = null;
        String whereClause = DbSchema.CompanyTable.Column.NAME_LOWERCASE + " LIKE '%" + name.toLowerCase() + "%' AND " + DbSchema.CompanyTable.Column.LOCALE + " = ?";
        if(type == "favorites") {
            whereClause += " AND " + DbSchema.CompanyTable.Column.FAVORITE + " = 1";
        }
        String[] whereArgs = {locale};
        String orderBy = DbSchema.CompanyTable.Column.NAME_LOWERCASE;
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, whereClause, whereArgs, null, null, orderBy);
        return cursor;
    }

    // Used for map
    public Cursor findByType(int companyType, String locale) {
        Cursor cursor = null;
        String whereClause = DbSchema.CompanyTable.Column.IS_HUNTING_GROUND + " = ?";
        if(companyType == 2) {
            whereClause = DbSchema.CompanyTable.Column.IS_FISHING_GROUND + " = ?";
        } else if(companyType == 3) {
            whereClause = DbSchema.CompanyTable.Column.IS_POND_FARM + " = ?";
        }
        whereClause = whereClause + " AND " + DbSchema.CompanyTable.Column.LOCALE + " = ?";
        String[] whereArgs = {"1", locale};
        cursor = mSQLiteDatabase.query(DbSchema.CompanyTable.NAME, null, whereClause, whereArgs, null, null, null);
        return cursor;
    }

    public void create(Company company) {
        ContentValues contentValues = getContentValues(company);
        String whereClause = DbSchema.CompanyTable.Column.ID + " = ?";
        String[] whereArgs = {company.getId().toString()};

//        int id = (int) mSQLiteDatabase.insertWithOnConflict(DbSchema.CompanyTable.NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
//        if (id == -1) {
//            mSQLiteDatabase.update(DbSchema.CompanyTable.NAME, contentValues, whereClause, whereArgs);
//        }

        int id = mSQLiteDatabase.update(DbSchema.CompanyTable.NAME, contentValues, whereClause, whereArgs);
        if (id == 0) {
            mSQLiteDatabase.insertWithOnConflict(DbSchema.CompanyTable.NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public String findOblastById(String id) {
        Cursor cursor = null;
        String whereClause = DbSchema.OblastTable.Column.ID + " = ?";
        String[] whereArgs = {id};
        cursor = mSQLiteDatabase.query(DbSchema.OblastTable.NAME, null, whereClause, whereArgs, null, null, null);
        String sOblastName = "";

        if (cursor.moveToFirst()) {
            int iOblastName = cursor.getColumnIndex(DbSchema.OblastTable.Column.NAME);
            sOblastName =  context.getString(context.getResources().getIdentifier(cursor.getString(iOblastName), "string", context.getPackageName()));
        }
        cursor.close();

        return sOblastName;
    }

    private Oblast create(Oblast oblast) {
        Oblast ret = null;
        ContentValues contentValues = getContentValues(oblast);
        long rowId = mSQLiteDatabase.insert(DbSchema.OblastTable.NAME, null, contentValues);
        if (rowId != -1) {
            oblast.setId(rowId);
            ret = oblast;
        }
        return ret;
    }

    public void setFavorite(String id, String value) {
        String whereClause = DbSchema.CompanyTable.Column.ID + " = ?";
        String[] whereArgs = {id};
        ContentValues contentValues = new ContentValues();

        contentValues.put(DbSchema.CompanyTable.Column.FAVORITE, value);
        mSQLiteDatabase.update(DbSchema.CompanyTable.NAME, contentValues, whereClause, whereArgs);
    }

    private int deleteAllCompanies() {
        return mSQLiteDatabase.delete(DbSchema.CompanyTable.NAME, "1", null);
    }

    private void createOblasts() {
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

    class GetDataAsyncTask extends AsyncTask<String, Void, String> {
//        ProgressDialog loading;
        AsyncResponse listener = null;

        GetDataAsyncTask(AsyncResponse listener) {
            this.listener = listener;
        }

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            loading = ProgressDialog.show(UpdateFragment.this, "Please Wait...",null,true,true);
//        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty ("Authorization", basicAuth);
                con.setConnectTimeout(5000);

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                        sb.append("\n");
                    }
                    return sb.toString().trim();
                }
            } catch (java.net.SocketTimeoutException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                listener.processFinish(parseJSON(result));
            } else {
                listener.processFinish(false);
            }
//            loading.dismiss();
        }

        private boolean parseJSON(String json) {
//            deleteAllCompanies();

            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray companies = jsonObj.getJSONArray(TAG_RESULT);

                for(int i = 0; i < companies.length(); i++){
                    JSONObject c = companies.getJSONObject(i);

                    Long id = c.getLong(DbSchema.CompanyTable.Column.ID);
                    Integer isMember = c.getInt(DbSchema.CompanyTable.Column.IS_MEMBER);
                    Integer isHuntingGround = c.getInt(DbSchema.CompanyTable.Column.IS_HUNTING_GROUND);
                    Integer isFishingGround = c.getInt(DbSchema.CompanyTable.Column.IS_FISHING_GROUND);
                    Integer isPondFarm = c.getInt(DbSchema.CompanyTable.Column.IS_POND_FARM);
                    Double lat = null;
                    Double lng = null;
                    String name = c.getString(DbSchema.CompanyTable.Column.NAME);
                    String description = c.getString(DbSchema.CompanyTable.Column.DESCRIPTION);
                    Double area = null;
                    String website = c.getString(DbSchema.CompanyTable.Column.WEBSITE);
                    String email = c.getString(DbSchema.CompanyTable.Column.EMAIL);
                    String juridicalAddress = c.getString(DbSchema.CompanyTable.Column.JURIDICAL_ADDRESS);
                    String actualAddress = c.getString(DbSchema.CompanyTable.Column.ACTUAL_ADDRESS);
                    String director = c.getString(DbSchema.CompanyTable.Column.DIRECTOR);
                    Integer isEnabled = c.getInt(DbSchema.CompanyTable.Column.IS_ENABLED);
                    Integer oblastId = c.getInt(DbSchema.CompanyTable.Column.OBLAST_ID);
                    String locale = c.getString(DbSchema.CompanyTable.Column.LOCALE);
                    String phone1 = c.getString(DbSchema.CompanyTable.Column.PHONE_1);
                    String phone2 = c.getString(DbSchema.CompanyTable.Column.PHONE_2);
                    String phone3 = c.getString(DbSchema.CompanyTable.Column.PHONE_3);
                    String territoryCoords = null;

                    if(!c.isNull(DbSchema.CompanyTable.Column.AREA)) {
                        area = c.getDouble(DbSchema.CompanyTable.Column.AREA);
                    }
                    if(!c.isNull(DbSchema.CompanyTable.Column.LAT) && !c.isNull(DbSchema.CompanyTable.Column.LNG)) {
                        lat = c.getDouble(DbSchema.CompanyTable.Column.LAT);
                        lng = c.getDouble(DbSchema.CompanyTable.Column.LNG);
                    }
                    if(!c.isNull(DbSchema.CompanyTable.Column.TERRITORY_COORDS)) {
                        territoryCoords = c.getString(DbSchema.CompanyTable.Column.TERRITORY_COORDS);
                    }

                    Company company = new Company(context, id, isMember, isHuntingGround, isFishingGround,
                            isPondFarm, area, lat, lng, name, description, website, email, juridicalAddress,
                            actualAddress, director, isEnabled, oblastId, locale, phone1, phone2, phone3, 0,
                            territoryCoords);
                    create(company);
                }
                createOblasts();
            } catch (JSONException e) {
//            Toast.makeText(context, context.getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
                return false;
            }
//        Toast.makeText(context, context.getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    public void downloadData(AsyncResponse listener) {
        new GetDataAsyncTask(listener).execute();
    }

    public static void setSortBy(String columnName) {
        sortByColumnName = columnName;
    }

}
