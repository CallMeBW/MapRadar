package de.bwirth.mapradar.provider;
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
public class CategoriesDatabase {
    public static final String DB_NAME = "GOOGLE_CATEGORY_DB";
    public final static String TABLE_NAME = "GOOGLE_CATEGORY_TB";
    public final static String COLUMN_CATEGORY = "category";
    public final static String COLUMN_IS_SELECTED_CATEGORY = "isselected";
    private SQLiteDatabase db;

    public CategoriesDatabase(Context context) {
        Helper helper = new Helper(context, DB_NAME, null, 1);
        db = helper.getWritableDatabase();
    }

    public long insertOrChangeCategory(String catName, boolean isFavourite) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, catName);
        values.put(COLUMN_IS_SELECTED_CATEGORY, isFavourite ? "TRUE" : "FALSE");
        return db.insert(TABLE_NAME, null, values);
    }

    public void insertIfNotAlready(String catName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, catName);
        final boolean favourite = isFavourite(catName);
        values.put(COLUMN_IS_SELECTED_CATEGORY, favourite ? "TRUE" : "FALSE");
        if(!favourite){
            db.insert(TABLE_NAME, null, values);
        }
    }

    public boolean isFavourite(String catName) {
        Cursor c = db.query(TABLE_NAME, new String[]{COLUMN_CATEGORY, COLUMN_IS_SELECTED_CATEGORY},
                COLUMN_CATEGORY + " = '" + catName + "'",null,null,null,null
        );
        if(c == null || c.getCount() != 1){
            return false;
        }
        c.moveToFirst();
        int col = c.getColumnIndex(COLUMN_IS_SELECTED_CATEGORY);
        return c.getString(col).equals("TRUE");
    }

    public Cursor getAllCategories() {
        return db.query(TABLE_NAME, new String[]{COLUMN_CATEGORY, COLUMN_IS_SELECTED_CATEGORY},
                null, null, null, null, COLUMN_CATEGORY + " ASC");
    }

    private class Helper extends SQLiteOpenHelper {
        public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_CATEGORY + " TEXT UNIQUE ON CONFLICT REPLACE," +
                    COLUMN_IS_SELECTED_CATEGORY + " BOOLEAN DEFAULT FALSE" +
                    ");");
            Log.i("SUGGESTION", "DB GOOGLE CATEGORIES CREATED");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}