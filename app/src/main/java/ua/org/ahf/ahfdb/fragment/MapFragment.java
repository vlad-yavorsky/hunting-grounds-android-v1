package ua.org.ahf.ahfdb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import ua.org.ahf.ahfdb.activity.MapsActivity;
import ua.org.ahf.ahfdb.R;

public class MapFragment extends Fragment implements OnClickListener {

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        view.findViewById(R.id.huntingGroundsButton).setOnClickListener(this);
        view.findViewById(R.id.fishingGroundButton).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        switch (view.getId()) {
            case R.id.huntingGroundsButton:
                intent.putExtra("COMPANY_TYPE", 1);
                break;
            case R.id.fishingGroundButton:
                intent.putExtra("COMPANY_TYPE", 2);
                break;
        }
        startActivity(intent);
    }

}
