package com.example.maria.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maria.inventory.data.ProductContract;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductsAdapter extends CursorAdapter {

    public ProductsAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.name_view);
        TextView priceView = (TextView) view.findViewById(R.id.price_view);
        final TextView quantityView = (TextView) view.findViewById(R.id.quantity_view);
        TextView supplierView = (TextView) view.findViewById(R.id.supplier_view);
        ImageView product_image = (ImageView) view.findViewById(R.id.product_image);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY_AVAILABLE);
        int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE);
        int idIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);

        String productName=cursor.getString(nameColumnIndex);
        int productPrice=cursor.getInt(priceColumnIndex);
        final String productQuantity=cursor.getString(quantityColumnIndex);
        String productSupplier=cursor.getString(supplierColumnIndex);
        String productImage=cursor.getString(imageColumnIndex);
        final int id = cursor.getInt(idIndex);

        product_image.setImageBitmap(StringToBitMap(productImage));

        nameView.setText(productName);
        priceView.setText("Price: "+NumberFormat.getCurrencyInstance(Locale.getDefault()).format(productPrice));
        quantityView.setText("Quantity: "+String.valueOf(productQuantity));
        supplierView.setText("Supplier: "+productSupplier);

        final Button saleButton= (Button) view.findViewById(R.id.sale_button);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity=Integer.parseInt(productQuantity);
                if(quantity>0) {
                    quantityView.setText("Quantity: "+String.valueOf(quantity-1));
                    String selection = ProductContract.ProductEntry._ID + "= ?";
                    String[] selectionArgs = {Integer.toString(id)};
                    Uri updateURI = Uri.withAppendedPath(ProductContract.ProductEntry.CONTENT_URI, Integer.toString(id));
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY_AVAILABLE, quantity-1);
                    int count = context.getContentResolver().update(updateURI, values, selection, selectionArgs);
                }
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}