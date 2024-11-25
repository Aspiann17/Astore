package id.my.aspian.astore;

import static id.my.aspian.astore.Utils.format;
import static id.my.aspian.astore.Utils.star;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = "products")
public class Product {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "price")
    public int price;

    @ColumnInfo(name = "rating")
    public int rating;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "description")
    public String description;

    public static ArrayList<Map<String, String>> get_all(StoreDatabase db) {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        List<Product> product_data = db.productDao().getAll();

        for (Product product : product_data) {
            HashMap<String, String> map = new HashMap<>();
            map.put("product_id", product.id + "");
            map.put("product_name", product.name);
            map.put("product_price", format(product.price));
            map.put("product_rating", star(product.rating));
            map.put("product_category", String.valueOf(product.rating));
            map.put("product_description", product.description);
            list.add(map);
        }

        return list;
    }

    public static ArrayList<Map<String, String>> get_all(StoreDatabase db, String category) {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        List<Product> product_data = db.productDao().get(category);

        for (Product product : product_data) {
            list.add(new HashMap<>() {{
                put("product_id", String.valueOf(product.id));
                put("product_name", product.name);
                put("product_price", format(product.price));
                put("product_rating", star(product.rating));
                put("product_category", String.valueOf(product.rating));
                put("product_description", product.description);
            }});
        }

        return list;
    }
}