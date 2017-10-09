package ua.org.ahf.ahfdb.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.fragment.MapFragment;
import ua.org.ahf.ahfdb.fragment.PreferencesFragment;
import ua.org.ahf.ahfdb.fragment.CatalogFragment;
import ua.org.ahf.ahfdb.helper.DbHelper;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MapFragment mapFragment;
    private CatalogFragment catalogFragment;
    private PreferencesFragment preferencesFragment;
    private static String HOME_SCREEN = "home_screen";
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (preferences.getBoolean(getString(R.string.first_run), true)) {
            startActivityForResult(new Intent(this, UpdateActivity.class), 1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapFragment = new MapFragment();
        catalogFragment = new CatalogFragment();
        preferencesFragment = new PreferencesFragment();

        if (savedInstanceState == null) {
            // Set default fragment
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultScreen = sharedPreferences.getString(HOME_SCREEN, "2");
            switch (defaultScreen) {
                case "1" :
                    fragment = mapFragment;
                    break;
                case "2" :
                default:
                    fragment = catalogFragment;
                    break;
            }
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // app first run
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Boolean result = data.getBooleanExtra("update_success", false);
                if(result) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putBoolean(getString(R.string.first_run), false);
                    edit.apply();
                    // Reload fragment after database update
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragment).attach(fragment).commit();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle catalog view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_map) {
            fragmentTransaction.replace(R.id.container, mapFragment);
        } else if (id == R.id.nav_catalog) {
            fragmentTransaction.replace(R.id.container, catalogFragment);
        } else if (id == R.id.nav_settings) {
            fragmentTransaction.replace(R.id.container, preferencesFragment);
        }

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    DbHelper.setSortBy(DbHelper.DbSchema.CompanyTable.Column.NAME);
                    ((CatalogFragment)fragment).reloadData();
                }
                return true;
            case R.id.sort_by_oblast:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    DbHelper.setSortBy(DbHelper.DbSchema.CompanyTable.Column.OBLAST_ID);
                    ((CatalogFragment)fragment).reloadData();
                }
                return true;
            case R.id.sort_by_area:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    DbHelper.setSortBy(DbHelper.DbSchema.CompanyTable.Column.AREA);
                    ((CatalogFragment)fragment).reloadData();
                }
                return true;
            case R.id.sort_by_distance:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    // TODO: change after adding GPS support
                    DbHelper.setSortBy(DbHelper.DbSchema.CompanyTable.Column.NAME);
                    ((CatalogFragment)fragment).reloadData();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
