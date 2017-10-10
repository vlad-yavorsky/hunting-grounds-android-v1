package ua.org.ahf.ahfdb.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.helper.DbHelper;
import ua.org.ahf.ahfdb.helper.Utils;
import ua.org.ahf.ahfdb.model.Company;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Company company = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getCompany();
        fillLayout();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.static_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                Intent intent = new Intent(DetailsActivity.this, MapsActivity.class);
//                startActivity(intent);
            }
        });
        mMap.addMarker(new MarkerOptions().position(company.getPosition())
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeBitmap(getBaseContext(), "hunting_target", 90, 90))));

        float zoomLevel = 10.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(company.getPosition(), zoomLevel));
    }

    void getCompany() {
        // Get id of company to get all information about it from database
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Cursor cursor = DbHelper.instance(this).findById(id);

        if (cursor.moveToFirst()) {
//            Integer id = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.ID));
            Integer isMember = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_MEMBER));
            Integer isHuntingGround = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_HUNTING_GROUND));
            Integer isFishingGround = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_FISHING_GROUND));
            Integer isPondFarm = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_POND_FARM));
            Double lat = cursor.getDouble(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.LAT));
            Double lng = cursor.getDouble(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.LNG));
            String name = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.NAME));
            String description = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.DESCRIPTION));
            Double area = cursor.getDouble(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.AREA));
            String website  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.WEBSITE));
            String email  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.EMAIL));
            String juridicalAddress  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.JURIDICAL_ADDRESS));
            String actualAddress  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.ACTUAL_ADDRESS));
            String director  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.DIRECTOR));
            Integer isEnabled = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_ENABLED));
            Integer oblastId = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.OBLAST_ID));
            String locale = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.LOCALE));
            String phone1  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.PHONE_1));
            String phone2  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.PHONE_2));
            String phone3  = cursor.getString(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.PHONE_3));
            Integer favorite  = cursor.getInt(cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.FAVORITE));

            company = new Company(Long.parseLong(id), isMember, isHuntingGround, isFishingGround,
                    isPondFarm, area, lat, lng, name, description, website, email, juridicalAddress,
                    actualAddress, director, isEnabled, oblastId, locale, phone1, phone2, phone3, favorite);
        }
    }

    void fillLayout() {
        boolean showContactsHeader = false;

        ((TextView)findViewById(R.id.tv_name)).setText(company.getName());
        setTitle(company.getName());

        if (company.getDescription().isEmpty()) {
            findViewById(R.id.tv_description).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_description)).setText(Utils.trimTrailingWhitespace(Html.fromHtml(company.getDescription(), null, new Utils.UlTagHandler())));
//                ((TextView)findViewById(R.id.tv_description)).setText(Html.fromHtml(company.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        }

        if (company.getArea() == null) {
            findViewById(R.id.ll_area).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_area)).setText(getString(R.string.area) + " " + company.getArea() + " " + getString(R.string.kilo_ha));
        }

        if (company.getEmail().isEmpty()) {
            findViewById(R.id.ll_email).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_email)).setText(getString(R.string.email) + " " + company.getEmail());
            showContactsHeader = true;
        }

        if (company.getWebsite().isEmpty()) {
            findViewById(R.id.ll_website).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_website)).setText(getString(R.string.website) + " " + company.getWebsite());
            showContactsHeader = true;
        }

        if (company.getPhone1().isEmpty()) {
            findViewById(R.id.ll_phone_1).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_phone_1)).setText(company.getPhone1());
            showContactsHeader = true;
        }

        if (company.getPhone2().isEmpty()) {
            findViewById(R.id.ll_phone_2).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_phone_2)).setText(company.getPhone2());
            showContactsHeader = true;
        }

        if (company.getPhone3().isEmpty()) {
            findViewById(R.id.ll_phone_3).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_phone_3)).setText(company.getPhone3());
            showContactsHeader = true;
        }

        if (company.getJuridicalAddress().isEmpty()) {
            findViewById(R.id.ll_juridical_address).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_juridical_address)).setText(getString(R.string.juridical_address) + " " + company.getJuridicalAddress());
            showContactsHeader = true;
        }

        if (company.getActualAddress().isEmpty()) {
            findViewById(R.id.ll_actual_address).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_actual_address)).setText(getString(R.string.actual_address) + " " + company.getActualAddress());
            showContactsHeader = true;
        }

        if (company.getDirector().isEmpty()) {
            findViewById(R.id.ll_director).setVisibility(View.GONE);
        } else {
            ((TextView)findViewById(R.id.tv_director)).setText(getString(R.string.director) + " " + company.getDirector());
            showContactsHeader = true;
        }

        if(!showContactsHeader) {
            findViewById(R.id.tv_contacts).setVisibility(View.GONE);
        }
    }

    private Menu mOptionsMenu;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.details, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        if (company.isFavorite() == 1) {
            item.setIcon(R.drawable.ic_favorite_black_24dp);
        } else {
            item.setIcon(R.drawable.ic_favorite_border_black_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Set back button for toolbar
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                Integer newValue = (company.isFavorite() + 1) % 2;
                company.setFavorite(newValue);
                DbHelper.instance(this).setFavorite(company.getId().toString(), newValue.toString());
                if (company.isFavorite() == 0) {
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                    item.setTitle(R.string.add_to_favorites);
                } else {
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                    item.setTitle(R.string.delete_from_favorites);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateOptionsMenu() {
        if (mOptionsMenu != null) {
            onPrepareOptionsMenu(mOptionsMenu);
        }
    }

}
