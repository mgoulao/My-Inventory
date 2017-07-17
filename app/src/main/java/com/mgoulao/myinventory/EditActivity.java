package com.mgoulao.myinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mgoulao.myinventory.data.InventoryContract.InventoryEntry;
import com.mgoulao.myinventory.data.InventoryDbHelper;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "MG";

    EditText nameEditText;
    EditText priceEditText;
    EditText quantityEditText;
    Button changePictureButton;
    ImageButton quantityAdd;
    ImageButton quantityRemove;
    ImageView productImage;

    String imageUriString;
    String name;
    Uri path;

    private boolean mproductHasChanged = false;

    /**
     * Final for the image intent request code
     */
    private final static int SELECT_PICTURE = 200;
    private static final int PRODUCT_LOADER = 0;
    private static final int IMAGE_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setElevation(0);

        Intent intent = getIntent();
        path = intent.getData();

        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        changePictureButton = (Button) findViewById(R.id.change_picture);
        productImage = (ImageView) findViewById(R.id.product_image);
        quantityAdd = (ImageButton) findViewById(R.id.quant_add);
        quantityRemove = (ImageButton) findViewById(R.id.quant_remove);


        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        quantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = quantityEditText.getText().toString();
                int quantityInt = Integer.parseInt(quantity) + 1;
                quantityEditText.setText(String.valueOf(quantityInt));
            }
        });

        quantityRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = quantityEditText.getText().toString();
                int quantityInt = Integer.parseInt(quantity) - 1;
                if (quantityInt > 0) {
                    quantityEditText.setText(String.valueOf(quantityInt));
                } else {
                    Toast.makeText(EditActivity.this, getString(R.string.toast_negative_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mproductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            try {
                Uri selectedImageUri = data.getData();
                imageUriString = selectedImageUri.toString();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                productImage.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        double priceDouble;
        if (priceString.equals("")) {
            priceDouble = Double.parseDouble("0");
        } else {
            priceDouble = Double.parseDouble(priceString);
        }
        String quantityString = quantityEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityString);
        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        /*if (path == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
                TextUtils.isEmpty(weightString) && mGender == productEntry.GENDER_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }*/

        // Create database helper
        InventoryDbHelper mDbHelper = new InventoryDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRICE, priceDouble);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantityInt);
        values.put(InventoryEntry.COLUMN_IMAGE, imageUriString);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            Toast.makeText(this, "Error saving the product", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        if (path != null) {
            int rowsDeleted = getContentResolver().delete(path, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.edit_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.edit_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mproductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
