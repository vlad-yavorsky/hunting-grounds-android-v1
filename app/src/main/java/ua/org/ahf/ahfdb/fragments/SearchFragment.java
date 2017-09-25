package ua.org.ahf.ahfdb.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ua.org.ahf.ahfdb.DbHelper;
import ua.org.ahf.ahfdb.DetailsActivity;
import ua.org.ahf.ahfdb.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView lvCompanies;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ListView listView = (ListView) view.findViewById(R.id.lv_companies);
        String[] columns = {
                DbHelper.DbSchema.CompanyTable.Column.ID,
                DbHelper.DbSchema.CompanyTable.Column.NAME,
                DbHelper.DbSchema.CompanyTable.Column.IS_MEMBER,
                DbHelper.DbSchema.CompanyTable.Column.AREA
        };
        int[] resourceIds = {
                R.id.tv_id,
                R.id.tv_name,
                R.id.tv_short_info,
                R.id.tv_short_info
        };
        Cursor cursor = DbHelper.instance(getActivity()).fetchAll();
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_row, cursor, columns, resourceIds, 0);

        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 1) {
                    if(cursor.getString(columnIndex).equals("1")) {
                        ((TextView)view).setText(R.string.memberOfAssociation);
                    } else {
                        ((TextView)view).setText("");
                    }
                    return true;
                }
                if (columnIndex == 5) {
                    if(!cursor.isNull(columnIndex)) {
                        TextView textView = (TextView) view;
                        if(!textView.getText().equals("")) {
                            textView.setText(textView.getText() + " • ");
                        }
                        textView.setText(textView.getText() + cursor.getString(columnIndex) + " " + getResources().getString(R.string.kilo_ha));
                    }
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(simpleCursorAdapter);
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("id", Long.toString(id));
                    startActivity(intent);
//                    Cursor cursor = (Cursor)parent.getItemAtPosition(position);
//                    String _id = "";
//                    String name = "";
//                    if(cursor.moveToPosition(position)) {
//                        _id = cursor.getString(cursor.getColumnIndex("_id"));
//                        name = cursor.getString(cursor.getColumnIndex("name"));
//                    }
//                    Log.d("blah", "itemClick: id = " + _id + ", name = " + name + ", id = " + id);
                }
            }
        );

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
}

// TODO: Add filter options (name, region, territory area) and region of company on the right side
// TODO: Add info how far is hunting ground from user (Member of Association • 10 kilo ha. • 50 km)