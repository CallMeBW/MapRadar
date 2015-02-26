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
public class SuggestionsDatabase {
    public static final String DB_SUGGESTION = "SUGGESTION_DB";
    public final static String TABLE_SUGGESTION = "SUGGESTION_TB";
    public final static String FIELD_ID = "_id";
    public final static String FIELD_SUGGESTION = "suggestion";
    private SQLiteDatabase db;
    private Helper helper;

    public SuggestionsDatabase(Context context) {
        helper = new Helper(context, DB_SUGGESTION, null, 1);
        db = helper.getWritableDatabase();
    }

    public long insertSuggestion(String text) {
        ContentValues values = new ContentValues();
        values.put(FIELD_SUGGESTION, text);
        return db.insert(TABLE_SUGGESTION, null, values);
    }

    public Cursor getSuggestions(String text, int maxSuggestions) {
        // retrieve all recent searches starting with the text.
        // do not return searches that equal the text since the user already typed that in.
        return db.query(TABLE_SUGGESTION, new String[]{FIELD_ID, FIELD_SUGGESTION},
                FIELD_SUGGESTION + " LIKE '" + text + "%' AND " + FIELD_SUGGESTION + " <> '" + text + "'",
                null, null, null,
                FIELD_SUGGESTION+" ASC", String.valueOf(maxSuggestions));
    }

    public void limitSizeTo(int maxCount) {
        Cursor cursor = db.query(TABLE_SUGGESTION, new String[]{FIELD_ID},
                null, null, null, null, null);
        if (cursor.getCount() <= maxCount) {
            return;
        }
        // TODO delete alll rows that exceed the max row count
//        db.delete(TABLE_SUGGESTION, FIELD_ID + "=?", new String[] { "" });

    }

    private class Helper extends SQLiteOpenHelper {
        public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_SUGGESTION + " (" +
                    FIELD_ID + " integer primary key autoincrement, " + FIELD_SUGGESTION + " TEXT UNIQUE ON CONFLICT REPLACE" + ");");
            Log.d("SUGGESTION", "DB CREATED");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}