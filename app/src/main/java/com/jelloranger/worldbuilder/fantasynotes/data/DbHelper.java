package com.jelloranger.worldbuilder.fantasynotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.jelloranger.worldbuilder.fantasynotes.data.model.Entity;
import com.jelloranger.worldbuilder.fantasynotes.data.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "EntityDatabase";

    private static final int DATABASE_VERSION = 1;

    // Table
    private static final String ENTITY_TABLE = "entitytable";

    // Columns
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_BITMAP = "bitmap";
    private static final String COLUMN_CONNECTIONS = "connections";

    private static final String CREATE_ENTITY_TABLE = "CREATE TABLE " + ENTITY_TABLE +
            "(" +
            COLUMN_ID + " TEXT PRIMARY KEY, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_DESCRIPTION+ " TEXT, " +
            COLUMN_TYPE + " TEXT, " +
            COLUMN_BITMAP + " BLOB" +
            ");";

    private static DbHelper sDbHelper;

    public static synchronized DbHelper getInstance(final Context context) {

        if (sDbHelper == null) {
            sDbHelper = new DbHelper(context.getApplicationContext());
        }
        return sDbHelper;
    }

    private DbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ENTITY_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
        if (oldVersion != newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ENTITY_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

    public void insertEntity(final Entity entity) {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            final ContentValues values = new ContentValues();
            values.put(COLUMN_ID, entity.getId());
            values.put(COLUMN_NAME, entity.getName());
            values.put(COLUMN_DESCRIPTION, entity.getDescription());
            values.put(COLUMN_TYPE, entity.getType().toString().toUpperCase());
            values.put(COLUMN_BITMAP, DbUtils.getBytes(entity.getImage()));
            db.insertOrThrow(ENTITY_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void updateEntity(final Entity entity) {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            final ContentValues values = new ContentValues();
            values.put(COLUMN_ID, entity.getId());
            values.put(COLUMN_NAME, entity.getName());
            values.put(COLUMN_DESCRIPTION, entity.getDescription());
            values.put(COLUMN_TYPE, entity.getType().toString().toUpperCase());
            values.put(COLUMN_BITMAP, DbUtils.getBytes(entity.getImage()));
            db.update(ENTITY_TABLE, values, COLUMN_ID + " = ?", new String[]{entity.getId()});
            db.setTransactionSuccessful();
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<Entity> getEntities() {
        final List<Entity> entities = new ArrayList<>();
        final String query = "SELECT * FROM " + ENTITY_TABLE;

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    final Entity entity = new Entity();
                    entity.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                    entity.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                    entity.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                    entity.setType(Entity.Type.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))));
                    entity.setImage(DbUtils.getImage(cursor.getBlob(cursor.getColumnIndex(COLUMN_BITMAP))));
                    entities.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (final SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return entities;
    }

}
