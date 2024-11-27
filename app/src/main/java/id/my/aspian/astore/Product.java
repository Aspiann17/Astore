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

    @ColumnInfo(name = "stock", defaultValue = "0")
    public int stock;

    @ColumnInfo(name = "price", defaultValue = "0")
    public int price;

    @ColumnInfo(name = "rating", defaultValue = "0")
    public int rating;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "description")
    public String description;

    public static ArrayList<Map<String, String>> get_all(StoreDatabase db, String category) {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        List<Product> product_data = db.productDao().get(category);

        for (Product product : product_data) {
            Cart product_on_cart = db.cartDao().get(product.id);
            int stock = product.stock;

            if (product_on_cart != null) {
                stock -= product_on_cart.quantity;
            }

            String final_stock = String.valueOf(stock);
            list.add(new HashMap<>() {{
                put("product_id", String.valueOf(product.id));
                put("product_stock", final_stock);
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