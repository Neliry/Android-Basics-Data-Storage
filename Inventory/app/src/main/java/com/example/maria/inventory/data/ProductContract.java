package com.example.maria.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {
    private ProductContract() {}
    public static final String CONTENT_AUTHORITY = "com.example.android.maria.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

        /** Name of database table for pets */
        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME ="name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_QUANTITY_AVAILABLE = "quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public final static String COLUMN_PRODUCT_PICTURE = "picture";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

    }
}
