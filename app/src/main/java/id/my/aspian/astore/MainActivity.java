package id.my.aspian.astore;

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
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    StoreDatabase db;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Menu bottom_menu;

    // Option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        menu.findItem(R.id.delete_cart_data).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.admin_mode) {
            editor.putString("role", "admin");
            bottom_menu.findItem(R.id.nav_cart).setVisible(false);
            bottom_menu.findItem(R.id.nav_profile).setVisible(false);
        } else if (item_id == R.id.user_mode) {
            editor.putString("role", "user");
            bottom_menu.findItem(R.id.nav_cart).setVisible(true);
            bottom_menu.findItem(R.id.nav_profile).setVisible(true);
        }

        editor.apply();

        return super.onOptionsItemSelected(item);
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

        // Navigation
        BottomNavigationView bottom_nav = findViewById(R.id.bottom_navigation);
        bottom_nav.setOnItemSelectedListener(item -> {

            int item_id = item.getItemId();

            if (item_id == R.id.nav_home) {
                move_fragment(new HomeFragment());
                return true;
            } else if (item_id == R.id.nav_cart) {
                move_fragment(new CartFragment());
                return true;
            } else if (item_id == R.id.nav_list_all) {
                return false;
            } else if (item_id == R.id.nav_profile) {
                move_fragment(new ProfileFragment());
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

    private void move_fragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }
}