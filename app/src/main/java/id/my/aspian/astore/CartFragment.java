package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.execute;
import static id.my.aspian.astore.Utils.format;
import static id.my.aspian.astore.Utils.star;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    StoreDatabase db;
    ListView list_cart;
    SwipeRefreshLayout refresh_layout;

    ArrayList<Map<String, String>> product_data;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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

        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        list_cart = view.findViewById(R.id.list_cart);

        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this::refresh);

        execute(() -> {
            ArrayList<Map<String, String>> list = Cart.get_all(db);

            requireActivity().runOnUiThread(() -> {
                list_cart.setAdapter(new SimpleAdapter(
                        getContext(), list, R.layout.list_products,
                        new String[]{"product_id", "product_name", "product_price", "product_amount", "total_price"},
                        new int[]{R.id.product_id, R.id.product_name, R.id.product_price, R.id.product_rating, R.id.product_description}
                ));
            });
        });

        return view;
    }

    private void refresh() {
        refresh_layout.setRefreshing(true);
        refresh_layout.setRefreshing(false);
    }
}