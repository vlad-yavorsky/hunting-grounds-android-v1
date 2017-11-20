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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Iterator;

import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.activity.DetailsActivity;
import ua.org.ahf.ahfdb.activity.NavigationActivity;
import ua.org.ahf.ahfdb.helper.DbHelper;
import ua.org.ahf.ahfdb.helper.DbSchema;
import ua.org.ahf.ahfdb.model.Company;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap mMap;
    private ClusterManager<Company> mClusterManager;
    private Company clickedItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        view.findViewById(R.id.ll_info_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id", Long.toString(clickedItem.getId()));
                startActivity(intent);
            }
        });

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

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
//        mMap.setMyLocationEnabled(false);

        setUpClusterer();
        addMarkers();

        LatLng ukraine = new LatLng(49.463006, 31.201909);
        float zoomLevel = 6.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ukraine, zoomLevel));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                closeInfoWindow();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setMapType(preferences.getInt(getString(R.string.key_map_type), GoogleMap.MAP_TYPE_NORMAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(R.string.map);
        if(clickedItem != null) {
            getView().findViewById(R.id.ll_info_window).setVisibility(View.VISIBLE);
            openInfoWindow(clickedItem);
        }
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
            int oblastId = cursor.getColumnIndex(DbSchema.CompanyTable.Column.OBLAST_ID);
            int territoryCoords = cursor.getColumnIndex(DbSchema.CompanyTable.Column.TERRITORY_COORDS);

            do {
                if(cursor.isNull(lat) || cursor.isNull(lng)) {
                    continue;
                }

                Double areaValue = null;
                if(!cursor.isNull(area)) {
                    areaValue = cursor.getDouble(area);
                }

                String territoryCoordsValue = null;
                if(!cursor.isNull(territoryCoords)) {
                    territoryCoordsValue = cursor.getString(territoryCoords);
                }
                Company company = new Company(getActivity(), cursor.getLong(id), cursor.getInt(isMember),
                        cursor.getInt(isHuntingGround), cursor.getInt(isFishingGround),
                        cursor.getInt(isPondFarm), cursor.getDouble(lat), cursor.getDouble(lng),
                        cursor.getString(name), areaValue, cursor.getInt(oblastId), territoryCoordsValue);

                // Add cluster items (markers) to the cluster manager.
                mClusterManager.addItem(company);
                if(company.getPolygonOptions() != null) {
                    company.setPolygon(mMap.addPolygon(company.getPolygonOptions()));
                }
            } while (cursor.moveToNext());
        }
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        mClusterManager = new ClusterManager<Company>(getActivity(), mMap);

        mClusterManager.setRenderer(new DefaultClusterRenderer<Company>(getActivity(), mMap, mClusterManager) {
            @Override
            protected void onBeforeClusterItemRendered(Company company, MarkerOptions markerOptions) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunting_target));
                // centering marker
                markerOptions.anchor(0.5f, 0.5f);
            }

            @Override
            protected void onBeforeClusterRendered(Cluster<Company> cluster, MarkerOptions markerOptions) {
                super.onBeforeClusterRendered(cluster, markerOptions);
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hunting_target));
            }

            @Override
            protected boolean shouldRenderAsCluster(Cluster cluster) {
                // Always render clusters.
                return cluster.getSize() > 1;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Company>() {
            @Override
            public boolean onClusterItemClick(Company item) {
                openInfoWindow(item);
                return false;
            }
        });

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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map, menu);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int type = preferences.getInt(getString(R.string.key_map_type), GoogleMap.MAP_TYPE_NORMAL);

        switch (type) {
            case GoogleMap.MAP_TYPE_NORMAL:
                menu.findItem(R.id.map_type_normal).setChecked(true);
                break;
            case GoogleMap.MAP_TYPE_HYBRID:
                menu.findItem(R.id.map_type_hybrid).setChecked(true);
                break;
            case GoogleMap.MAP_TYPE_TERRAIN:
                menu.findItem(R.id.map_type_terrain).setChecked(true);
                break;
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMapType(int newType) {
        int prevMapType = mMap.getMapType();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(getString(R.string.key_map_type), newType);
        edit.apply();
        mMap.setMapType(newType);
        // optimization
        if((prevMapType == GoogleMap.MAP_TYPE_NORMAL && newType == GoogleMap.MAP_TYPE_TERRAIN) ||
                (prevMapType == GoogleMap.MAP_TYPE_TERRAIN && newType == GoogleMap.MAP_TYPE_NORMAL)) {
            return;
        }
        // change colors of icons, when changes map type
//        Iterator<Marker> iterator = mClusterManager.getMarkerCollection().getMarkers().iterator();
//        while(iterator.hasNext()){
//            iterator.next().setIcon();
//        }
        // change colors of borders, when changes map type
        Iterator<Company> iterator = mClusterManager.getAlgorithm().getItems().iterator();
        while(iterator.hasNext()){
            Company company = iterator.next();
            if(company == clickedItem) {
                company.setPolygonColor("selected", newType);
            } else {
                company.setPolygonColor("deselected", newType);
            }
        }
    }

    private void closeInfoWindow() {
        if(clickedItem != null) {
            clickedItem.setPolygonColor("deselected", mMap.getMapType());
            clickedItem = null;
            getView().findViewById(R.id.ll_info_window).setVisibility(View.GONE);
        }
    }

    private void openInfoWindow(Company item) {
        View contentView = getView();

        ((TextView) contentView.findViewById(R.id.tv_company_name)).setText(item.getName());
        ((TextView) contentView.findViewById(R.id.tv_oblast)).setText(item.getOblastName());

        TextView tv_area = (TextView) contentView.findViewById(R.id.tv_area);
        if (item.getArea() == null) {
            tv_area.setVisibility(View.GONE);
        } else {
            tv_area.setText(item.getArea() + " " + getString(R.string.kilo_ha));
            tv_area.setVisibility(View.VISIBLE);
        }

        if (clickedItem == null) {
            contentView.findViewById(R.id.ll_info_window).setVisibility(View.VISIBLE);
        }

        // if earlier marker was selected, set the borders of hunting ground to blue color
        if(clickedItem != null) {
            clickedItem.setPolygonColor("deselected", mMap.getMapType());
        }
        // set the borders of selected hunting ground to red color
        item.setPolygonColor("selected", mMap.getMapType());

        clickedItem = item;
    }

}