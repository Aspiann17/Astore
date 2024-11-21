package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.toast;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView list_category = view.findViewById(R.id.list_category);
        list_category.setOnItemClickListener((parent, category_view, position, id) -> {
            String category_id = ((TextView) category_view.findViewById(R.id.category_id)).getText().toString();
            String category_title = ((TextView) category_view.findViewById(R.id.category_title)).getText().toString();

            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra("category_id", category_id);
            intent.putExtra("category_title", category_title);
            startActivity(intent);
        });

        tmp_data(view);

        return view;
    }

    public void tmp_data(View view) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("category_id", "" + i);
            map.put("category_title", "Title" + i);
            map.put("product_available", "0" + i);

            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
            getContext(), list, R.layout.list_category,
            new String[]{"category_id", "category_title", "product_available"},
            new int[]{R.id.category_id, R.id.category_title, R.id.product_available}
        );

        ((ListView) view.findViewById(R.id.list_category)).setAdapter(adapter);
    }
}