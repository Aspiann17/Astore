package id.my.aspian.astore;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {
    @Query("SELECT * FROM carts")
    List<Cart> getAll();

    @Query("SELECT * FROM carts WHERE product_id = :product_id")
    Cart get(int product_id);

    @Query("SELECT product_id FROM carts")
    List<Integer> getAllProductId();

    @Insert
    void insert(Cart cart);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Cart cart);

    @Query("DELETE FROM carts WHERE id = :cart_id")
    void delete(int cart_id);

    @Query("DELETE FROM carts WHERE id = :cart_id")
    void delete(String cart_id);

    @Query("DELETE FROM carts")
    void delete_all();
}