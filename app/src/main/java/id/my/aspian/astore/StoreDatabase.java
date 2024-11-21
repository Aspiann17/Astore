package id.my.aspian.astore;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class}, version = 1)
public abstract class StoreDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}