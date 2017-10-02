package ua.org.ahf.ahfdb.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.helper.DbHelper;
import ua.org.ahf.ahfdb.model.Company;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // Declare a variable for the cluster manager.
    private ClusterManager<Company> mClusterManager;

    private Company clickedClusterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.setMyLocationEnabled(true);

        setUpClusterer();
        int companyType = getIntent().getIntExtra("COMPANY_TYPE", 1);
        String locale = getResources().getString(R.string.locale);
        Cursor cursor = DbHelper.instance(this).findByType(companyType, locale);

        if (cursor.moveToFirst()) {
            int id = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.ID);
            int isMember = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_MEMBER);
            int isHuntingGround = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_HUNTING_GROUND);
            int isFishingGround = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_FISHING_GROUND);
            int isPondFarm = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.IS_POND_FARM);
            int lat = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.LAT);
            int lng = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.LNG);
            int name = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.NAME);
            int area = cursor.getColumnIndex(DbHelper.DbSchema.CompanyTable.Column.AREA);

            do {
                if(cursor.isNull(lat) || cursor.isNull(lng)) {
                    continue;
                }

                Double areaValue = null;
                if(!cursor.isNull(area)) {
                    areaValue = cursor.getDouble(area);
                }
                Company item = new Company(cursor.getLong(id), cursor.getInt(isMember),
                        cursor.getInt(isHuntingGround), cursor.getInt(isFishingGround),
                        cursor.getInt(isPondFarm), cursor.getDouble(lat), cursor.getDouble(lng),
                        cursor.getString(name), areaValue);

                // Add cluster items (markers) to the cluster manager.
                mClusterManager.addItem(item);
            } while (cursor.moveToNext());
        }

        googleMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        LatLng ukraine = new LatLng(49.463006, 31.201909);
        float zoomLevel = 6.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ukraine, zoomLevel));
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<Company>(this, mMap);

        mClusterManager.setRenderer(new DefaultClusterRenderer<Company>(getApplicationContext(), mMap, mClusterManager) {
            @Override
            protected void onBeforeClusterItemRendered(Company company, MarkerOptions markerOptions) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunting_target));
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("hunting_target", 50, 50)));
            }

            @Override
            protected void onBeforeClusterRendered(Cluster<Company> cluster, MarkerOptions markerOptions) {
                super.onBeforeClusterRendered(cluster, markerOptions);
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_hunt));
            }

            @Override
            protected boolean shouldRenderAsCluster(Cluster cluster) {
                // Always render clusters.
                return cluster.getSize() > 1;
            }
        });

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Company>() {
            @Override
            public boolean onClusterItemClick(Company item) {
                clickedClusterItem = item;
                return false;
            }
        });

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Company>() {
            @Override
            public void onClusterItemInfoWindowClick(Company company) {
                Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);
                intent.putExtra("id", Long.toString(company.getId()));
                startActivity(intent);
            }
        });

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Company>() {
            @Override
            public boolean onClusterClick(Cluster<Company> cluster) {
                // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
                // inside of bounds, then animate to center of the bounds.

                // Create the builder to collect all essential cluster items for the bounds.
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                // Get the LatLngBounds
                final LatLngBounds bounds = builder.build();

                // Animate camera to the bounds
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {
        private final View contentView;

        MyCustomAdapterForItems() {
            contentView = getLayoutInflater().inflate(R.layout.info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            TextView tv_id = ((TextView) contentView.findViewById(R.id.tv_id));
            TextView tv_company_name = ((TextView) contentView.findViewById(R.id.tv_company_name));
//            TextView tv_position = (TextView) contentView.findViewById(R.id.tv_position);
            TextView tv_area = (TextView) contentView.findViewById(R.id.tv_area);
            TextView tv_oblast = (TextView) contentView.findViewById(R.id.tv_oblast);

            if (clickedClusterItem != null) {
                tv_id.setText(Long.toString(clickedClusterItem.getId()));
                tv_company_name.setText(clickedClusterItem.getName());
//                tv_position.setText(clickedClusterItem.getLat() + ", " + clickedClusterItem.getLng());

                if (clickedClusterItem.getArea() == null) {
                    tv_area.setVisibility(View.GONE);
                } else {
                    tv_area.setText(clickedClusterItem.getArea() + " " + getResources().getString(R.string.kilo_ha));
                    tv_area.setVisibility(View.VISIBLE);
                }
//                String oblastName = DbHelper.instance().findOblastById(clickedClusterItem.getOblastId().toString());
//                tv_oblast.setText(oblastName);
            }
            return contentView;
        }
    }

//    public Bitmap resizeBitmap(String drawableName,int width, int height){
//        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(drawableName, "drawable", getPackageName()));
//        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
//    }


}
