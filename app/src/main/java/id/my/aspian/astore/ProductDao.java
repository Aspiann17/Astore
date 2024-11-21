package id.my.aspian.astore;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE id = :id")
    List<Product> get(int id);

    @Query("SELECT * FROM products WHERE category = :category")
    List<Product> get(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserts(Product... products);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);
}