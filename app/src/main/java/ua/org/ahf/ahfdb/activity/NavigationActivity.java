package ua.org.ahf.ahfdb.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
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

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MapFragment mapFragment;
    private CatalogFragment catalogFragment;
    private PreferencesFragment preferencesFragment;
    private static String HOME_SCREEN = "home_screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
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
            String defaultScreen = sharedPreferences.getString(HOME_SCREEN, "1");
            Fragment fragment = null;
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

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("firstrun", true)) {
            // do smth
            sharedPreferences.edit().putBoolean("firstrun", false).commit();
        }
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
        // Handle navigation view item clicks here.
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
}
