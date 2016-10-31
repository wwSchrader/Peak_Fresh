package com.example.android.peakfresh.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.peakfresh.InsertProductTask;
import com.example.android.peakfresh.R;
import com.example.android.peakfresh.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Warren on 9/21/2016.
 * This activity is for adding new products. It will appear as a dialog on large screen devices.
 */

public class NewProduct_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener,  AddCategoryDialogFragment.CategoryDialogListener {
    private Context mContext;
    private String newDate = "null";
    private static final int  REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_REQUEST_CODE = 2;
    private static final String CURRENT_PHOTO_PATH = "currentPhotoPath";
    private static final String CURRENT_EXP_DATE = "currentExpDate";
    public static final String ADD_PRODUCT_KEY = "addProduct";

    ImageView newProductImage;
    Button newProductCameraButton, newProductDateButton;
    EditText productTitleEditTextField;
    TextView newProductExpirationDateTextView;
    Spinner mCategorySpinner;
    private String mCurrentPhotoPath, mCategory;
    private boolean onItemSelectedListenerFlag;
    private String[] mProduct_ID_Array;
    private int spinnerSelection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.large_layout)){
            //if screen is large, display as a dialog box

            // From: http://stackoverflow.com/questions/11425020/actionbar-in-a-dialogfragment
            this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = this.getWindow().getAttributes();
            params.alpha = 1.0f;
            params.dimAmount = 0.5f;
            this.getWindow().setAttributes(params);

            // This sets the window size, while working around the IllegalStateException thrown by ActionBarView
            this.getWindow().setLayout(850,850);
        }


        setContentView(R.layout.new_product_activity);
        mContext = this;

        //setup toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //setup up button on toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        newProductImage = (ImageView) findViewById(R.id.new_product_image_detail);

        newProductCameraButton = (Button) findViewById(R.id.new_product_camera_button_activity);
        newProductCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mContext.checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE);
                    } else {
                        takePicture();
                    }
                } else {
                    takePicture();
                }

            }
        });
        productTitleEditTextField = (EditText) findViewById(R.id.edit_text_product_title_activity);
        newProductDateButton = (Button) findViewById(R.id.new_product_date_button_activity);
        newProductDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        newProductExpirationDateTextView = (TextView) findViewById(R.id.new_product_expiration_date_activity);

        mCategorySpinner = (Spinner) findViewById(R.id.new_product_category_spinner);
        //setup the view for the category spinner
        ArrayList<String> categoryArrayList = Utility.loadCategoryArray(Utility.CATEGORY_ARRAY, mContext, "add_screen");
        ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.category_spinner_item, categoryArrayList);
        //set flag to false since onItemSelected is triggered when first set
        onItemSelectedListenerFlag = false;
        adapter.setDropDownViewResource(R.layout.category_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setOnItemSelectedListener(this);
        spinnerSelection = 0;

        if (savedInstanceState != null){
            //restore any images or dates
            if (savedInstanceState.getString(CURRENT_PHOTO_PATH) != null){
                mCurrentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH);
                Glide.with(mContext)
                        .load(mCurrentPhotoPath)
                        .placeholder(R.mipmap.ic_launcher)
                        .fitCenter()
                        .into(newProductImage);
            }
            if (savedInstanceState.getString(CURRENT_EXP_DATE) != null){
                newDate = savedInstanceState.getString(CURRENT_EXP_DATE);
                newProductExpirationDateTextView.setText(newDate);
            }
        } else {
            mCurrentPhotoPath = Utility.resourceToUri(mContext, R.drawable.icon_splash).toString();
            Glide.with(mContext)
                    .load(mCurrentPhotoPath)
                    .placeholder(R.mipmap.ic_launcher)
                    .fitCenter()
                    .into(newProductImage);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_product_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_discard:
                // User chose the to discard new product, return to main activity
                finish();
                return true;

            case R.id.action_accept:

                if (productTitleEditTextField.getText().toString() == null){
                    Toast.makeText(mContext, "Please fill out a name!", Toast.LENGTH_SHORT).show();
                    return  false;
                } else if (mCategory == null){
                    Toast.makeText(mContext, "Please choose a category!", Toast.LENGTH_SHORT).show();
                    return  false;
                } else if (newDate.equals("null")){
                    Toast.makeText(mContext, "Please choose an expiration date!", Toast.LENGTH_SHORT).show();
                    return  false;
                }

                //User chose to accept new product, save in db and return to main activity
                InsertProductTask insertProductTask = new InsertProductTask(this, ADD_PRODUCT_KEY);
                insertProductTask.execute(
                        productTitleEditTextField.getText().toString(),
                        mCategory,
                        newDate,
                        mCurrentPhotoPath
                );
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoPath != null){
            outState.putString(CURRENT_PHOTO_PATH, mCurrentPhotoPath);
        }
        if (newDate != null){
            outState.putString(CURRENT_EXP_DATE, newDate);
        }
        super.onSaveInstanceState(outState);
    }

    public void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensuring camera activity can handle intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            //Create file where photo should go
            File photoFile = null;
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                Log.e("External storage", "External Storage not available");
                return;
            }

            try {
                photoFile = Utility.createImageFile(mContext);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex){
                Log.e("createImageFile", ex.getMessage());
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(mContext, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("OnActivityResult", "onActivityResultTrigged");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            setPic();
            galleryAddPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 500;
        int targetH = 500;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        newProductImage.setImageBitmap(bitmap);

        Glide.with(mContext)
                .load(mCurrentPhotoPath)
                .placeholder(R.mipmap.ic_launcher)
                .fitCenter()
                .into(newProductImage);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.v("onDateSet", "onDateSetTriggered");

        int adjMonth = month + 1;

        //update date string with date selected from datepicker fragment
        newDate = adjMonth + "/" + dayOfMonth + "/" + year;
        newProductExpirationDateTextView.setText(newDate);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (onItemSelectedListenerFlag){
            if(parent.getItemAtPosition(position).toString().equals("Custom")){
                //launch fragment to add new category
                AddCategoryDialogFragment categoryDialogFragment = new AddCategoryDialogFragment();
                categoryDialogFragment.show(getSupportFragmentManager(), "newCategory");

            }else {
                mCategory = parent.getItemAtPosition(position).toString();
                spinnerSelection = parent.getSelectedItemPosition();
            }
            Log.v("onItemSelected", "triggered");
        } else {
            //if it's the first time, set flag to true to run code next time
            onItemSelectedListenerFlag = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDialogPositiveClick(String category) {
        mCategory = category;
        Utility.addItemToCategoryArray(Utility.CATEGORY_ARRAY, mContext, category);
        ArrayList<String> categoryArrayList = Utility.loadCategoryArray(Utility.CATEGORY_ARRAY, mContext, "add_screen");
        ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.category_spinner_item, categoryArrayList);
        //set flag to false since onItemSelected is triggered when first set
        onItemSelectedListenerFlag = false;
        adapter.setDropDownViewResource(R.layout.category_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setSelection(adapter.getPosition(category));
        mCategorySpinner.setOnItemSelectedListener(this);
        Toast.makeText(mContext, "Category Added!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick() {
        mCategorySpinner.setSelection(spinnerSelection);
        Toast.makeText(mContext, "Action Canceled", Toast.LENGTH_SHORT).show();
    }
}
