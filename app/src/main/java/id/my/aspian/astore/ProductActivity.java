package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.execute;
import static id.my.aspian.astore.Utils.format;
import static id.my.aspian.astore.Utils.star;
import static id.my.aspian.astore.Utils.toast;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductActivity extends AppCompatActivity {
    StoreDatabase db;

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
            Dialog dialog = new Dialog(ProductActivity.this);
            dialog.setContentView(R.layout.dialog_product_input);
            dialog.show();
        }

        return false;
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
        String category = null;

        if (intent_data != null) category = intent_data.getString("category");
        // end

        // Toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(category);
        }
        // end

        // List Product
        ListView list_product = findViewById(R.id.list_product);
        list_product.setEmptyView(findViewById(R.id.empty_product));
        execute(() -> {
            list_product.setAdapter(new SimpleAdapter(
                    this, parse_data(), R.layout.list_products,
                    new String[]{"product_name", "product_price", "product_rating", "product_category", "product_description"},
                    new int[]{R.id.product_name, R.id.product_price, R.id.product_rating, R.id.product_category, R.id.product_description}
            ));
        });
        // end
    }

    private ArrayList<Map<String, String>> parse_data() {
        ArrayList<Map<String, String>> list = new ArrayList<>();

        for (Product product : db.productDao().getAll()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("product_id", product.id + "");
            map.put("product_name", product.name);
            map.put("product_price", format(product.price));
            map.put("product_rating", star(product.rating));
            map.put("product_category", "" + product.rating);
            map.put("product_description", product.description);
            list.add(map);
        }

        return list;
    }
}