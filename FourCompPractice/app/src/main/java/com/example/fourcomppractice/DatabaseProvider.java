package com.example.fourcomppractice;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {

    public static final int VALUE_DIR = 0;

    public static final int VALUE_ITEM = 1;

    public static final String AUTHORITY = "com.example.fourcomppractice.provider";

    private UriMatcher mUriMatcher;

    private MyDatabaseHelper mDbHelper;

    public DatabaseProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "value", VALUE_DIR);
        mUriMatcher.addURI(AUTHORITY, "value/#", VALUE_ITEM);

    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mDbHelper = new MyDatabaseHelper(getContext(), "Calculator.db", null, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            case VALUE_DIR:
                cursor=db.query("Value", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VALUE_ITEM:
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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (mUriMatcher.match(uri)) {
            case VALUE_DIR:
            case VALUE_ITEM:
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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (mUriMatcher.match(uri)){
            case VALUE_DIR:
                updatedRows = db.update("Value", values, selection, selectionArgs);
                break;
            case VALUE_ITEM:
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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (mUriMatcher.match(uri)) {
            case VALUE_DIR:
                deletedRows = db.delete("Value", selection, selectionArgs);
                break;
            case VALUE_ITEM:
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
        switch (mUriMatcher.match(uri)) {
            case VALUE_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.fourcomppractice.provider.book";
            case VALUE_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.fourcomppractice.provider.book";
        }
        return null;
    }


}
