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
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        // end

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
            } else if (item_id == R.id.nav_order) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new OrderFragment()).commit();
                return true;
            } else if (item_id == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new ProfileFragment()).commit();
                return true;
            }

            return false;
        });
        // end
    }
}