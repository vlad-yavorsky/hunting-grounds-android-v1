package ua.org.ahf.ahfdb.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ua.org.ahf.ahfdb.Company;
import ua.org.ahf.ahfdb.DbHelper;
import ua.org.ahf.ahfdb.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateFragment extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String JSON_URL = "http://ahf.org.ua/get-data.php";
    private String myJSON;
    private JSONArray companies;
    private static final String TAG_RESULT = "result";

    private OnFragmentInteractionListener mListener;

    public UpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateFragment newInstance(String param1, String param2) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        view.findViewById(R.id.updateDatabaseButton).setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateDatabaseButton:
                clearDatabase();
                getJSON(JSON_URL);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void clearDatabase() {
        DbHelper.instance(getActivity()).deleteAllCompanies();
    }

    private void getJSON(String url) {
        class GetJSON extends AsyncTask<String, Void, String> {
//            ProgressDialog loading;

//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(UpdateFragment.this, "Please Wait...",null,true,true);
//            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
//                super.onPostExecute(s);
                myJSON = result;
                parseJSON();
//                loading.dismiss();
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }

    protected void parseJSON(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            companies = jsonObj.getJSONArray(TAG_RESULT);

            for(int i = 0; i < companies.length(); i++){
                JSONObject c = companies.getJSONObject(i);

                Long id = c.getLong(DbHelper.DbSchema.CompanyTable.Column.ID);
                Integer isMember = c.getInt(DbHelper.DbSchema.CompanyTable.Column.IS_MEMBER);
                Integer isHuntingGround = c.getInt(DbHelper.DbSchema.CompanyTable.Column.IS_HUNTING_GROUND);
                Integer isFishingGround = c.getInt(DbHelper.DbSchema.CompanyTable.Column.IS_FISHING_GROUND);
                Integer isPondFarm = c.getInt(DbHelper.DbSchema.CompanyTable.Column.IS_POND_FARM);
                String name = c.getString(DbHelper.DbSchema.CompanyTable.Column.NAME);
                String description = c.getString(DbHelper.DbSchema.CompanyTable.Column.DESCRIPTION);
                String website = c.getString(DbHelper.DbSchema.CompanyTable.Column.WEBSITE);
                String email = c.getString(DbHelper.DbSchema.CompanyTable.Column.EMAIL);
                String juridicalAddress = c.getString(DbHelper.DbSchema.CompanyTable.Column.JURIDICAL_ADDRESS);
                String actualAddress = c.getString(DbHelper.DbSchema.CompanyTable.Column.ACTUAL_ADDRESS);
                String director = c.getString(DbHelper.DbSchema.CompanyTable.Column.DIRECTOR);
                Integer isEnabled = c.getInt(DbHelper.DbSchema.CompanyTable.Column.IS_ENABLED);
                Integer oblastId = c.getInt(DbHelper.DbSchema.CompanyTable.Column.OBLAST_ID);

                Double area = null;
                if(!c.isNull(DbHelper.DbSchema.CompanyTable.Column.AREA)) {
                    area = c.getDouble(DbHelper.DbSchema.CompanyTable.Column.AREA);
                }

                Double lat = null;
                if(!c.isNull(DbHelper.DbSchema.CompanyTable.Column.LAT)) {
                    lat = c.getDouble(DbHelper.DbSchema.CompanyTable.Column.LAT);
                }

                Double lng = null;
                if(!c.isNull(DbHelper.DbSchema.CompanyTable.Column.LNG)) {
                    lng = c.getDouble(DbHelper.DbSchema.CompanyTable.Column.LNG);
                }


                Company company = new Company(id, isMember, isHuntingGround, isFishingGround,
                        isPondFarm, area, lat, lng, name, description, website, email, juridicalAddress,
                        actualAddress, director, isEnabled, oblastId);
                DbHelper.instance(getActivity()).create(company);
            }
            DbHelper.instance(getActivity()).createOblasts();
            Toast.makeText(getActivity(), "Update success!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Update failed!", Toast.LENGTH_SHORT).show();
        }
    }
}

// TODO: Check internet connection, after that download latest data and clear/update database