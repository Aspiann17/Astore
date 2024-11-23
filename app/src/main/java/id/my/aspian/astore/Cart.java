package id.my.aspian.astore;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

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
}