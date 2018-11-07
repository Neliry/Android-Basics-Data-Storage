package com.example.maria.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.maria.inventory.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDbHelper mDbHelper;
    public static final String PATH_PRODUCTS = "products";
    private static final int PRODUCTS =100;
    private static final int PRODUCTS_ID=101;

    private static final UriMatcher sUriMatcher= new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS +"/#", PRODUCTS_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper=new ProductDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database=mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match=sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                cursor=database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection=ProductContract.ProductEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        String name = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null || name.equals("")) {
                return null;
        }

        Integer price = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price<0) {
            return null;
        }

        Integer quantity = contentValues.getAsInteger(ProductEntry.COLUMN_QUANTITY_AVAILABLE);
        if (quantity != null && quantity < 0) {
            contentValues.put(ProductEntry.COLUMN_QUANTITY_AVAILABLE, 0);
            return null;
        }

        String supplier = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplier == null || name.equals("")) {
            return null;
        }

        long id = database.insert(ProductEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null || name.equals("")) {
                return 0;
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price< 0) {
                return 0;
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_QUANTITY_AVAILABLE)) {
            Integer quantity = contentValues.getAsInteger(ProductEntry.COLUMN_QUANTITY_AVAILABLE);
            if (quantity != null && quantity < 0) {
                return 0;
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String supplier = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            if (supplier == null || supplier.equals("")) {
                return 0;
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}