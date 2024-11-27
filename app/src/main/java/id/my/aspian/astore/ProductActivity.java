package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.execute;
import static id.my.aspian.astore.Utils.toast;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ProductActivity extends AppCompatActivity {
    TextInputEditText raw_product_name, raw_product_stock, raw_product_price, raw_product_rating, raw_product_description, raw_product_amount;
    SwipeRefreshLayout refresh_layout;
    ListView list_product;
    Dialog admin_dialog, user_dialog;

    StoreDatabase db;
    SharedPreferences preferences;
    ArrayList<Map<String, String>> product_data;
    String category, action, product_id, role = null;

    // Toolbar
    // Back Button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Option
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_toolbar_menu, menu);

        boolean is_admin = role.equals("admin");
        menu.findItem(R.id.add_product).setVisible(is_admin);
        menu.findItem(R.id.add_tmp_product).setVisible(is_admin);
        menu.findItem(R.id.to_cart).setVisible(!is_admin);

        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.add_product) {

            // Mengosongkan Input
            for (TextInputEditText i : new TextInputEditText[]{
                    raw_product_name, raw_product_price,
                    raw_product_rating, raw_product_description,
                    raw_product_stock
            }) i.setText("");

            action = "add";
            admin_dialog.show();
        } else if (item_id == R.id.add_tmp_product) {

            // Membuat data dummy
            execute(() -> {
                Product product = new Product();
                product.name = "Rawr";
                product.stock = 17;
                product.price = 2000;
                product.rating = 5;
                product.category = category.toLowerCase();
                product.description = "Tidak ada";

                db.productDao().insert(product);
            });

            refresh();
        } else if (item_id == R.id.to_cart) {
            // Menuju ke MainActivity sambil menghapus semua Activity di BackStack
            // dan langsung menampilkan CartFragment dengan menggunakan setSelectedItemId.
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("action", "cart");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    // end

    // SwipeRefresh
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
    // end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Database
        db = Room.databaseBuilder(getApplicationContext(), StoreDatabase.class, "store").build();
        // end

        // Preferences
        preferences = getSharedPreferences("session", MODE_PRIVATE);
        role = preferences.getString("role", "user");
        // end

        // Intent Data
        Bundle intent_data = getIntent().getExtras();
        if (intent_data != null) category = intent_data.getString("category");
        // end

        // Toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) toolbar.setTitle(category);
        // end

        // List Product
        list_product = findViewById(R.id.list_product);
        list_product.setEmptyView(findViewById(R.id.empty_list));

        // Event

        // Delete
        list_product.setOnItemLongClickListener((parent, view, position, id) -> {
            if (!role.equals("admin")) return false;

            try {
                String product_id = ((TextView) view.findViewById(R.id.product_id)).getText().toString();
                execute(() -> db.productDao().delete(Integer.parseInt(product_id)));
                refresh();

                return true;
            } catch (Exception e) {
                toast(this, "Terjadi error pada bagian 'list_product.setOnItemLongClickListener'");
                return false;
            }
        });

        list_product.setOnItemClickListener((parent, view, position, id) -> {
            if (role.equals("admin")) show_update_form(view);
            else if (role.equals("user")) show_user_form(view);
        });
        // end

        // Product Dialog
        // Admin
        admin_dialog = new Dialog(this);
        admin_dialog.setContentView(R.layout.dialog_form_product);
        admin_dialog.findViewById(R.id.submit).setOnClickListener(v -> handle_admin_form());
        ((TextView) admin_dialog.findViewById(R.id.dialog_title)).setText(category);

        raw_product_name = admin_dialog.findViewById(R.id.product_name);
        raw_product_stock = admin_dialog.findViewById(R.id.product_stock);
        raw_product_price = admin_dialog.findViewById(R.id.product_price);
        raw_product_rating = admin_dialog.findViewById(R.id.product_rating);
        raw_product_description = admin_dialog.findViewById(R.id.product_description);

        // User
        user_dialog = new Dialog(this);
        user_dialog.setContentView(R.layout.dialog_product_amount);
        user_dialog.findViewById(R.id.submit).setOnClickListener(v -> handle_user_form());

        raw_product_amount = user_dialog.findViewById(R.id.product_amount);
        // end

        // SwipeRefresh
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(this::refresh);
        // end

        refresh();
    }

    private void show_user_form(@NonNull View view) {
        product_id = ((TextView) view.findViewById(R.id.product_id)).getText().toString();
        String product_name = ((TextView) view.findViewById(R.id.product_name)).getText().toString();
        ((TextView) user_dialog.findViewById(R.id.product_name)).setText(product_name);
        user_dialog.show();
    }

    private void show_update_form(@NonNull View view) {
        product_id = ((TextView) view.findViewById(R.id.product_id)).getText().toString();
        action = "update";

        execute(() -> {
            Product product = db.productDao().get(Integer.parseInt(product_id));

            runOnUiThread(() -> {
                raw_product_name.setText(product.name);
                raw_product_stock.setText(String.valueOf(product.stock));
                raw_product_price.setText(String.valueOf(product.price));
                raw_product_rating.setText(String.valueOf(product.rating));
                raw_product_description.setText(product.description);
                admin_dialog.show();
            });
        });
    }

    private void handle_user_form() {
        String amount = Objects.requireNonNull(raw_product_amount.getText()).toString().trim();

        if (amount.isEmpty()) {
            raw_product_amount.setError("Amount cannot be empty");
            return;
        } else raw_product_amount.setError(null);

        execute(() -> {
            int product_stock = db.productDao().get(Integer.parseInt(product_id)).stock;

            if (product_stock < Integer.parseInt(amount)) {
                runOnUiThread(() -> {
                    toast(this, "Not enough stock");
                    raw_product_amount.setError("Not enough stock");
                    raw_product_amount.setText(String.valueOf(product_stock));
                });

                return;
            }

            Cart cart = new Cart();

            // Menambah jumlah jika pada cart sudah ada produk yang sama.
            if (db.cartDao().get(Integer.parseInt(product_id)) != null) {
                cart = db.cartDao().get(Integer.parseInt(product_id));
                cart.quantity += Integer.parseInt(amount);
                db.cartDao().update(cart);
            } else {
                // Menambahkan ke cart jika belum ada.
                cart.product_id = Integer.parseInt(product_id);
                cart.quantity = Integer.parseInt(amount);
                db.cartDao().insert(cart);
            }

            // Melakukan reset pada input jika operasi berhasil.
            runOnUiThread(() -> raw_product_amount.setText(""));
        });

        user_dialog.dismiss();
    }

    private void handle_admin_form() {
        try {
            String product_name = Objects.requireNonNull(raw_product_name.getText()).toString().trim();
            String product_stock = Objects.requireNonNull(raw_product_stock.getText()).toString().trim();
            String product_price = Objects.requireNonNull(raw_product_price.getText()).toString().trim();
            String product_rating = Objects.requireNonNull(raw_product_rating.getText()).toString().trim();
            String product_description = Objects.requireNonNull(raw_product_description.getText()).toString().trim();

            // Validation
            if (product_name.isEmpty()) {
                raw_product_name.setError("Name must be filled");
                return;
            } else if (product_stock.isEmpty()) {
                raw_product_stock.setError("Stock cannot be empty");
                return;
            } else if (product_price.isEmpty()) {
                raw_product_price.setError("Price cannot be empty");
                return;
            } else if (product_rating.isEmpty()) {
                raw_product_rating.setError("Rating cannot be empty");
                return;
            } else if (
                Integer.parseInt(product_rating) > 5 ||
                Integer.parseInt(product_rating) < 1
            ) {
                raw_product_rating.setError("Rating must be 1-5");
                return;
            } else if (product_description.isEmpty()) {
                raw_product_description.setError("Description cannot be empty");
                return;
            } else {
                for (TextInputEditText i : new TextInputEditText[]{
                        raw_product_name, raw_product_price,
                        raw_product_rating, raw_product_description,
                        raw_product_stock
                }) i.setError(null);
            }

            Product product = new Product();
            product.name = product_name;
            product.stock = Integer.parseInt(product_stock);
            product.price = Integer.parseInt(product_price);
            product.rating = Integer.parseInt(product_rating);
            product.category = category.toLowerCase();
            product.description = product_description;

            switch (action) {
                case "add":
                    execute(() -> db.productDao().insert(product));
                    break;
                case "update":
                    product.id = Integer.parseInt(product_id);
                    execute(() -> db.productDao().update(product));
                    break;
                default:
                    toast(this, "No Action");
                    break;
            }

            admin_dialog.dismiss();
        } catch (Exception e) {
            toast(this, "Terjadi error pada validate", String.valueOf(e));
        } finally {
            refresh();
        }
    }

    private void refresh() {
        refresh_layout.setRefreshing(true);

        execute(() -> {
            product_data = Product.get_all(db, category.toLowerCase());

            runOnUiThread(() -> {
                list_product.setAdapter(new SimpleAdapter(
                    this, product_data, R.layout.list_products,
                    new String[]{"product_id", "product_name", "product_stock","product_price", "product_rating", "product_category", "product_description"},
                    new int[]{R.id.product_id, R.id.product_name, R.id.product_stock,R.id.product_price, R.id.product_rating, R.id.product_category, R.id.product_description}
                ));

                refresh_layout.setRefreshing(false);
            });
        });
    }
}