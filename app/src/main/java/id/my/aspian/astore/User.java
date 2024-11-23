package id.my.aspian.astore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "hashword")
    public String hashword;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    public String created_at;
}