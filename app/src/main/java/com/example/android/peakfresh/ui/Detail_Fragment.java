package com.example.android.peakfresh.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.peakfresh.R;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Warren on 9/8/2016.
 */
public class Detail_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String[] DETAIL_COLUMNS = {ProductColumns.PRODUCT_NAME,
            ProductColumns.PRODUCT_ICON, ProductColumns.PRODUCT_EXPIRATION_DATE,
            ProductColumns.PRODUCT_EXPIRATION_DATE};
    private static final int  REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_REQUEST_CODE = 2;
    private Detail_Fragment mContext;
    private int mProduct_Id;
    ImageView mImageView;
    TextView mProduct_title, mExpirationSummary, mExpirationDate;
    Button mCalendarButton, mCameraButton;
    Spinner mCategorySpinner;
    private static String[] mProduct_ID_Array;
    private final static int LOADER_ID = 1;
    public final static String PRODUCT_ID_KEY = "Product_Id";
    private String mCurrentPhotoPath;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mProduct_Id = arguments.getInt(PRODUCT_ID_KEY);
            mProduct_ID_Array = new String[]{Integer.toString(mProduct_Id)};
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.product_icon_detail);
        mProduct_title = (TextView) rootView.findViewById(R.id.product_title_detail);
        mExpirationDate = (TextView) rootView.findViewById(R.id.product_expiration_date_detail);
        mExpirationSummary = (TextView) rootView.findViewById(R.id.expiration_summary);
        mCameraButton = (Button) rootView.findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.CAMERA)
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
        mCalendarButton = (Button) rootView.findViewById(R.id.add_to_calendar_button);
//        mCategorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    //Handles thumbnail from taking picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            setPic();
            galleryAddPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
        }
    }

    public void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensuring camera activity can handle intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            //Create file where photo should go
            File photoFile = null;
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                Log.e("External storage", "External Storage not available");
                return;
            }

            try {
                photoFile = createImageFile();
            } catch (IOException ex){
                Log.e("createImageFile", ex.getMessage());
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

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
        mImageView.setImageBitmap(bitmap);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mProduct_Id != 0) {
            return new CursorLoader(
                    getActivity(),
                    ProductContentProvider.Products.PRODUCTS_URI,
                    DETAIL_COLUMNS,
                    ProductColumns._ID + " LIKE ?",
                    mProduct_ID_Array,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mProduct_title.setText(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_NAME)));
            mExpirationDate.setText(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE)));

            Log.v("onLoadFinished", data.getString(data.getColumnIndex(ProductColumns.PRODUCT_NAME)) + data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE)));

            Glide.with(getContext())
                    .load(data.getInt(data.getColumnIndex(ProductColumns.PRODUCT_ICON)))
                    .placeholder(R.mipmap.ic_launcher)
                    .fitCenter()
                    .into(mImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
