package ua.org.ahf.ahfdb.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import ua.org.ahf.ahfdb.activity.DetailsActivity;
import ua.org.ahf.ahfdb.activity.NavigationActivity;
import ua.org.ahf.ahfdb.helper.DbHelper;
import ua.org.ahf.ahfdb.helper.DbSchema;
import ua.org.ahf.ahfdb.helper.Utils;
import ua.org.ahf.ahfdb.model.Company;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap mMap;
    private ClusterManager<Company> mClusterManager;
    private Company clickedClusterItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.setMyLocationEnabled(true);

        setUpClusterer();
        addMarkers();

        googleMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        LatLng ukraine = new LatLng(49.463006, 31.201909);
        float zoomLevel = 6.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ukraine, zoomLevel));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setMapType(preferences.getInt(getString(R.string.key_map_type), 1));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(R.string.map);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void addMarkers() {
//        int companyType = getIntent().getIntExtra("COMPANY_TYPE", 1);
        String locale = getString(R.string.locale);
        Cursor cursor = DbHelper.instance(getActivity()).findAll(locale, "all");

        if (cursor.moveToFirst()) {
            int id = cursor.getColumnIndex(DbSchema.CompanyTable.Column.ID);
            int isMember = cursor.getColumnIndex(DbSchema.CompanyTable.Column.IS_MEMBER);
            int isHuntingGround = cursor.getColumnIndex(DbSchema.CompanyTable.Column.IS_HUNTING_GROUND);
            int isFishingGround = cursor.getColumnIndex(DbSchema.CompanyTable.Column.IS_FISHING_GROUND);
            int isPondFarm = cursor.getColumnIndex(DbSchema.CompanyTable.Column.IS_POND_FARM);
            int lat = cursor.getColumnIndex(DbSchema.CompanyTable.Column.LAT);
            int lng = cursor.getColumnIndex(DbSchema.CompanyTable.Column.LNG);
            int name = cursor.getColumnIndex(DbSchema.CompanyTable.Column.NAME);
            int area = cursor.getColumnIndex(DbSchema.CompanyTable.Column.AREA);

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
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<Company>(getActivity(), mMap);

        mClusterManager.setRenderer(new DefaultClusterRenderer<Company>(getActivity(), mMap, mClusterManager) {
            @Override
            protected void onBeforeClusterItemRendered(Company company, MarkerOptions markerOptions) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunting_target));
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
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
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
            contentView = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
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
                    tv_area.setText(clickedClusterItem.getArea() + " " + getString(R.string.kilo_ha));
                    tv_area.setVisibility(View.VISIBLE);
                }
//                String oblastName = DbHelper.instance().findOblastById(clickedClusterItem.getOblastId().toString());
//                tv_oblast.setText(oblastName);
            }
            return contentView;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMapType(int type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(getString(R.string.key_map_type), type);
        edit.apply();
        switch (type) {
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return;
            case 4:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return;
        }
    }
}