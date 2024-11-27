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
    Product get(int id);

    @Query("SELECT * FROM products WHERE category = :category")
    List<Product> get(String category);

    @Query("SELECT * FROM products WHERE name LIKE :name")
    List<Product> get_all(String name);

    @Query("SELECT COUNT(*) FROM products WHERE category = :category")
    int count(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserts(Product... products);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Product product);

    @Query("DELETE FROM products WHERE id = :id")
    void delete(int id);
}