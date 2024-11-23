package id.my.aspian.astore;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class, User.class, Cart.class}, version = 2, exportSchema = false)
public abstract class StoreDatabase extends RoomDatabase {
    public abstract ProductDao productDao();

    public abstract UserDao userDao();

    public abstract CartDao cartDao();
}