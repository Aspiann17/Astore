package id.my.aspian.astore;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class, Cart.class}, version = 1, exportSchema = false)
public abstract class StoreDatabase extends RoomDatabase {
    public abstract ProductDao productDao();

    public abstract CartDao cartDao();
}