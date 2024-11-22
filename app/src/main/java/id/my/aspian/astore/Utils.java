package id.my.aspian.astore;

import android.content.Context;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Utils {
    private static final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void toast(Context context, String... messages) {
        for (String message : messages) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static String format(long number) {
        return formatter.format(number).replace(",00", "");
    }

    public static Future<?> execute(Runnable action) {
        return executor.submit(action);
    }

    public static String star(int rating) {
        return rating + " ★";
    }
}