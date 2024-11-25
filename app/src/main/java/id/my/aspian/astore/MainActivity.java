package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.execute;
import static id.my.aspian.astore.Utils.toast;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    StoreDatabase db;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Menu bottom_menu;

    // Option
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.admin_mode) {
            editor.putString("role", "admin");
            bottom_menu.findItem(R.id.nav_cart).setVisible(false);
            // bottom_menu.findItem(R.id.nav_order).setVisible(false);
            bottom_menu.findItem(R.id.nav_profile).setVisible(false);
        } else if (item_id == R.id.user_mode) {
            editor.putString("role", "user");
            bottom_menu.findItem(R.id.nav_cart).setVisible(true);
//            bottom_menu.findItem(R.id.nav_order).setVisible(true);
            bottom_menu.findItem(R.id.nav_profile).setVisible(true);
        } else if (item_id == R.id.delete_cart_data) {
            execute(() -> db.cartDao().delete_all());
        }

        editor.apply();

        return true;
    }
    // end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            // Bottom diatur ke 0 agar bottom navigation memiliki ukuran yang sesuai dengan kemauan.
            return insets;
        });

        db = Room.databaseBuilder(getApplicationContext(), StoreDatabase.class, "store").build();

        // Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment()).commit();
        // end

        // Bottom Navigation
        BottomNavigationView bottom_nav = findViewById(R.id.bottom_navigation);
        bottom_nav.setOnItemSelectedListener(item -> {
            int item_id = item.getItemId();

            if (item_id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment()).commit();
                return true;
            } else if (item_id == R.id.nav_cart) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CartFragment()).commit();
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
        bottom_menu = bottom_nav.getMenu();
        // end

        // Preferences
        preferences = getSharedPreferences("session", MODE_PRIVATE);
        editor = preferences.edit();
        // end
    }
}