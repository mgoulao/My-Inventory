package com.mgoulao.myinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mgoulao.myinventory.data.InventoryContract.InventoryEntry;

/**
 * Created by msilv on 7/15/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    private static final String TAG = "MG";

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);

        String name = cursor.getString(nameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        String priceText = price + " €";

        nameTextView.setText(name);
        quantityTextView.setText(view.getResources().getString(R.string.edit_quantity) + ": " + quantity);
        priceTextView.setText(priceText);


    }
}
