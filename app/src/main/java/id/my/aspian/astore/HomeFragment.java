package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.execute;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    StoreDatabase db;
    ListView list_category;
    SwipeRefreshLayout refresh_layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = Room.databaseBuilder(requireActivity(), StoreDatabase.class, "store").build();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this::refresh);

        list_category = view.findViewById(R.id.list_category);
        list_category.setEmptyView(view.findViewById(R.id.empty_list));
        list_category.setOnItemClickListener((parent, category_view, position, id) -> {
            String category_title = ((TextView) category_view.findViewById(R.id.category_title)).getText().toString();

            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra("category", category_title);
            startActivity(intent);
        });

        refresh();
        return view;
    }

    private void show_card() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        String[] categories = new String[]{
            "Makanan", "Minuman", "Pakaian", "Elektronik", "Rumah Tangga"
        };

        for (String category : categories) {
            list.add(new HashMap<>() {{
                put("category_title", category);
                put("category_count", String.valueOf(db.productDao().count(category.toLowerCase())));
            }});
        }

        if (getActivity() != null) {
            requireActivity().runOnUiThread(() -> list_category.setAdapter(new SimpleAdapter(

                getContext(), list, R.layout.list_category,
                new String[]{"category_title", "category_count"},
                new int[]{R.id.category_title, R.id.product_available}
            )));
        }
    }

    private void refresh() {
        refresh_layout.setRefreshing(true);
        execute(this::show_card);
        refresh_layout.setRefreshing(false);
    }
}