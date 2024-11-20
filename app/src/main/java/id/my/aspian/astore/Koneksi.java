package id.my.aspian.astore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Koneksi extends SQLiteOpenHelper {
    private static final String DB_NAME = "astore";
    private static final int DB_VERSION = 1;

    public Koneksi(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {}
}
