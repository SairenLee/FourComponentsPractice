package com.example.fourcomppractice;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {

    public static final int Value_DIR = 0;

    public static final int Value_ITEM = 1;

    public static final String AUTHORITY = "com.example.fourcomppractice.provider";

    private static UriMatcher uriMatcher;

    private static MyDatabaseHelper dbHelper;

    public DatabaseProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "value", Value_DIR);
        uriMatcher.addURI(AUTHORITY, "value/#", Value_ITEM);

    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbHelper = new MyDatabaseHelper(getContext(), "Calculator.db", null, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case Value_DIR:
                cursor=db.query("Value", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case Value_ITEM:
                String valueId = uri.getPathSegments().get(1);
                cursor = db.query("Value", projection, "id = ?", new String[]{valueId}, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)) {
            case Value_DIR:
            case Value_ITEM:
                long newValueId = db.insert("Value", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/value/" + newValueId);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)){
            case Value_DIR:
                updatedRows = db.update("Value", values, selection, selectionArgs);
                break;
            case Value_ITEM:
                String newValueId = uri.getPathSegments().get(1);
                updatedRows = db.update("Value", values, "id = ?", new String[]{newValueId});
                break;
            default:
                break;

        }
        return updatedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case Value_DIR:
                deletedRows = db.delete("Value", selection, selectionArgs);
                break;
            case Value_ITEM:
                String newValueId = uri.getPathSegments().get(1);
                deletedRows = db.delete("Value", "id = ?", new String[]{newValueId});
                break;
            default:
                break;
        }
        return  deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case Value_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.fourcomppractice.provider.book";
            case Value_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.fourcomppractice.provider.book";
        }
        return null;
    }


}
