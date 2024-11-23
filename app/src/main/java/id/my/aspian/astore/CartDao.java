package id.my.aspian.astore;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {
    @Query("SELECT * FROM carts")
    List<Cart> getAll();

    @Insert
    void insert(Cart cart);

    @Update
    void update(Cart cart);
}