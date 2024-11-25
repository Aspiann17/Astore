package id.my.aspian.astore;

import static androidx.room.ForeignKey.CASCADE;

import static id.my.aspian.astore.Utils.format;
import static id.my.aspian.astore.Utils.star;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(
    tableName = "carts",
    foreignKeys = {
        @ForeignKey(entity = Product.class, parentColumns = "id", childColumns = "product_id", onDelete = CASCADE, onUpdate = CASCADE),
        // @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id", onDelete = CASCADE, onUpdate = CASCADE)
    }
)
public class Cart {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "product_id")
    public int product_id;

    // @ColumnInfo(name = "user_id")
    // public int user_id;

    @ColumnInfo(name = "quantity")
    public int quantity;

    public static ArrayList<Map<String, String>> get_all(StoreDatabase db) {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        List<Cart> cart_data = db.cartDao().getAll();

        for (int i = 0; i < cart_data.size(); i++) {
            Cart cart = cart_data.get(i);
            Product product = db.productDao().get(cart.product_id);

            list.add(new HashMap<>() {{
                put("cart_id", String.valueOf(cart.id));
                put("product_name", product.name);
                put("product_price", format(product.price));
                put("product_amount", String.valueOf(cart.quantity));
                put("total_price", format((long) cart.quantity * product.price));
            }});
        }

        return list;
    }
}