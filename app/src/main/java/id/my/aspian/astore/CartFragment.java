package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.execute;
import static id.my.aspian.astore.Utils.format;
import static id.my.aspian.astore.Utils.toast;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    StoreDatabase db;
    View view;
    ListView list_cart;
    TextInputEditText dialog_input;
    SwipeRefreshLayout refresh_layout;
    Dialog amount_dialog, checkout_dialog;
    Menu toolbar_menu;

    String cart_id, product_id;

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

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_toolbar_menu, menu);

        toolbar_menu = menu;

        // Menghilangkan option menu agar tidak duplikat
        menu.findItem(R.id.user_mode).setVisible(false);
        menu.findItem(R.id.admin_mode).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_cart_data) {
            execute(() -> db.cartDao().delete_all());
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (toolbar_menu != null) {
            toolbar_menu.findItem(R.id.delete_cart_data).setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (toolbar_menu != null) {
            toolbar_menu.findItem(R.id.delete_cart_data).setVisible(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = Room.databaseBuilder(requireActivity(), StoreDatabase.class, "store").build();
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Dialog
        amount_dialog = new Dialog(requireContext());
        amount_dialog.setContentView(R.layout.dialog_product_amount);
        amount_dialog.findViewById(R.id.submit).setOnClickListener(v -> handle_modify_amount());
        dialog_input = amount_dialog.findViewById(R.id.product_amount);
        TextInputLayout dialog_input_container = amount_dialog.findViewById(R.id.input_container);
        TextView dialog_title = amount_dialog.findViewById(R.id.product_name);

        checkout_dialog = new Dialog(requireContext());
        checkout_dialog.setContentView(R.layout.dialog_checkout);
        checkout_dialog.findViewById(R.id.close_button).setOnClickListener(v -> {
            checkout_dialog.dismiss();
        });

        checkout_dialog.findViewById(R.id.close_button).setOnClickListener(v -> {
            execute(() -> db.cartDao().delete_all());
            checkout_dialog.dismiss();
        });

        // List
        list_cart = view.findViewById(R.id.list_cart);
        list_cart.setEmptyView(view.findViewById(R.id.empty_list));
        list_cart.setOnItemLongClickListener((parent, v, position, id) -> {
            String cart_id = ((TextView) v.findViewById(R.id.product_category)).getText().toString();
            execute(() -> db.cartDao().delete(cart_id));

            refresh();
            return true;
        });

        // Update
        list_cart.setOnItemClickListener((parent, v, position, id) -> {
            String product_name = ((TextView) v.findViewById(R.id.product_name)).getText().toString();
            cart_id = ((TextView) v.findViewById(R.id.product_category)).getText().toString();
            product_id = ((TextView) v.findViewById(R.id.product_id)).getText().toString();
            dialog_title.setText(product_name);
            amount_dialog.show();

            execute(() -> {
                int stock = db.productDao().get(Integer.parseInt(product_id)).stock;
                dialog_input_container.setSuffixText("(" + stock + " pcs)");
            });
        });

        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this::refresh);

        view.findViewById(R.id.checkout_button).setOnClickListener(v -> checkout_dialog.show());

        refresh();
        return view;
    }

    private void handle_modify_amount() {
        String amount = ((TextView) amount_dialog.findViewById(R.id.product_amount)).getText().toString().trim();

        if (amount.isEmpty()) {
            dialog_input.setError("Amount cannot be empty");
            return;
        } else dialog_input.setError(null);

        execute(() -> {
            int product_stock = db.productDao().get(Integer.parseInt(product_id)).stock;

            if (product_stock < Integer.parseInt(amount)) {
                requireActivity().runOnUiThread(() -> {
                    toast(getContext(), "Not enough stock");
                    dialog_input.setError("Not enough stock");
                });

                return;
            }

            Cart cart = new Cart();
            cart.id = Integer.parseInt(cart_id);
            cart.product_id = Integer.parseInt(product_id);
            cart.quantity = Integer.parseInt(amount);
            db.cartDao().update(cart);
        });

        amount_dialog.dismiss();
    }

    private void refresh() {
        refresh_layout.setRefreshing(true);

        execute(() -> {
            ArrayList<Map<String, String>> list = Cart.get_all(db);
            long total_price = Cart.get_total(db);

            if (list.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    list_cart.setAdapter(null);
                    refresh_layout.setRefreshing(false);
                });
                return;
            }

            requireActivity().runOnUiThread(() -> {
                list_cart.setAdapter(new SimpleAdapter(
                    requireContext(), list, R.layout.list_products,
                    new String[]{"cart_id", "product_id", "product_name", "product_stock", "product_price", "product_amount", "total_price"},
                    new int[]{R.id.product_category, R.id.product_id, R.id.product_name, R.id.product_stock, R.id.product_price, R.id.product_rating, R.id.product_description}
                ));

                ((TextView) view.findViewById(R.id.total_price)).setText(format(total_price));
                refresh_layout.setRefreshing(false);
            });
        });
    }
}