package com.example.android.peakfresh.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.peakfresh.R;
import com.example.android.peakfresh.UpdateProductTask;
import com.example.android.peakfresh.Utility;
import com.example.android.peakfresh.data.ProductColumns;
import com.example.android.peakfresh.data.ProductContentProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by Warren on 9/8/2016.
 */
public class Detail_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener {
    private static final String[] DETAIL_COLUMNS = {ProductColumns.PRODUCT_NAME,
            ProductColumns.PRODUCT_ICON, ProductColumns.PRODUCT_EXPIRATION_DATE,
            ProductColumns.PRODUCT_EXPIRATION_DATE};
    private static final int  REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_REQUEST_CODE = 2;
    private Context mContext;
    private int mProduct_Id;
    ImageView mImageView;
    TextView mProduct_title, mExpirationSummary, mExpirationDate;
    ImageButton mCameraButton, mTitleButton;
    Button mCalendarButton, mDateButton;
    Spinner mCategorySpinner;
    private static String[] mProduct_ID_Array;
    private final static int LOADER_ID = 1;
    public final static String PRODUCT_ID_KEY = "Product_Id";
    static final String CURRENT_PHOTO_PATH = "currentPhotoPath";
    private String mCurrentPhotoPath;
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String IMAGE_POSITION = "image_position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        //restore photo image path
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(CURRENT_PHOTO_PATH) != null){
                mCurrentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH);
            }
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int position = getActivity().getIntent().getIntExtra(IMAGE_POSITION, 0);
            mImageView.setTransitionName(EXTRA_IMAGE + position);
            ViewCompat.setTransitionName(mImageView, EXTRA_IMAGE + position);
            Log.d("Set Transition name", mImageView.getTransitionName());
        }
        mProduct_title = (TextView) rootView.findViewById(R.id.product_title_detail);
        mTitleButton = (ImageButton) rootView.findViewById(R.id.product_title_button);
        mTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mContext).title("Name Product")
                        .content("Name the product")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Enter product name", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                UpdateProductTask updateProductTask = new UpdateProductTask(mContext,
                                        mProduct_ID_Array, input.toString(), ProductColumns.PRODUCT_NAME);
                                updateProductTask.execute();
                            }
                        })
                        .show();
            }
        });
        mExpirationDate = (TextView) rootView.findViewById(R.id.product_expiration_date_detail);
        mDateButton = (Button) rootView.findViewById(R.id.expiration_date_button);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(Detail_Fragment.this, 0);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        mExpirationSummary = (TextView) rootView.findViewById(R.id.expiration_summary);
        mCameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
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
        mCalendarButton = (Button) rootView.findViewById(R.id.add_to_calendar_button);
//        mCategorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            int adjMonth = month + 1;

            String newDate = adjMonth + "/" + day + "/" + year;
            updateDate(newDate);
        }
    };

    public void updateDate(String expirationDate){

    }

    //Handles thumbnail from taking picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        mImageView.setImageBitmap(bitmap);


    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);

        UpdateProductTask updateProductTask = new UpdateProductTask(getContext(), mProduct_ID_Array,
                contentUri.toString(), ProductColumns.PRODUCT_ICON);
        updateProductTask.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //save image filepath if needed
        if (mCurrentPhotoPath != null) {
            outState.putString(CURRENT_PHOTO_PATH, mCurrentPhotoPath);
        }
        super.onSaveInstanceState(outState);
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
            mExpirationSummary.setText(Utility.getExpirationDateDescription(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE))));

            Log.v("onLoadFinished", data.getString(data.getColumnIndex(ProductColumns.PRODUCT_NAME)) + data.getString(data.getColumnIndex(ProductColumns.PRODUCT_EXPIRATION_DATE)));

            Glide.with(getContext())
                    .load(data.getString(data.getColumnIndex(ProductColumns.PRODUCT_ICON)))
                    .placeholder(R.mipmap.ic_launcher)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                scheduleStartPostponedTransition(mImageView);
                                Log.v("OnLoadFinished", "scheduleStartPostponedTransition triggered");
                            }
                            return false;
                        }
                    })
                    .into(mImageView);
        }
        Log.v("OnLoadFinished", "Finished method");
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Log.d("StartTransition", "Transition started");
                            getActivity().supportStartPostponedEnterTransition();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        int adjMonth = month + 1;

        String newDate = adjMonth + "/" + day + "/" + year;
        UpdateProductTask updateProductTask = new UpdateProductTask(getContext(), mProduct_ID_Array,
                newDate, ProductColumns.PRODUCT_EXPIRATION_DATE);
        updateProductTask.execute();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
