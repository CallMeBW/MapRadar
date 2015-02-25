package de.ip.mapradar.provider;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 19.02.2015            <br></code>
 * Description:                    <br>
 */
public class FavoritesDatabase {
    public static final String DB_NAME = "FAVOURITES_DB";
    public final static String TABLE_FAVOURITES = "FAVS_TB";
    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_PLACE_ID = "place_id";
    private SQLiteDatabase db;

    public FavoritesDatabase(Context context) {
        Helper helper = new Helper(context, DB_NAME, null, 1);
        db = helper.getWritableDatabase();
    }

    public long insertFavourite(int place_ID) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_ID, place_ID);
        return db.insert(TABLE_FAVOURITES, null, values);
    }

    public Cursor getFavourites() {
        return db.query(TABLE_FAVOURITES, new String[]{COLUMN_PLACE_ID},
                null, null, null, null, null);
    }

    private class Helper extends SQLiteOpenHelper {
        public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_FAVOURITES + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " + COLUMN_PLACE_ID + " integer UNIQUE ON CONFLICT REPLACE" + ");");
            Log.d("SUGGESTION", "DB CREATED");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}