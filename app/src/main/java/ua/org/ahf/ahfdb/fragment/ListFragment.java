package ua.org.ahf.ahfdb.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ua.org.ahf.ahfdb.activity.NavigationActivity;
import ua.org.ahf.ahfdb.helper.DbHelper;
import ua.org.ahf.ahfdb.activity.DetailsActivity;
import ua.org.ahf.ahfdb.R;
import ua.org.ahf.ahfdb.helper.DbSchema;

public class ListFragment extends Fragment {

    private SimpleCursorAdapter simpleCursorAdapter = null;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().getString("type") == "all") {
            ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(R.string.catalog);
        } else {
            ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(R.string.favorites);
            reloadData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        ListView listView = (ListView) view.findViewById(R.id.lv_companies);

        String[] columns = {
                DbSchema.CompanyTable.Column.ID,
                DbSchema.CompanyTable.Column.NAME,
                DbSchema.CompanyTable.Column.OBLAST_ID,
                DbSchema.CompanyTable.Column.IS_MEMBER,
                DbSchema.CompanyTable.Column.AREA
        };
        int[] resourceIds = {
                R.id.tv_id,
                R.id.tv_name,
                R.id.tv_oblast,
                R.id.tv_short_info,
                R.id.tv_short_info
        };
        String locale = getString(R.string.locale);
        Cursor cursor = DbHelper.instance(getActivity()).findAll(locale, getArguments().getString("type"));

        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_row, cursor, columns, resourceIds, 0);
        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                // member of associations column
                if (columnIndex == 1) {
                    if(cursor.getString(columnIndex).equals("1")) {
                        ((TextView)view).setText(R.string.memberOfAssociation);
                    } else {
                        ((TextView)view).setText("");
                    }
                    return true;
                }
                // area column
                if (columnIndex == 5) {
                    if(!cursor.isNull(columnIndex)) {
                        TextView textView = (TextView) view;
                        if(!textView.getText().equals("")) {
                            textView.setText(textView.getText() + " • ");
                        }
                        textView.setText(textView.getText() + cursor.getString(columnIndex) + " " + getString(R.string.kilo_ha));
                    }
                    return true;
                }
                // oblast column
                if (columnIndex == 17) {
                    TextView textView = (TextView)view;
                    String oblastName = DbHelper.instance(getActivity()).findOblastById(cursor.getString(columnIndex));
                    textView.setText(oblastName);
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
                }
            }
        );

        FilterQueryProvider provider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String locale = getString(R.string.locale);
                if (TextUtils.isEmpty(constraint)) {
                    return DbHelper.instance(getActivity()).findAll(locale, getArguments().getString("type"));
                }
                return DbHelper.instance(getActivity()).findByName(constraint.toString(), locale, getArguments().getString("type"));
            }
        };
        simpleCursorAdapter.setFilterQueryProvider(provider);

        return view;
    }

    public void reloadData() {
        Cursor cursor = null;
        String locale = getString(R.string.locale);
        cursor = DbHelper.instance(getActivity()).findAll(locale, getArguments().getString("type"));
        simpleCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.catalog, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String string) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String string) {
                simpleCursorAdapter.getFilter().filter(string);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
