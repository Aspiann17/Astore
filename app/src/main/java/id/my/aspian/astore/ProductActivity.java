package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.execute;
import static id.my.aspian.astore.Utils.toast;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    TextInputEditText raw_product_name, raw_product_price, raw_product_rating, raw_product_description;
    SwipeRefreshLayout refresh_layout;
    ListView list_product;
    Dialog dialog;

    StoreDatabase db;
    ArrayList<Map<String, String>> product_data;
    String category, action, product_id = null;


    // Toolbar
    // Back Button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Option
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.add_product) {

            // Mengosongkan Input
            for (TextInputEditText i : new TextInputEditText[]{
                    raw_product_name, raw_product_price,
                    raw_product_rating, raw_product_description
            }) i.setText("");

            action = "add";
            dialog.show();
        } else if (item_id == R.id.add_tmp_product) {

            // Membuat data dummy
            execute(() -> {
                Product product = new Product();
                product.name = "Rawr";
                product.price = 2000;
                product.rating = 5;
                product.category = "Cloth";
                product.description = "Tidak ada";

                db.productDao().insert(product);
                show_product();
            });
        }

        return false;
    }
    // end

    // SwipeRefresh
    @Override
    protected void onResume() {
        super.onResume();
        execute(this::show_product);
        refresh_layout.setRefreshing(false);
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
        list_product.setEmptyView(findViewById(R.id.empty_product));

        // Event

        // Delete
        list_product.setOnItemLongClickListener((parent, view, position, id) -> {
            try {
                int product_id = Integer.parseInt(((TextView) view.findViewById(R.id.product_id)).getText().toString());
                execute(() -> {
                    db.productDao().delete(product_id);
                    show_product();
                });

                return true;
            } catch (Exception e) {
                toast(this, "Terjadi error pada bagian 'list_product.setOnItemLongClickListener'");
                return false;
            }
        });

        // Update
        list_product.setOnItemClickListener((parent, view, position, id) -> {
            product_id = ((TextView) view.findViewById(R.id.product_id)).getText().toString();
            action = "update";

            execute(() -> {
                Product product = db.productDao().get(Integer.parseInt(product_id));

                runOnUiThread(() -> {
                    raw_product_name.setText(product.name);
                    raw_product_price.setText(String.valueOf(product.price));
                    raw_product_rating.setText(String.valueOf(product.rating));
                    raw_product_description.setText(product.description);
                    dialog.show();
                });
            });
        });
        // end

        // Product Dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_product_input);
        dialog.findViewById(R.id.submit).setOnClickListener(v -> validate());

        raw_product_name = dialog.findViewById(R.id.product_name);
        raw_product_price = dialog.findViewById(R.id.product_price);
        raw_product_rating = dialog.findViewById(R.id.product_rating);
        raw_product_description = dialog.findViewById(R.id.product_description);
        // end

        // SwipeRefresh
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(() -> {
            execute(this::show_product);
            refresh_layout.setRefreshing(false);
        });
        // end

        execute(this::show_product);
    }

    private void validate() {
        try {
            String product_name = Objects.requireNonNull(raw_product_name.getText()).toString().trim();
            String product_price = Objects.requireNonNull(raw_product_price.getText()).toString().trim();
            String product_rating = Objects.requireNonNull(raw_product_rating.getText()).toString().trim();
            String product_description = Objects.requireNonNull(raw_product_description.getText()).toString().trim();

            // Validation
            if (product_name.isEmpty()) {
                raw_product_name.setError("Name must be filled");
                return;
            } else if (product_price.isEmpty()) {
                raw_product_price.setError("Price cannot be empty");
                return;
            } else if (product_rating.isEmpty()) {
                raw_product_rating.setError("Rating cannot be empty");
                return;
            } else if (product_description.isEmpty()) {
                raw_product_description.setError("Description cannot be empty");
                return;
            } else {
                for (TextInputEditText i : new TextInputEditText[]{
                        raw_product_name, raw_product_price,
                        raw_product_rating, raw_product_description
                }) i.setError(null);
            }

            Product product = new Product();
            product.name = product_name;
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

            dialog.dismiss();
        } catch (Exception e) {
            toast(this, "Terjadi error pada validate", String.valueOf(e));
        } finally {
            execute(this::show_product);
        }
    }

    private void show_product() {
        product_data = Product.get_all(db);

        if (product_data.isEmpty()) return;

        runOnUiThread(() -> {
            list_product.setAdapter(new SimpleAdapter(
                    this, product_data, R.layout.list_products,
                    new String[]{"product_id", "product_name", "product_price", "product_rating", "product_category", "product_description"},
                    new int[]{R.id.product_id, R.id.product_name, R.id.product_price, R.id.product_rating, R.id.product_category, R.id.product_description}
            ));
        });
    }
}