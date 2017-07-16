package com.mgoulao.myinventory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
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
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);

        String name = cursor.getString(nameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        String priceText = price + " €";
        String imageUriString = cursor.getString(imageColumnIndex);
        Log.d(TAG, imageUriString);
        Uri imageUri = Uri.parse(imageUriString);
        Log.d(TAG, "" + imageUri);

        nameTextView.setText(name);
        quantityTextView.setText(view.getResources().getString(R.string.edit_quantity) + ": " + quantity);
        priceTextView.setText(priceText);
        // Set the image in ImageView
        new SetProductImage(imageView, view.getContext()).execute(imageUri);


    }

    private class SetProductImage extends AsyncTask<Uri, Void, Bitmap> {
        ImageView mImageView;
        Context mContext;

        private SetProductImage(ImageView imageView, Context context) {
            mImageView = imageView;
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(Uri... params) {
            Uri imageUri = params[0];
            Bitmap mIcon11 = null;
            try {
                mIcon11 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(result);
        }
    }

}
