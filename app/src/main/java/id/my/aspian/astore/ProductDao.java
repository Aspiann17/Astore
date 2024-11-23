package id.my.aspian.astore;

import androidx.room.Dao;
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
    Product get(String id);

//    @Query("SELECT * FROM products WHERE category = :category")
//    List<Product> get(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserts(Product... products);

    @Update
    void update(Product product);

    @Query("DELETE FROM products WHERE id = :id")
    void delete(int id);
}