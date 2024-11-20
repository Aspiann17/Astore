package id.my.aspian.astore;

import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            // Bottom diatur ke 0 agar bottom navigation memiliki ukuran yang sesuai dengan kemauan.
            return insets;
        });

        // Status Bar
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        // end

        // Fragment init
        FrameLayout frame_layout = findViewById(R.id.main_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment()).commit();
        // end

        // Bottom Navigation
        BottomNavigationView bottom_nav = findViewById(R.id.bottom_navigation);
        bottom_nav.setOnItemSelectedListener(item -> {
            int item_id = item.getItemId();

            if (item_id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment()).commit();
                return true;
            } else if (item_id == R.id.nav_order) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new OrderFragment()).commit();
                return true;
            } else if (item_id == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new ProfileFragment()).commit();
                return true;
            }

            return false;
        });

        // List Category
//        ListView list_category = findViewById(R.id.list_category);
//        list_category.setOnItemClickListener((parent, view, position, id) -> {
//            String category_id = ((TextView) view.findViewById(R.id.category_id)).getText().toString();
//            toast(this, category_id);
//        });
        // end

//        tmp_data();
    }

//    public void tmp_data() {
//        ArrayList<HashMap<String, String>> list = new ArrayList<>();
//
//        for (int i = 1; i <= 10; i++) {
//            HashMap<String, String> map = new HashMap<>();
//            map.put("category_id", "" + i);
//            map.put("category_title", "Title" + i);
//            map.put("product_available", "0" + i);
//
//            list.add(map);
//        }
//
//        ((ListView) findViewById(R.id.list_category)).setAdapter(new SimpleAdapter(this, list, R.layout.list_category, new String[]{"category_id", "category_title", "product_available"}, new int[]{R.id.category_id, R.id.category_title, R.id.product_available}));
//    }
}