package com.example.android.peakfresh.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.peakfresh.R;
import com.example.android.peakfresh.Utility;

import java.io.File;
import java.io.IOException;

/**
 * Created by Warren on 9/21/2016.
 */

public class NewProduct_Activity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {
    private Context mContext;
    private String newDate = "null";
    private static final int  REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_REQUEST_CODE = 2;

    ImageView newProductImage;
    Button newProductCameraButton, newProductDateButton;
    EditText productTitleEditTextField;
    TextView newProductExpirationDateTextView;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_product_activity);
        mContext = this;

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
                DialogFragment newFragment = new DatePickerForActivitiesFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        newProductExpirationDateTextView = (TextView) findViewById(R.id.new_product_expiration_date_activity);
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
        int targetW = 1000;
        int targetH = 1000;

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

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        newProductImage.setImageBitmap(bitmap);
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
}
